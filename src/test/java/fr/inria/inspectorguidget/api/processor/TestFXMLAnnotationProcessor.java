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

import fr.inria.inspectorguidget.api.TestInspectorGuidget;
import org.junit.jupiter.api.Test;


import java.util.Collections;
import java.util.List;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;


public class TestFXMLAnnotationProcessor extends TestInspectorGuidget<FXMLAnnotationProcessor> {
	private FXMLAnnotationProcessor proc;

	@Override
	public List<FXMLAnnotationProcessor> createProcessor() {
		proc = new FXMLAnnotationProcessor();
		return Collections.singletonList(proc);
	}

	@Test
	public void testFXMLAnnotationAttributes() {
		run("src/test/resources/java/fxml/FXMLAnnotationAttributes.java");
		assertThat(proc.getFieldAnnotations().size()).isEqualTo(2);
		assertThat(proc.getMethodAnnotations().size()).isEqualTo(0);
	}
}
