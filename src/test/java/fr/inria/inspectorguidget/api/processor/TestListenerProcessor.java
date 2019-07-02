package fr.inria.inspectorguidget.api.processor;

import fr.inria.inspectorguidget.api.TestInspectorGuidget;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import spoon.reflect.declaration.CtElement;

import static org.assertj.core.api.Assertions.assertThat;

public class TestListenerProcessor extends TestInspectorGuidget<InspectorGuidgetProcessor<? extends CtElement>> {
	private LambdaListenerProcessor lambdaProc;
	private ClassListenerProcessor classProc;

	@BeforeAll
	public static void setUpBeforeClass() {
		InspectorGuidgetProcessor.LOG.addHandler(HANDLER_FAIL);
	}

	@Override
	public List<InspectorGuidgetProcessor<? extends CtElement>> createProcessor() {
		lambdaProc = new LambdaListenerProcessor();
		classProc = new ClassListenerProcessor();
		return Arrays.asList(classProc, lambdaProc);
	}


	@Test
	public void testActionListenerAsLambda() {
		run("src/test/resources/java/listeners/ActionListenerLambda.java");
		assertThat(lambdaProc.getAllListenerLambdas()).hasSize(1);
		assertThat(classProc.getAllListenerMethods()).isEmpty();
	}

	@Test
	public void testActionListenerAsLambdaInheritance() {
		run("src/test/resources/java/listeners/ActionListenerLambdaInheritance.java");
		assertThat(lambdaProc.getAllListenerLambdas()).hasSize(1);
	}

	@Test
	public void testActionListenerAsLambdaInheritanceDefaultMethod() {
		run("src/test/resources/java/listeners/ListenerLambdaInheritanceDefault.java");
		assertThat(lambdaProc.getAllListenerLambdas().size()).isEqualTo(1);
	}

	@Test
	public void testSwingCaretListenerAsLambda() {
		run("src/test/resources/java/listeners/CaretListenerLambda.java");
		assertThat(lambdaProc.getAllListenerLambdas().size()).isEqualTo(1);
	}

	@Test
	public void testJFXHandlerAsClass() {
		run("src/test/resources/java/listeners/JFXEventHandlerClass.java");
		assertThat(classProc.getAllListenerMethods().size()).isEqualTo(1);
		assertThat(classProc.getAllListenerMethods().keySet().size()).isEqualTo(1);
		assertThat(classProc.getAllListenerMethods().values().stream().mapToLong(c -> c.size()).sum()).isEqualTo(1);
	}

	@Test
	public void testJFXEventHandlerAsLambda() {
		run("src/test/resources/java/listeners/JFXEventHandlerLambda.java");
		assertThat(lambdaProc.getAllListenerLambdas().size()).isEqualTo(1);
	}

	@Test
	public void testAWTMouseListernerAsClassImplementingInterface() {
		run("src/test/resources/java/listeners/MouseListClass.java");
		assertThat(classProc.getAllListenerMethods().size()).isEqualTo(1);
		assertThat(classProc.getAllListenerMethods().keySet().size()).isEqualTo(1);
		assertThat(classProc.getAllListenerMethods().values().stream().mapToLong(c -> c.size()).sum()).isEqualTo(5);
	}


	@Test
	public void testSwingMouseInputListernerAsClassImplementingInterface() {
		run("src/test/resources/java/listeners/MouseInputListClass.java");
		assertThat(classProc.getAllListenerMethods().size()).isEqualTo(1);
		assertThat(classProc.getAllListenerMethods().keySet().size()).isEqualTo(1);
		assertThat(classProc.getAllListenerMethods().values().stream().mapToLong(c -> c.size()).sum()).isEqualTo(7);
	}

	@Test
	public void testSwingMouseListernerAsAnonClass() {
		run("src/test/resources/java/listeners/MouseListAnonClass.java");
		assertThat(classProc.getAllListenerMethods().size()).isEqualTo(1);
		assertThat(classProc.getAllListenerMethods().keySet().size()).isEqualTo(1);
		assertThat(classProc.getAllListenerMethods().values().stream().mapToLong(c -> c.size()).sum()).isEqualTo(5);
	}

