package fr.inria.diverse.torgen.inspectorguidget.test;

import fr.inria.diverse.torgen.inspectorguidget.analyser.CommandAnalyser;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class TestCommandAnalyser {
	CommandAnalyser analyser;

	@Before
	public void setUp() {
		analyser = new CommandAnalyser();
	}

	@Test
	public void testEmptyClassListenerMethodNoCommand() {
		analyser.addInputResource("src/test/resources/java/analysers/ActionListenerEmptyClass.java");
		analyser.run();
		assertEquals(1, analyser.getCommands().values().size());
		assertEquals(0L, analyser.getCommands().values().stream().flatMap(c -> c.stream()).count());
	}

	@Test
	public void testEmptyLambdaListenerMethodNoCommand() {
		analyser.addInputResource("src/test/resources/java/analysers/ActionListenerEmptyLambda.java");
		analyser.run();
		assertEquals(1, analyser.getCommands().values().size());
		assertEquals(0L, analyser.getCommands().values().stream().flatMap(c -> c.stream()).count());
	}

	@Test
	public void testClassListenerMethodNoConditional() {
		analyser.addInputResource("src/test/resources/java/analysers/ActionListenerNoConditClass.java");
		analyser.run();
		assertEquals(1, analyser.getCommands().values().size());
		assertEquals(1L, analyser.getCommands().values().stream().flatMap(c -> c.stream()).count());
	}

	@Test
	public void testLambdaListenerMethodNoConditional() {
		analyser.addInputResource("src/test/resources/java/analysers/ActionListenerNoConditLambda.java");
		analyser.run();
		assertEquals(1, analyser.getCommands().values().size());
		assertEquals(1L, analyser.getCommands().values().stream().flatMap(c -> c.stream()).count());
	}

	@Test
	public void testClassListenerMethodNoCondMultipleMethodsButOneUsed() {
		analyser.addInputResource("src/test/resources/java/analysers/MouseInputListOneMethodUsed.java");
		analyser.run();
		assertEquals(1, analyser.getCommands().values().size());
		assertEquals(1L, analyser.getCommands().values().stream().flatMap(c -> c.stream()).count());
	}

	@Test
	public void testClassListenerMethodNoCodeMultipleMethods() {
		analyser.addInputResource("src/test/resources/java/listeners/MouseInputListClass.java");
		analyser.run();
		assertEquals(7, analyser.getCommands().values().size());
		assertEquals(0L, analyser.getCommands().values().stream().flatMap(c -> c.stream()).count());
	}

	@Test
	public void testClassListenerOneMethodCondOneInstanceOf() {
		analyser.addInputResource("src/test/resources/java/analysers/ActionListenerCondOneInstanceOf.java");
		analyser.run();
		assertEquals(1, analyser.getCommands().values().size());
		assertEquals(1L, analyser.getCommands().values().stream().flatMap(c -> c.stream()).count());
	}

	@Test
	public void testClassListenerOneMethodCondOneInstanceOfElse() {
		analyser.addInputResource("src/test/resources/java/analysers/ActionListenerCondOneInstanceOfElse.java");
		analyser.run();
		assertEquals(1, analyser.getCommands().values().size());
		assertEquals(2L, analyser.getCommands().values().stream().flatMap(c -> c.stream()).count());
	}

	@Test
	public void testClassListenerCondInstanceOfReturn() {
		analyser.addInputResource("src/test/resources/java/analysers/ActionListenerCondInstanceOfReturn.java");
		analyser.run();
		assertEquals(1, analyser.getCommands().values().size());
		assertEquals(3L, analyser.getCommands().values().stream().flatMap(c -> c.stream()).count());
	}
}