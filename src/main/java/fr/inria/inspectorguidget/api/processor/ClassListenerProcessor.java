/*
 * This file is part of InspectorGuidget.
 * InspectorGuidget is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * InspectorGuidget is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * You should have received a copy of the GNU General Public License
 * along with InspectorGuidget.  If not, see <https://www.gnu.org/licenses/>.
 */

package fr.inria.inspectorguidget.api.processor;

import fr.inria.inspectorguidget.internal.helper.SpoonHelper;
import fr.inria.inspectorguidget.internal.helper.WidgetHelper;
import java.util.Collections;
import java.util.IdentityHashMap;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;
import java.util.stream.Collectors;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import org.jetbrains.annotations.NotNull;
import spoon.reflect.declaration.CtClass;
import spoon.reflect.declaration.CtMethod;
import spoon.reflect.reference.CtTypeReference;

/**
 * This processor find listener methods in the source code
 */
public class ClassListenerProcessor extends InspectorGuidgetProcessor<CtClass<?>> {
	private final @NotNull Map<CtClass<?>, Set<CtMethod<?>>> listenerMethods;


	public ClassListenerProcessor() {
		super();
		listenerMethods = new IdentityHashMap<>();
	}

	public @NotNull Map<CtClass<?>, Set<CtMethod<?>>> getAllListenerMethods() {
		return Collections.unmodifiableMap(listenerMethods);
	}


	@Override
	public void process(final @NotNull CtClass<?> clazz) {
		LOG.log(Level.INFO, () -> "process CtClass: " + clazz);

		final BooleanProperty isAdded = new SimpleBooleanProperty(false);

		// Case SWING
		WidgetHelper.INSTANCE.getSwingListenersRef(getFactory()).stream().filter(clazz::isSubtypeOf).forEach(ref -> {
			isAdded.setValue(true);
			addListenerMethodsFrom(ref, clazz);
		});

		// Case AWT
		WidgetHelper.INSTANCE.getAWTListenersRef(getFactory()).stream().filter(clazz::isSubtypeOf).forEach(ref -> {
			isAdded.setValue(true);
			addListenerMethodsFrom(ref, clazz);
		});

		// Case JFX
		WidgetHelper.INSTANCE.getJFXListenersRef(getFactory()).stream().filter(clazz::isSubtypeOf).forEach(ref -> {
			isAdded.setValue(true);
			addListenerMethodsFrom(ref, clazz);
		});

		// Case SWT
		WidgetHelper.INSTANCE.getSWTListenersRef(getFactory()).stream().filter(clazz::isSubtypeOf).forEach(ref -> {
			isAdded.setValue(true);
			addListenerMethodsFrom(ref, clazz);
		});

		if(!isAdded.getValue() && WidgetHelper.INSTANCE.isListenerClass(clazz, getFactory(), null)) {
			LOG.log(Level.WARNING, "Listener not supported " + SpoonHelper.INSTANCE.formatPosition(clazz.getPosition()) + ": " + clazz.getQualifiedName());
		}
	}


	private void addListenerMethodsFrom(final @NotNull CtTypeReference<?> ref, final @NotNull CtClass<?> clazz) {
		final Set<CtMethod<?>> methods = getImplementedListenerMethods(clazz, ref);
		final Set<CtMethod<?>> savedMethods = listenerMethods.get(clazz);

		if(savedMethods != null) {
			methods.addAll(savedMethods);
		}

		listenerMethods.put(clazz, methods);
	}


	@Override
	public boolean isToBeProcessed(final @NotNull CtClass<?> candidate) {
		return WidgetHelper.INSTANCE.isListenerClass(candidate, getFactory(), null);
	}


	/**
	 * Store each method from cl that implements interf
	 */
	private Set<CtMethod<?>> getImplementedListenerMethods(final @NotNull CtClass<?> cl, final @NotNull CtTypeReference<?> interf) {
		return interf.getTypeDeclaration().getMethods()
			.parallelStream()
			.map(interfM -> {
				CtMethod<?> m = cl.getMethod(interfM.getSimpleName(), interfM.getParameters().stream().map(p -> p.getType()).toArray(CtTypeReference<?>[]::new));

				//FIXME generics in methods are not correctly managed by Spoon or Java (getClass from Class
				// does not provide any generics). So...
				if(m == null && cl.isSubtypeOf(WidgetHelper.INSTANCE.getJFXListenersRef(getFactory()).get(0))) {
					m = cl.getMethodsByName(interfM.getSimpleName()).get(0);
				}
				return m;
			})
			.filter(m -> m != null)
			.collect(Collectors.toSet());
	}
}