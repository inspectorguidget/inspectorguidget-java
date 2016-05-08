package fr.inria.diverse.torgen.inspectorguidget.analyser;

import fr.inria.diverse.torgen.inspectorguidget.helper.ClassMethodCallFilter;
import fr.inria.diverse.torgen.inspectorguidget.helper.ConditionalFilter;
import fr.inria.diverse.torgen.inspectorguidget.helper.SpoonHelper;
import fr.inria.diverse.torgen.inspectorguidget.processor.ClassListenerProcessor;
import fr.inria.diverse.torgen.inspectorguidget.processor.LambdaListenerProcessor;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import spoon.reflect.code.*;
import spoon.reflect.declaration.CtClass;
import spoon.reflect.declaration.CtElement;
import spoon.reflect.declaration.CtExecutable;
import spoon.reflect.declaration.CtMethod;
import spoon.reflect.reference.CtVariableReference;
import spoon.reflect.visitor.filter.VariableAccessFilter;

import java.util.*;
import java.util.logging.Level;
import java.util.stream.Collectors;

public class CommandAnalyser extends InspectorGuidetAnalyser {
	private final @NotNull ClassListenerProcessor classProc;
	private final @NotNull LambdaListenerProcessor lambdaProc;
	private final @NotNull Map<CtExecutable<?>, List<Command>> commands;

	public CommandAnalyser() {
		super(Collections.emptyList());

		commands = new IdentityHashMap<>();
		classProc=new ClassListenerProcessor();
		lambdaProc=new LambdaListenerProcessor();

		addProcessor(classProc);
		addProcessor(lambdaProc);
	}

	public @NotNull Map<CtExecutable<?>, List<Command>> getCommands() {
		return Collections.unmodifiableMap(commands);
	}

	@Override
	public void process() {
		super.process();
		final Map<CtClass<?>, List<CtMethod<?>>> methods = classProc.getAllListenerMethods();

		methods.entrySet().parallelStream().forEach(entry -> {
			if(entry.getValue().size()==1) {
				analyseSingleListenerMethod(Optional.of(entry.getKey()), entry.getValue().get(0));
			}else {
				analyseMultipleListenerMethods(entry.getKey(), entry.getValue());
			}
		});

		lambdaProc.getAllListenerLambdas().parallelStream().forEach(l -> analyseSingleListenerMethod(Optional.empty(), l));
		//TODO support methods of the class called in listener methods.
	}


	private void extractCommandsFromConditionalStatements(final @NotNull CtElement condStat, final @NotNull CtExecutable<?> listenerMethod) {
		//TODO analyse nested conditional statements
		//TODO analyse local variables used in the conditions and that use GUI event parameters.
		// Object foo = e.getSource();
		// if(foo instance JButton) {}
		List<Command> cmds = commands.get(listenerMethod);

		if(cmds==null) {
			cmds = new ArrayList<>();
			commands.put(listenerMethod, cmds);
		}

		if(condStat instanceof CtIf) {
			final CtIf ifStat = (CtIf) condStat;
			final CtBlock<?> elseStat =  ifStat.getElseStatement();
			cmds.add(new Command(((CtBlock<?>)ifStat.getThenStatement()).getStatements(),
									Collections.singletonList(ifStat.getCondition())));

			if(elseStat!=null) {
				//TODO create a command if it does not contain any other GUI conditional statement
				// For the else block, creating a negation of the condition.
				final CtUnaryOperator<Boolean> neg = ifStat.getFactory().Core().createUnaryOperator();
				neg.setKind(UnaryOperatorKind.NEG);
				neg.setOperand(ifStat.getCondition());
				cmds.add(new Command(elseStat.getStatements(), Collections.singletonList(neg)));
			}
			return;
		}

		//TODO switch, ternary
		LOG.log(Level.SEVERE, "Unsupported conditional blocks: " + condStat);
	}


	private List<CtElement> getConditionalThatUseVarRef(final CtVariableReference<?> varRef,
														final @NotNull CtExecutable<?> listenerMethod) {
		final CtBlock<?> body = listenerMethod.getBody();
		return body.getElements(new VariableAccessFilter<>(varRef)).stream().
				map(varAcc -> SpoonHelper.INSTANCE.getConditionalParent(varAcc, body)).
				filter(cond -> cond.isPresent()).map(cond -> cond.get()).collect(Collectors.toList());
	}


	private void analyseSingleListenerMethod(final @NotNull  Optional<CtClass<?>> listenerClass,
											 final @NotNull CtExecutable<?> listenerMethod) {
		if(listenerMethod.getBody()==null || listenerMethod.getBody().getStatements().isEmpty()) {
			// Empty so no command
			synchronized(commands) { commands.put(listenerMethod, Collections.emptyList()); }
		}else {
			final List<CtElement> conds = getConditionalStatements(listenerMethod, listenerClass);

			if(conds.isEmpty()) {
				// when no conditional, the content of the method forms a command.
				synchronized(commands) {
					commands.put(listenerMethod, Collections.singletonList(
							new Command(listenerMethod.getBody().getStatements(), Collections.emptyList())));
				}
			}else {
				// For each conditional statements found in the listener method or in its dispatched methods,
				// a command is extracted.
				conds.forEach(cond -> extractCommandsFromConditionalStatements(cond, listenerMethod));
			}
		}
	}


	private @NotNull List<CtElement> getConditionalStatements(final @Nullable CtExecutable<?> exec,
																final @NotNull Optional<CtClass<?>> listenerClass) {
		if(exec==null || exec.getBody()==null)
			return Collections.emptyList();

		final List<CtElement> conds = new ArrayList<>();

		if(listenerClass.isPresent()) { // Searching for dispatched methods is not performed on lambdas.
			conds.addAll(
					// Getting all the methods called in the current method that use a parameter of this last.
					// The goal is to identify the dispatched methods, recursively.
					exec.getElements(new ClassMethodCallFilter(exec.getParameters(), listenerClass.get())).stream().
					// For each dispatched methods, looking for conditional statements.
					map(dispatchM -> getConditionalStatements(dispatchM.getExecutable().getDeclaration(), listenerClass)).
					flatMap(c -> c.stream()).collect(Collectors.toList()));
		}

		conds.addAll(
				// Filtering out the conditional statements that do not use a GUI event.
				exec.getBody().getElements(new ConditionalFilter()).stream().
				// For each conditional statements, looking whether a parameter is used in the condition.
				map(cond -> exec.getParameters().stream().map(par -> getConditionalThatUseVarRef(par.getReference(), exec)).
						collect(Collectors.toList())).flatMap(c -> c.stream()).flatMap(c -> c.stream()).distinct().collect(Collectors.toList()));

		return conds;
	}


	private void analyseMultipleListenerMethods(final @NotNull CtClass<?> listenerClass, final @NotNull List<CtMethod<?>> listenerMethods) {
		final List<CtMethod<?>> nonEmptyM=listenerMethods.stream().
				filter(l -> l.getBody() != null && !l.getBody().getStatements().isEmpty()).collect(Collectors.toList());

		switch(nonEmptyM.size()) {
			case 0:
				synchronized(commands) { listenerMethods.forEach(l -> commands.put(l, Collections.emptyList())); }
				break;
			case 1:
				analyseSingleListenerMethod(Optional.of(listenerClass), nonEmptyM.get(0));
				break;
			default:
				//TODO
				break;
		}
	}
}
