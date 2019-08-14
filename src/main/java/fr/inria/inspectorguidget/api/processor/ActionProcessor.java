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
