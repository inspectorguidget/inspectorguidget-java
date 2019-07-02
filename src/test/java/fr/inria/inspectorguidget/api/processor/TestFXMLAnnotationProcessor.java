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
