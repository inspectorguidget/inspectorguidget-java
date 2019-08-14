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

import fr.inria.inspectorguidget.internal.helper.LoggingHelper;
import java.util.Collection;
import java.util.logging.Logger;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import spoon.processing.AbstractProcessor;
import spoon.reflect.declaration.CtElement;
import spoon.reflect.reference.CtTypeReference;
import spoon.support.SpoonClassNotFoundException;

public abstract class InspectorGuidgetProcessor<T extends CtElement> extends AbstractProcessor<T> {
	public static final @NotNull Logger LOG = Logger.getLogger("InspectorGuidget Processor");

	static {
		LOG.setLevel(LoggingHelper.INSTANCE.loggingLevel);
	}

	public static boolean isASubTypeOf(final @Nullable CtTypeReference<?> candidate, final @NotNull Collection<CtTypeReference<?>> types) {
		return candidate != null && candidate.getTypeDeclaration() != null && types.stream().anyMatch(type -> {
			try {
				return candidate.isSubtypeOf(type);
			}catch(final SpoonClassNotFoundException ex) {
				return false;
			}
		});
	}

	public InspectorGuidgetProcessor() {
		super();
	}
}
