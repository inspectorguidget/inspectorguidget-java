package fr.inria.inspectorguidget.api.processor;

import fr.inria.inspectorguidget.api.TestInspectorGuidget;
import java.util.Collections;
import java.util.List;

import org.junit.jupiter.api.Test;
import spoon.reflect.declaration.CtElement;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;


public class TestActionProcessor extends TestInspectorGuidget<InspectorGuidgetProcessor<? extends CtElement>> {
	private ActionProcessor classProc;

	@Override
	public List<InspectorGuidgetProcessor<? extends CtElement>> createProcessor() {
		classProc = new ActionProcessor();
		return Collections.singletonList(classProc);
	}

	@Test
	public void testAbstractActionNotAListener() {
		run("src/test/resources/java/listeners/AbstractAction.java");
		assertThat(classProc.getActions().size()).isEqualTo(1);
	}

	@Test
	public void ActionListenerCondInstanceOfReturnNotAClass() {
		run("src/test/resources/java/analysers/ActionListenerCondInstanceOfReturn.java");
		assertThat(classProc.getActions().size()).isEqualTo(0);
	}
}
