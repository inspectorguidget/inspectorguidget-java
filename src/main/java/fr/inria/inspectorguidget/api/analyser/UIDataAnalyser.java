package fr.inria.inspectorguidget.api.analyser;

import fr.inria.inspectorguidget.api.UIDataExtractor;
import fr.inria.inspectorguidget.api.processor.WidgetProcessor;
import fr.inria.inspectorguidget.data.ClassRef;
import fr.inria.inspectorguidget.data.Handler;
import fr.inria.inspectorguidget.data.HandlerInteraction;
import fr.inria.inspectorguidget.data.Interaction;
import fr.inria.inspectorguidget.data.Location;
import fr.inria.inspectorguidget.data.UICommand;
import fr.inria.inspectorguidget.data.UIData;
import fr.inria.inspectorguidget.data.Widget;
import fr.inria.inspectorguidget.data.WidgetBinding;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import spoon.reflect.declaration.CtClass;
import spoon.reflect.declaration.CtExecutable;

public class UIDataAnalyser implements UIDataExtractor {
	private final CommandAnalyser cmdAnalyser;
	private final WidgetProcessor widgetProc;
	CommandWidgetFinder finder;

	public UIDataAnalyser() {
		super();

		cmdAnalyser = new CommandAnalyser();
		widgetProc = new WidgetProcessor(true);
	}

	@Override
	public UIData extractUIData() {
		runAnalysis();
		return produceUIData();
	}

	private void runAnalysis() {
		cmdAnalyser.run();

		final InspectorGuidetAnalyser launcher = new InspectorGuidetAnalyser(
			Collections.singletonList(widgetProc), cmdAnalyser.getModelBuilder());
		launcher.process();

		finder = new CommandWidgetFinder(
			cmdAnalyser.getCommands().values().parallelStream().flatMap(s -> s.getCommands().stream()).collect(Collectors.toList()),
			widgetProc.getWidgetUsages());
		finder.process();
	}

	private UIData produceUIData() {
		final Map<Command, CommandWidgetFinder.WidgetFinderEntry> results = finder.getResults();

		return new UIData(results.entrySet().stream()
			.map(entry -> {
				// The method/lambda that contains the command
				final CtExecutable<?> exec = entry.getKey().getExecutable();

				// Reference to the class that contains the command
				final ClassRef cref = new ClassRef(exec.getBody().getPosition().getCompilationUnit().getFile().toString(),
					exec.getParent(CtClass.class).getSimpleName(), exec.getParent(CtClass.class).getQualifiedName());

				// Data on the handler
				final Handler handler = new Handler(exec.getReference().getDeclaringType().getQualifiedName(),
					new Location(exec.getBody().getPosition().getLine(), exec.getBody().getPosition().getEndLine(), cref));

				// Data on the interaction
				final Interaction interaction = new HandlerInteraction(Collections.singletonList(handler));

				// Data command
				final UICommand cmd = new UICommand(new Location(entry.getKey().getLineStart(), entry.getKey().getLineEnd(), cref), List.of(cref));

				// Data widgets
				final List<Widget> widgets = entry.getValue().getRegisteredWidgets()
					.stream()
					.map(w -> new Widget(w.widgetVar.getSimpleName(), w.widgetVar.getType().getSimpleName(),
						w.getUsagesWithCons()
							.stream()
							.map(u -> new Location(u.getPosition().getLine(), u.getPosition().getEndLine(),
								new ClassRef(u.getPosition().getCompilationUnit().getFile().toString(),
									u.getParent(CtClass.class).getSimpleName(),
									u.getParent(CtClass.class).getQualifiedName())))
							.collect(Collectors.toList())
					))
					.collect(Collectors.toList());

				// Data on the widget binding
				return new WidgetBinding(interaction, widgets, cmd);
			})
			.collect(Collectors.toList()));
	}

	@Override
	public void addInputResource(final String file) {
		cmdAnalyser.addInputResource(file);
	}

	@Override
	public void setSourceClasspath(final String... args) {
		cmdAnalyser.setSourceClasspath(args);
	}
}
