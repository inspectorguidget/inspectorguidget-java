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

package fr.inria.inspectorguidget.api.analyser;

import fr.inria.inspectorguidget.api.processor.ClassListenerProcessor;
import fr.inria.inspectorguidget.api.processor.LambdaListenerProcessor;
import java.util.Collections;
import java.util.Map;
import java.util.Set;
import org.jetbrains.annotations.NotNull;
import spoon.reflect.code.CtLambda;
import spoon.reflect.declaration.CtClass;
import spoon.reflect.declaration.CtMethod;

public class GUIListenerAnalyser extends InspectorGuidetAnalyser {
	private final @NotNull ClassListenerProcessor classProc;
	private final @NotNull LambdaListenerProcessor lambdaProc;

	public GUIListenerAnalyser() {
		super(Collections.emptyList());

		classProc = new ClassListenerProcessor();
		lambdaProc = new LambdaListenerProcessor();

		addProcessor(classProc);
		addProcessor(lambdaProc);
	}

	public @NotNull Map<CtClass<?>, Set<CtMethod<?>>> getClassListeners() {
		return Collections.unmodifiableMap(classProc.getAllListenerMethods());
	}

	public @NotNull Set<CtLambda<?>> getLambdaListeners() {
		return Collections.unmodifiableSet(lambdaProc.getAllListenerLambdas());
	}
}