	@Test
	public void testSwingMouseListernerAsClassWithInheritance() {
		run("src/test/resources/java/listeners/MouseListAnonClassWithInheritance.java");
		assertThat(classProc.getAllListenerMethods().size()).isEqualTo(2);
		assertThat(classProc.getAllListenerMethods().keySet().size()).isEqualTo(2);
		assertThat(classProc.getAllListenerMethods().values().stream().mapToLong(c -> c.size()).sum()).isEqualTo(6);
	}

	@Test
	public void testSwingMouseListernerAsAnonClassWithInheritance() {
		run("src/test/resources/java/listeners/MouseListClassInheritance.java");
		assertThat(classProc.getAllListenerMethods().size()).isEqualTo(2);
		assertThat(classProc.getAllListenerMethods().keySet().size()).isEqualTo(2);
		assertThat(classProc.getAllListenerMethods().values().stream().mapToLong(c -> c.size()).sum()).isEqualTo(6);
	}

	@Test
	public void testSwingMouseListernerTwoSameClasses() {
		run("src/test/resources/java/listeners/MouseListTwoSameClasses.java");
		assertThat(classProc.getAllListenerMethods().size()).isEqualTo(2);
		assertThat(classProc.getAllListenerMethods().keySet().size()).isEqualTo(2);
		assertThat(classProc.getAllListenerMethods().values().stream().mapToLong(c -> c.size()).sum()).isEqualTo(10);
	}

	@Test
	public void testActionListenerClassEmpty() {
		run("src/test/resources/java/analysers/ActionListenerEmptyClass.java");
		assertThat(classProc.getAllListenerMethods().size()).isEqualTo(1);
		assertThat(classProc.getAllListenerMethods().keySet().size()).isEqualTo(1);
		assertThat(classProc.getAllListenerMethods().values().stream().mapToLong(c -> c.size()).sum()).isEqualTo(1);
	}

	@Test
	public void testMouseListClassInheritanceAbsract() {
		run("src/test/resources/java/listeners/MouseListClassInheritanceAbsract.java");
		assertThat(classProc.getAllListenerMethods().size()).isEqualTo(2);
		assertThat(classProc.getAllListenerMethods().values().stream().mapToLong(c -> c.size()).sum()).isEqualTo(5);
	}

	@Test
	public void testAbstractActionNotAListener() {
		run("src/test/resources/java/listeners/AbstractAction.java");
		assertThat(classProc.getAllListenerMethods().values().stream().mapToLong(c -> c.size()).sum()).isEqualTo(0);
		assertThat(classProc.getAllListenerMethods().size()).isEqualTo(0);
	}

	@Test
	public void testAWTDragListener() {
		run("src/test/resources/java/listeners/AWTDragListener.java");
		assertThat(classProc.getAllListenerMethods().size()).isEqualTo(4);
		assertThat(classProc.getAllListenerMethods().values().stream().mapToLong(c -> c.size()).sum()).isEqualTo(12);
	}

	@Test
	public void testClassWithMultipleListeners() {
		run("src/test/resources/java/listeners/MultipleListener.java");
		assertThat(classProc.getAllListenerMethods().size()).isEqualTo(1);
		assertThat(classProc.getAllListenerMethods().values().stream().mapToLong(c -> c.size()).sum()).isEqualTo(2L);
	}

	@Test
	public void testSWTEventHandlerClass() {
		runWithCP(Collections.singletonList(SWT_LIB), "src/test/resources/java/listeners/SWTEventHandlerClass.java");
		assertThat(classProc.getAllListenerMethods().size()).isEqualTo(1);
		assertThat(classProc.getAllListenerMethods().values().stream().mapToLong(c -> c.size()).sum()).isEqualTo(1L);
	}

	@Test
	public void testSWTEventHandlerLambda() {
		runWithCP(Collections.singletonList(SWT_LIB), "src/test/resources/java/listeners/SWTEventHandlerLambda.java");
		assertThat(lambdaProc.getAllListenerLambdas().size()).isEqualTo(1);
		assertThat(classProc.getAllListenerMethods().size()).isEqualTo(0);
	}


	@Test
	public void testSWTAdapterClass() {
		runWithCP(Collections.singletonList(SWT_LIB), "src/test/resources/java/listeners/SWTAdapterClass.java");
		assertThat(classProc.getAllListenerMethods().size()).isEqualTo(1);
		assertThat(classProc.getAllListenerMethods().values().stream().mapToLong(c -> c.size()).sum()).isEqualTo(1L);
	}
}
