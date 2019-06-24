package fr.inria.inspectorguidget.api.processor;

import fr.inria.inspectorguidget.internal.helper.WidgetHelper;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import org.jetbrains.annotations.NotNull;
import spoon.reflect.declaration.CtClass;

public class ActionProcessor extends InspectorGuidgetProcessor<CtClass<?>> {
	private final @NotNull Set<CtClass<?>> actions;

	public ActionProcessor() {
		super();
		actions = new HashSet<>();
	}

	public @NotNull Set<CtClass<?>> getActions() {
		return Collections.unmodifiableSet(actions);
	}


	@Override
	public boolean isToBeProcessed(final @NotNull CtClass<?> candidate) {
		return candidate.isSubtypeOf(WidgetHelper.INSTANCE.getActionRef(candidate.getFactory()));
	}


	@Override
	public void process(final @NotNull CtClass<?> clazz) {
		actions.add(clazz);
	}
}