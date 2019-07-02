package fr.inria.inspectorguidget.api.analyser;

import fr.inria.inspectorguidget.api.TestInspectorGuidget;
import fr.inria.inspectorguidget.internal.helper.CodeBlockPos;
import fr.inria.inspectorguidget.internal.helper.SpoonStructurePrinter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.*;
import spoon.reflect.code.CtBinaryOperator;
import spoon.reflect.code.CtLiteral;
import spoon.reflect.declaration.CtExecutable;
import spoon.testing.Assert;
import static org.assertj.core.api.Assertions.*;

public class TestCommandAnalyser {
	CommandAnalyser analyser;

	@BeforeAll
	public static void setUpBeforeClass() {
		InspectorGuidetAnalyser.LOG.addHandler(TestInspectorGuidget.HANDLER_FAIL);
	}

	@BeforeEach
	public void setUp() {
		analyser = new CommandAnalyser();
	}

	@AfterEach
	public void tearsDown() {
		if(TestInspectorGuidget.SHOW_MODEL) {
			final SpoonStructurePrinter printer = new SpoonStructurePrinter();
			printer.scan(Collections.singletonList(analyser.getModelBuilder().getFactory().Package().getRootPackage()));
		}
	}

	@Test
	public void testEmptyClassListenerMethodNoCommand() {
		analyser.addInputResource("src/test/resources/java/analysers/ActionListenerEmptyClass.java");
		analyser.run();
		assertThat(analyser.getCommands().values().size()).isEqualTo(1);
		assertThat(analyser.getCommands().values().stream().mapToLong(c -> c.getNbTotalCmds()).sum()).isEqualTo(0L);
	}

	@Test
	public void testEmptyLambdaListenerMethodNoCommand() {
		analyser.addInputResource("src/test/resources/java/analysers/ActionListenerEmptyLambda.java");
		analyser.run();
		assertThat(analyser.getCommands().values().size()).isEqualTo(1);
		assertThat(analyser.getCommands().values().stream().mapToLong(c -> c.getNbTotalCmds()).sum()).isEqualTo(0L);
	}

	@Test
	public void testClassListenerMethodNoConditional() {
		analyser.addInputResource("src/test/resources/java/analysers/ActionListenerNoConditClass.java");
		analyser.run();
		assertThat(analyser.getCommands().values().size()).isEqualTo(1);
		assertThat(analyser.getCommands().values().stream().mapToLong(c -> c.getNbTotalCmds()).sum()).isEqualTo(1L);
	}

	@Test
	public void testLambdaListenerMethodNoConditional() {
		analyser.addInputResource("src/test/resources/java/analysers/ActionListenerNoConditLambda.java");
		analyser.run();
		assertThat(analyser.getCommands().values().size()).isEqualTo(1);
		assertThat(analyser.getCommands().values().stream().mapToLong(c -> c.getNbTotalCmds()).sum()).isEqualTo(1L);
	}

	@Test
	public void testLambdaListenerNoBlockMethodNoConditional() {
		analyser.addInputResource("src/test/resources/java/widgetsIdentification/LambdaOnSingleFieldWidgetNoCond.java");
		analyser.run();
		assertThat(analyser.getCommands().values().size()).isEqualTo(1);
		assertThat(analyser.getCommands().values().stream().mapToLong(c -> c.getNbTotalCmds()).sum()).isEqualTo(1L);
	}

	@Test
	public void testClassListenerMethodNoCondMultipleMethodsButOneUsed() {
		analyser.addInputResource("src/test/resources/java/analysers/MouseInputListOneMethodUsed.java");
		analyser.run();
		assertThat(analyser.getCommands().values().size()).isEqualTo(1);
		assertThat(analyser.getCommands().values().stream().mapToLong(c -> c.getNbTotalCmds()).sum()).isEqualTo(1L);
	}

	@Test
	public void testClassListenerMethodNoCodeMultipleMethods() {
		analyser.addInputResource("src/test/resources/java/listeners/MouseInputListClass.java");
		analyser.run();
		assertThat(analyser.getCommands().values().size()).isEqualTo(7);
		assertThat(analyser.getCommands().values().stream().mapToLong(c -> c.getNbTotalCmds()).sum()).isEqualTo(0L);
	}

	@Test
	public void testClassListenerOneMethodCondOneInstanceOf() {
		analyser.addInputResource("src/test/resources/java/analysers/ActionListenerCondOneInstanceOf.java");
		analyser.run();
		assertThat(analyser.getCommands().values().size()).isEqualTo(1);
		assertThat(analyser.getCommands().values().stream().mapToLong(c -> c.getNbTotalCmds()).sum()).isEqualTo(1L);
	}

	@Test
	public void testClassListenerOneMethodCondOneInstanceOfElse() {
		analyser.addInputResource("src/test/resources/java/analysers/ActionListenerCondOneInstanceOfElse.java");
		analyser.run();
		assertThat(analyser.getCommands().values().size()).isEqualTo(1);
		assertThat(analyser.getCommands().values().stream().mapToLong(c -> c.getNbTotalCmds()).sum()).isEqualTo(2L);
	}

	@Test
	public void testClassListenerCondInstanceOfReturn() {
		analyser.addInputResource("src/test/resources/java/analysers/ActionListenerCondInstanceOfReturn.java");
		analyser.run();
		assertThat(analyser.getCommands().values().size()).isEqualTo(1);
		assertThat(analyser.getCommands().values().stream().mapToLong(c -> c.getNbTotalCmds()).sum()).isEqualTo(3L);
		assertThat(new ArrayList<>(analyser.getCommands().values()).get(0).getCommand(0).getLineStart()).isEqualTo(17);
		assertThat(new ArrayList<>(analyser.getCommands().values()).get(0).getCommand(0).getLineEnd()).isEqualTo(18);
		assertThat(new ArrayList<>(analyser.getCommands().values()).get(0).getCommand(1).getLineStart()).isEqualTo(21);
		assertThat(new ArrayList<>(analyser.getCommands().values()).get(0).getCommand(1).getLineEnd()).isEqualTo(22);
		assertThat(new ArrayList<>(analyser.getCommands().values()).get(0).getCommand(2).getLineStart()).isEqualTo(25);
		assertThat(new ArrayList<>(analyser.getCommands().values()).get(0).getCommand(2).getLineEnd()).isEqualTo(26);
	}

	@Test
	public void testClassListenerSimpleDelegation() {
		analyser.addInputResource("src/test/resources/java/analysers/ActionListenerSimpleDelegation.java");
		analyser.run();
		assertThat(analyser.getCommands().values().size()).isEqualTo(1);
		assertThat(analyser.getCommands().values().stream().mapToLong(c -> c.getNbTotalCmds()).sum()).isEqualTo(2L);
		assertThat(new ArrayList<>(analyser.getCommands().values()).get(0).getCommand(0).getLineStart()).isEqualTo(27);
		assertThat(new ArrayList<>(analyser.getCommands().values()).get(0).getCommand(0).getLineEnd()).isEqualTo(27);
		assertThat(new ArrayList<>(analyser.getCommands().values()).get(0).getCommand(1).getLineStart()).isEqualTo(12);
		assertThat(new ArrayList<>(analyser.getCommands().values()).get(0).getCommand(1).getLineEnd()).isEqualTo(12);
	}

	@Test
	public void testClassListenerSwitch() {
		analyser.addInputResource("src/test/resources/java/analysers/ActionListenerCondSwitch.java");
		analyser.run();
		assertThat(analyser.getCommands().values().size()).isEqualTo(1);
		assertThat(analyser.getCommands().values().stream().mapToLong(c -> c.getNbTotalCmds()).sum()).isEqualTo(3L);
		assertThat(new ArrayList<>(analyser.getCommands().values()).get(0).getCommand(0).getLineStart()).isEqualTo(18);
		assertThat(new ArrayList<>(analyser.getCommands().values()).get(0).getCommand(0).getLineEnd()).isEqualTo(19);
		assertThat(new ArrayList<>(analyser.getCommands().values()).get(0).getCommand(1).getLineStart()).isEqualTo(21);
		assertThat(new ArrayList<>(analyser.getCommands().values()).get(0).getCommand(1).getLineEnd()).isEqualTo(22);
		assertThat(new ArrayList<>(analyser.getCommands().values()).get(0).getCommand(2).getLineStart()).isEqualTo(24);
		assertThat(new ArrayList<>(analyser.getCommands().values()).get(0).getCommand(2).getLineEnd()).isEqualTo(25);
	}

	@Test
	public void testClassListenerSwitchHasMainBlock() {
		analyser.addInputResource("src/test/resources/java/analysers/ActionListenerCondSwitch.java");
		analyser.run();
		List<Command> cmds = analyser.getCommands().values().stream().flatMap(c -> c.getCommands().stream()).collect(Collectors.toList());
		assertThat(cmds.get(0).getMainStatmtEntry().isPresent()).isTrue();
		assertThat(cmds.get(1).getMainStatmtEntry().isPresent()).isTrue();
		assertThat(cmds.get(2).getMainStatmtEntry().isPresent()).isTrue();
	}

	@Test
	public void testClassNestedIf() {
		analyser.addInputResource("src/test/resources/java/analysers/ActionListenerCondSimpleNestedIf.java");
		analyser.run();
		assertThat(analyser.getCommands().values().size()).isEqualTo(1);
		assertThat(analyser.getCommands().values().stream().mapToLong(c -> c.getNbTotalCmds()).sum()).isEqualTo(2L);

		Command cmd = new ArrayList<>(analyser.getCommands().values()).get(0).getCommand(0);

		assertThat(cmd.getLineStart()).isEqualTo(25);
		assertThat(cmd.getLineEnd()).isEqualTo(26);
		assertThat(cmd.getConditions().size()).isEqualTo(3);

		Assert.assertThat(cmd.getConditions().get(0).effectiveStatmt).isEqualTo("(e.getSource()) instanceof javax.swing.JButton");
		Assert.assertThat(cmd.getConditions().get(1).effectiveStatmt).isEqualTo("\"test\".equals(foo)");
		Assert.assertThat(cmd.getConditions().get(2).effectiveStatmt).isEqualTo("isItOkForYou()");

		cmd = new ArrayList<>(analyser.getCommands().values()).get(0).getCommand(1);

		assertThat(cmd.getLineStart()).isEqualTo(29);
		assertThat(cmd.getLineEnd()).isEqualTo(30);
		assertThat(cmd.getConditions().size()).isEqualTo(3);

		Assert.assertThat(cmd.getConditions().get(0).effectiveStatmt).isEqualTo("(e.getSource()) instanceof javax.swing.JMenuBar");
		Assert.assertThat(cmd.getConditions().get(1).effectiveStatmt).isEqualTo("\"test\".equals(foo)");
		Assert.assertThat(cmd.getConditions().get(2).effectiveStatmt).isEqualTo("isItOkForYou()");
	}

	@Test
	public void testGetOptimalCodeBlocks() {
		analyser.addInputResource("src/test/resources/java/analysers/ActionListenerCondSimpleNestedIf.java");
		analyser.run();

		Command cmd = new ArrayList<>(analyser.getCommands().values()).get(0).getCommand(0);
		List<CodeBlockPos> blocks = cmd.getOptimalCodeBlocks();

		assertThat(blocks.size()).isEqualTo(2);
		assertThat(blocks.get(0).startLine).isEqualTo(21);
		assertThat(blocks.get(0).endLine).isEqualTo(22);
		assertThat(blocks.get(1).startLine).isEqualTo(24);
		assertThat(blocks.get(1).endLine).isEqualTo(26);
	}

	@Test
	public void testNbLinesCommand() {
		analyser.addInputResource("src/test/resources/java/analysers/ActionListenerCondSimpleNestedIf.java");
		analyser.run();

		Command cmd = new ArrayList<>(analyser.getCommands().values()).get(0).getCommand(0);
		assertThat(cmd.getNbLines()).isEqualTo(4);
	}

	@Test
	@Disabled
	public void testClassListenerCondInstanceOfEmptyReturn() {
		analyser.addInputResource("src/test/resources/java/analysers/ActionListenerCondInstanceOfEmptyReturn.java");
		analyser.run();
		assertThat(analyser.getCommands().values().size()).isEqualTo(1);
		assertThat(analyser.getCommands().values().stream().mapToLong(c -> c.getNbTotalCmds()).sum()).isEqualTo(0L);
	}

	@Test
	public void testClassListenerCondInstanceOfLocalVar() {
		analyser.addInputResource("src/test/resources/java/analysers/ActionListenerCondInstanceOfLocalVar.java");
		analyser.run();
		assertThat(analyser.getCommands().values().size()).isEqualTo(1);
		assertThat(analyser.getCommands().values().stream().mapToLong(c -> c.getNbTotalCmds()).sum()).isEqualTo(3L);
		assertThat(new ArrayList<>(analyser.getCommands().values()).get(0).getCommand(0).getLineStart()).isEqualTo(20);
		assertThat(new ArrayList<>(analyser.getCommands().values()).get(0).getCommand(0).getLineEnd()).isEqualTo(21);
		assertThat(new ArrayList<>(analyser.getCommands().values()).get(0).getCommand(1).getLineStart()).isEqualTo(24);
		assertThat(new ArrayList<>(analyser.getCommands().values()).get(0).getCommand(1).getLineEnd()).isEqualTo(25);
		assertThat(new ArrayList<>(analyser.getCommands().values()).get(0).getCommand(2).getLineStart()).isEqualTo(28);
		assertThat(new ArrayList<>(analyser.getCommands().values()).get(0).getCommand(2).getLineEnd()).isEqualTo(29);
	}

	@Test
	public void testClassListenerFragmentedCommand() {
		analyser.addInputResource("src/test/resources/java/analysers/ActionListenerCondFragmentedCommand.java");
		analyser.run();
		assertThat(analyser.getCommands().values().size()).isEqualTo(1);
		assertThat(analyser.getCommands().values().stream().mapToLong(c -> c.getNbTotalCmds()).sum()).isEqualTo(2L);
		assertThat(new ArrayList<>(analyser.getCommands().values()).get(0).getCommand(0).getLineStart()).isEqualTo(18);
		assertThat(new ArrayList<>(analyser.getCommands().values()).get(0).getCommand(0).getLineEnd()).isEqualTo(21);
		assertThat(new ArrayList<>(analyser.getCommands().values()).get(0).getCommand(1).getLineStart()).isEqualTo(25);
		assertThat(new ArrayList<>(analyser.getCommands().values()).get(0).getCommand(1).getLineEnd()).isEqualTo(26);
	}

	@Test
	public void testRealComplexCommandExample1() {
		analyser.addInputResource("src/test/resources/java/analysers/RealComplexCommandExample1.java");
		analyser.run();
		assertThat(analyser.getCommands().values().size()).isEqualTo(1);
		assertThat(analyser.getCommands().values().stream().mapToLong(c -> c.getNbTotalCmds()).sum()).isEqualTo(4L);
	}

	@Test
	public void testRealComplexCommandExample1CommandPositions() {
		analyser.addInputResource("src/test/resources/java/analysers/RealComplexCommandExample1.java");
		analyser.run();
		UIListener list = new ArrayList<>(analyser.getCommands().values()).get(0);
		List<CodeBlockPos> blocks = list.getCommand(0).getOptimalCodeBlocks();
		assertThat(blocks.get(0).startLine).isEqualTo(32);
		assertThat(blocks.get(0).endLine).isEqualTo(33);
		assertThat(blocks.get(1).startLine).isEqualTo(35);
		assertThat(blocks.get(1).endLine).isEqualTo(36);
		assertThat(blocks.get(2).startLine).isEqualTo(38);
		assertThat(blocks.get(2).endLine).isEqualTo(43);
	}

	@Test
	public void testRealComplexCommandExample1CommandPositions2() {
		analyser.addInputResource("src/test/resources/java/analysers/RealComplexCommandExample1.java");
		analyser.run();
		UIListener list = new ArrayList<>(analyser.getCommands().values()).get(0);
		List<CodeBlockPos> blocks = list.getCommand(1).getOptimalCodeBlocks();

		assertThat(blocks.get(0).startLine).isEqualTo(32);
		assertThat(blocks.get(0).endLine).isEqualTo(33);
		assertThat(blocks.get(1).startLine).isEqualTo(35);
		assertThat(blocks.get(1).endLine).isEqualTo(36);
		assertThat(blocks.get(2).startLine).isEqualTo(46);
		assertThat(blocks.get(2).endLine).isEqualTo(46);
		assertThat(blocks.get(3).startLine).isEqualTo(48);
		assertThat(blocks.get(3).endLine).isEqualTo(56);
		assertThat(blocks.get(4).startLine).isEqualTo(58);
		assertThat(blocks.get(4).endLine).isEqualTo(58);
		assertThat(blocks.get(5).startLine).isEqualTo(60);
		assertThat(blocks.get(5).endLine).isEqualTo(64);
	}

	@Test
	public void testRealComplexCommandExample1CommandPositions3() {
		analyser.addInputResource("src/test/resources/java/analysers/RealComplexCommandExample1.java");
		analyser.run();
		UIListener cmds = new ArrayList<>(analyser.getCommands().values()).get(0);
		List<CodeBlockPos> blocks = cmds.getCommand(2).getOptimalCodeBlocks();

		assertThat(blocks.get(0).startLine).isEqualTo(32);
		assertThat(blocks.get(0).endLine).isEqualTo(33);
		assertThat(blocks.get(1).startLine).isEqualTo(35);
		assertThat(blocks.get(1).endLine).isEqualTo(36);
		assertThat(blocks.get(2).startLine).isEqualTo(46);
		assertThat(blocks.get(2).endLine).isEqualTo(46);
		assertThat(blocks.get(3).startLine).isEqualTo(48);
		assertThat(blocks.get(3).endLine).isEqualTo(56);
		assertThat(blocks.get(4).startLine).isEqualTo(67);
		assertThat(blocks.get(4).endLine).isEqualTo(71);
	}

	@Test
	public void testRealComplexCommandExample1CommandPositions4() {
		analyser.addInputResource("src/test/resources/java/analysers/RealComplexCommandExample1.java");
		analyser.run();
		UIListener cmds = new ArrayList<>(analyser.getCommands().values()).get(0);
		List<CodeBlockPos> blocks = cmds.getCommand(3).getOptimalCodeBlocks();

		assertThat(blocks.get(0).startLine).isEqualTo(32);
		assertThat(blocks.get(0).endLine).isEqualTo(33);
		assertThat(blocks.get(1).startLine).isEqualTo(35);
		assertThat(blocks.get(1).endLine).isEqualTo(36);
		assertThat(blocks.get(2).startLine).isEqualTo(46);
		assertThat(blocks.get(2).endLine).isEqualTo(46);
		assertThat(blocks.get(3).startLine).isEqualTo(48);
		assertThat(blocks.get(3).endLine).isEqualTo(56);
		assertThat(blocks.get(4).startLine).isEqualTo(58);
		assertThat(blocks.get(4).endLine).isEqualTo(58);
		assertThat(blocks.get(5).startLine).isEqualTo(74);
		assertThat(blocks.get(5).endLine).isEqualTo(79);
	}

	@Test
	public void testSimpleDispatch() {
		analyser.addInputResource("src/test/resources/java/analysers/SimpleDispatch.java");
		analyser.run();
		assertThat(analyser.getCommands().values().size()).isEqualTo(1);
		List<Command> cmds = analyser.getCommands().values().stream().flatMap(c -> c.getCommands().stream()).collect(Collectors.toList());
		assertThat(cmds.size()).isEqualTo(1L);
		assertThat(cmds.get(0).getMainStatmtEntry().get().getStatmts().get(0).getPosition().getLine()).isEqualTo(11);
	}

	@Test
	public void testCommandsInIfElseIfBlocks() {
		analyser.addInputResource("src/test/resources/java/widgetsIdentification/ClassListenerExternal.java");
		analyser.run();
		assertThat(analyser.getCommands().values().size()).isEqualTo(1);
		assertThat(new ArrayList<>(analyser.getCommands().values()).get(0).getNbLocalCmds()).isEqualTo(2);
	}

	@Test
	public void testCommandsInIfElseIfBlocksHasMainBlock() {
		analyser.addInputResource("src/test/resources/java/widgetsIdentification/ClassListenerExternal.java");
		analyser.run();
		assertThat(analyser.getCommands().values().size()).isEqualTo(1);
		assertThat(analyser.getCommands().values().stream().flatMap(c -> c.getCommands().stream()).collect(Collectors.toList()).get(0).getMainStatmtEntry().isPresent()).isTrue();
		assertThat(analyser.getCommands().values().stream().flatMap(c -> c.getCommands().stream()).collect(Collectors.toList()).get(1).getMainStatmtEntry().isPresent()).isTrue();

	}

	@Test
	public void testFilterOutRegistrationWidgetUsingVarsNbCmd() {
		analyser.addInputResource("src/test/resources/java/widgetsIdentification/FilterOutRegistrationWidgetUsingVars.java");
		analyser.run();
		assertThat(analyser.getCommands().values().size()).isEqualTo(1);
		assertThat(new ArrayList<>(analyser.getCommands().values()).get(0).getNbLocalCmds()).isEqualTo(1);
	}

	@Test
	public void testInsertPSTricksCodeFrameNbCmd() {
		analyser.addInputResource("src/test/resources/java/widgetsIdentification/InsertPSTricksCodeFrame.java");
		analyser.run();
		assertThat(analyser.getCommands().values().size()).isEqualTo(2);
		assertThat(new ArrayList<>(analyser.getCommands().values()).get(0).getNbLocalCmds()).isEqualTo(2);
		assertThat(new ArrayList<>(analyser.getCommands().values()).get(1).getNbLocalCmds()).isEqualTo(2);
	}

	@Test
	public void testMultipleListenerMethodsNbCmd() {
		analyser.addInputResource("src/test/resources/java/listeners/MultipleListenerMethods.java");
		analyser.run();
		assertThat(analyser.getCommands().values().size()).isEqualTo(2);
		assertThat(Math.min(new ArrayList<>(analyser.getCommands().values()).get(0).getNbLocalCmds(), new ArrayList<>(analyser.getCommands().values()).get(1).getNbLocalCmds())).isEqualTo(1);
		assertThat(Math.max(new ArrayList<>(analyser.getCommands().values()).get(0).getNbLocalCmds(), new ArrayList<>(analyser.getCommands().values()).get(1).getNbLocalCmds())).isEqualTo(5);
	}

	@Test
	public void testClassListenerSwitchDefault() {
		analyser.addInputResource("src/test/resources/java/analysers/ActionListenerCondSwitchDefault.java");
		analyser.run();
		assertThat(analyser.getCommands().values().size()).isEqualTo(1);
		assertThat(analyser.getCommands().values().stream().mapToLong(c -> c.getNbTotalCmds()).sum()).isEqualTo(1L);
		assertThat(analyser.getCommands().values().iterator().next().getCommands().iterator().next().getConditions().get(0).effectiveStatmt instanceof CtBinaryOperator).isTrue();
	}

	@Test
	public void testClassListenerSwitchDefault2() {
		analyser.addInputResource("src/test/resources/java/analysers/ActionListenerCondSwitchDefault2.java");
		analyser.run();
		assertThat(analyser.getCommands().values().size()).isEqualTo(1);
		assertThat(analyser.getCommands().values().stream().mapToLong(c -> c.getNbTotalCmds()).sum()).isEqualTo(1L);
		assertThat(analyser.getCommands().values().iterator().next().getCommands().iterator().next().getConditions().get(0).effectiveStatmt instanceof CtLiteral).isTrue();
	}

	@Test
	public void testSimpleDispatchMethodNoBody() {
		analyser.addInputResource("src/test/resources/java/analysers/SimpleDispatchMethodNoBody.java");
		analyser.run();
		assertThat(analyser.getCommands().values().size()).isEqualTo(1);
		assertThat(analyser.getCommands().values().stream().mapToLong(c -> c.getNbTotalCmds()).sum()).isEqualTo(1L);
	}

	@Test
	public void testSimpleDispatchMethodNoBody2() {
		analyser.addInputResource("src/test/resources/java/analysers/SimpleDispatchMethodNoBody2.java");
		analyser.run();
		assertThat(analyser.getCommands().values().size()).isEqualTo(1);
		assertThat(analyser.getCommands().values().stream().mapToLong(c -> c.getNbTotalCmds()).sum()).isEqualTo(1L);
	}

	@Test
	public void testSimpleDispatchMethodNoBody2HasMainBlock() {
		analyser.addInputResource("src/test/resources/java/analysers/SimpleDispatchMethodNoBody2.java");
		analyser.run();
		assertThat(analyser.getCommands().values().stream().flatMap(c -> c.getCommands().stream()).collect(Collectors.toList()).get(0).getMainStatmtEntry().isPresent()).isTrue();
	}

	@Test
	public void testFinalBlockAllReturns() {
		analyser.addInputResource("src/test/resources/java/analysers/FinalBlockAllReturns.java");
		analyser.run();
		assertThat(analyser.getCommands().values().size()).isEqualTo(1);
		assertThat(analyser.getCommands().values().stream().mapToLong(c -> c.getNbTotalCmds()).sum()).isEqualTo(1L);
		assertThat(analyser.getCommands().values().iterator().next().getCommands().iterator().next().getLineEnd()).isEqualTo(10);
	}

	@Test
	public void testFinalBlockNoReturn() {
		analyser.addInputResource("src/test/resources/java/analysers/FinalBlockNoReturn.java");
		analyser.run();
		assertThat(analyser.getCommands().values().size()).isEqualTo(1);
		assertThat(analyser.getCommands().values().stream().mapToLong(c -> c.getNbTotalCmds()).sum()).isEqualTo(1L);
		assertThat(analyser.getCommands().values().iterator().next().getCommands().iterator().next().getNbLines()).isEqualTo(3);
	}

	@Test
	@Disabled
	public void testFinalBlockJustReturn() {
		analyser.addInputResource("src/test/resources/java/analysers/FinalBlockJustReturn.java");
		analyser.run();
		assertThat(analyser.getCommands().values().stream().mapToLong(c -> c.getNbTotalCmds()).sum()).isEqualTo(0L);
	}

	@Test
	public void testFinalBlockIsCatch() {
		analyser.addInputResource("src/test/resources/java/analysers/FinalBlockIsCatch.java");
		analyser.run();
		assertThat(analyser.getCommands().values().size()).isEqualTo(1);
		assertThat(analyser.getCommands().values().stream().mapToLong(c -> c.getNbTotalCmds()).sum()).isEqualTo(1L);
	}

	@Test
	public void testFinalBlockIsThrow() {
		analyser.addInputResource("src/test/resources/java/analysers/FinalBlockIsThrow.java");
		analyser.run();
		assertThat(analyser.getCommands().values().size()).isEqualTo(1);
		assertThat(analyser.getCommands().values().stream().mapToLong(c -> c.getNbTotalCmds()).sum()).isEqualTo(1L);
	}

	@Test
	public void testLambdaListenerHasMainCommandBlock() {
		analyser.addInputResource("src/test/resources/java/analysers/LambdaListenerHasMainCommandBlock.java");
		analyser.run();
		assertThat(analyser.getCommands().values().stream().flatMap(c -> c.getCommands().stream()).collect(Collectors.toList()).get(0).getMainStatmtEntry().isPresent()).isTrue();
	}

	@Test
	public void testLocalVarOutsideListener() {
		analyser.addInputResource("src/test/resources/java/analysers/LocalVarOutsideListener.java");
		analyser.run();
		assertThat(analyser.getCommands().values().size()).isEqualTo(1);
		assertThat(analyser.getCommands().values().stream().mapToLong(c -> c.getNbTotalCmds()).sum()).isEqualTo(3L);
		List<Command> cmds = analyser.getCommands().values().stream().flatMap(c -> c.getCommands().stream()).collect(Collectors.toList());
		assertThat(cmds.get(0).getMainStatmtEntry().isPresent()).isTrue();
		assertThat(cmds.get(1).getMainStatmtEntry().isPresent()).isTrue();
		assertThat(cmds.get(2).getMainStatmtEntry().isPresent()).isTrue();
	}

	@Test
	public void testSwitchCaseStrange() {
		analyser.addInputResource("src/test/resources/java/analysers/SwitchCaseStrange.java");
		analyser.run();
		assertThat(analyser.getCommands().values().size()).isEqualTo(1);
		assertThat(analyser.getCommands().values().stream().mapToLong(c -> c.getNbTotalCmds()).sum()).isEqualTo(1L);
	}

	@Test
	public void testActionListenerTwice() {
		analyser.addInputResource("src/test/resources/java/listeners/ActionListenerTwice.java");
		analyser.run();
		assertThat(analyser.getCommands().values().size()).isEqualTo(2);
		assertThat(analyser.getCommands().values().stream().mapToLong(c -> c.getNbTotalCmds()).sum()).isEqualTo(2L);
		List<Command> cmds = analyser.getCommands().values().stream().flatMap(c -> c.getCommands().stream()).collect(Collectors.toList());
		assertThat(cmds.get(0).getMainStatmtEntry().isPresent()).isTrue();
		assertThat(cmds.get(1).getMainStatmtEntry().isPresent()).isTrue();
	}

	@Test
	public void testEmptyDispatch() {
		analyser.addInputResource("src/test/resources/java/analysers/EmptyDispatch.java");
		analyser.run();
		assertThat(analyser.getCommands().values().size()).isEqualTo(1);
		assertThat(analyser.getCommands().values().stream().mapToLong(c -> c.getNbTotalCmds()).sum()).isEqualTo(1L);
		Command cmd = analyser.getCommands().values().stream().flatMap(c -> c.getCommands().stream()).collect(Collectors.toList()).get(0);
		assertThat(cmd.getMainStatmtEntry().get().getLineStart()).isEqualTo(11);
		assertThat(cmd.getMainStatmtEntry().get().getLineEnd()).isEqualTo(11);
	}

	@Test
	public void testSimpleDispatch2() {
		analyser.addInputResource("src/test/resources/java/analysers/SimpleDispatch2.java");
		analyser.run();
		assertThat(analyser.getCommands().values().size()).isEqualTo(1);
		assertThat(analyser.getCommands().values().stream().mapToLong(c -> c.getNbTotalCmds()).sum()).isEqualTo(1L);
		List<Command> cmds = analyser.getCommands().values().stream().flatMap(c -> c.getCommands().stream()).collect(Collectors.toList());
		assertThat(cmds.get(0).getMainStatmtEntry().get().getStatmts().get(0).getPosition().getLine()).isEqualTo(9);
	}

	@Test
	public void testCorrectStatements() {
		analyser.addInputResource("src/test/resources/java/analysers/CommandStatements.java");
		analyser.run();
		assertThat(analyser.getCommands().values().size()).isEqualTo(1);
		assertThat(analyser.getCommands().values().stream().mapToLong(c -> c.getNbTotalCmds()).sum()).isEqualTo(1L);
		assertThat(analyser.getCommands().values().stream().flatMap(c -> c.getCommands().stream()).iterator().next().getAllStatmts().size()).isEqualTo(4);
	}

	@Test
	public void testCorrectStatementsFromSwitch() {
		analyser.addInputResource("src/test/resources/java/refactoring/E.java");
		analyser.run();
		assertThat(analyser.getCommands().values().size()).isEqualTo(1);
		assertThat(analyser.getCommands().values().stream().mapToLong(c -> c.getNbTotalCmds()).sum()).isEqualTo(2L);
		assertThat(analyser.getCommands().values().stream().flatMap(c -> c.getCommands().stream()).iterator().next().getAllStatmts().size()).isEqualTo(4);
	}

	@Test
	public void testNotSameStatementsCollectedInCommand() {
		analyser.addInputResource("src/test/resources/java/refactoring/I.java");
		analyser.run();
		assertThat(analyser.getCommands().values().iterator().next().getCommand(0).getAllStatmts().stream().filter(stat -> stat.getPosition().getLine()==23).count()).isEqualTo(1L);
	}

	@Test
	public void testNestedStatementNotPartOfACommand() {
		analyser.addInputResource("src/test/resources/java/refactoring/I.java");
		analyser.run();
		assertThat(analyser.getCommands().values().iterator().next().getCommand(0).getAllStatmts().stream().filter(stat -> stat.getPosition().getLine()==26).count()).isEqualTo(1L);
	}

	@Test
	public void testStrangeListener() {
		analyser.addInputResource("src/test/resources/java/analysers/NoIfStatement.java");
		analyser.run();
		assertThat(analyser.getCommands().values().size()).isEqualTo(1);
		assertThat(analyser.getCommands().values().stream().mapToLong(c -> c.getNbTotalCmds()).sum()).isEqualTo(2L);
	}

	@Test
	public void testClassWithMultipleListeners() {
		analyser.addInputResource("src/test/resources/java/listeners/MultipleListener.java");
		analyser.run();
		assertThat(analyser.getCommands().values().size()).isEqualTo(2);
		assertThat(analyser.getCommands().values().stream().mapToLong(c -> c.getNbTotalCmds()).sum()).isEqualTo(3L);
	}

	@Test
	public void testNonDeterministActionCmd() {
		analyser.addInputResource("src/test/resources/java/refactoring/ListenerTab.java");
		analyser.run();
		assertThat(analyser.getCommands().values().size()).isEqualTo(1);
		assertThat(analyser.getCommands().values().stream().mapToLong(c -> c.getNbTotalCmds()).sum()).isEqualTo(2L);
	}

	@Test
	public void testSuperListenerLocalCmds() {
		analyser.addInputResource("src/test/resources/java/analysers/SuperActionListener.java");
		analyser.run();
		assertThat(analyser.getCommands().values().size()).isEqualTo(2);
		assertThat(analyser.getCommands().values().stream().mapToLong(c -> c.getNbLocalCmds()).sum()).isEqualTo(3L);
	}

	@Test
	public void testIrrelevantCommandStatements() {
		analyser.addInputResource("src/test/resources/java/analysers/IrrelevantCommandStatements.java");
		analyser.run();
		assertThat(analyser.getCommands().values().size()).isEqualTo(2);
		assertThat(analyser.getCommands().values().stream().mapToLong(c -> c.getNbTotalCmds()).sum()).isEqualTo(0L);
	}

	@Test
	public void testFalseDispatch() {
		analyser.addInputResource("src/test/resources/java/analysers/ActionListenerLambdaFalseDispatch.java");
		analyser.run();
		assertThat(analyser.getCommands().values().size()).isEqualTo(1);
		assertThat(analyser.getCommands().values().stream().mapToLong(c -> c.getNbTotalCmds()).sum()).isEqualTo(1L);
		List<Command> cmds = analyser.getCommands().values().stream().flatMap(c -> c.getCommands().stream()).collect(Collectors.toList());
		assertThat(cmds.get(0).getMainStatmtEntry().get().getLineStart()).isEqualTo(13);
		assertThat(cmds.get(0).getMainStatmtEntry().get().getLineEnd()).isEqualTo(13);
	}

	@Test
	public void testFalseDispatch2() {
		analyser.addInputResource("src/test/resources/java/analysers/ActionListenerLambdaFalseDispatch2.java");
		analyser.run();
		assertThat(analyser.getCommands().values().size()).isEqualTo(1);
		assertThat(analyser.getCommands().values().stream().mapToLong(c -> c.getNbTotalCmds()).sum()).isEqualTo(1L);
		List<Command> cmds = analyser.getCommands().values().stream().flatMap(c -> c.getCommands().stream()).collect(Collectors.toList());
		assertThat(cmds.get(0).getMainStatmtEntry().get().getLineStart()).isEqualTo(14);
		assertThat(cmds.get(0).getMainStatmtEntry().get().getLineEnd()).isEqualTo(16);
	}

	@Test
	public void testFalseDispatch3() {
		analyser.addInputResource("src/test/resources/java/analysers/ActionListenerFalseDispatch3.java");
		analyser.run();
		assertThat(analyser.getCommands().values().size()).isEqualTo(1);
		assertThat(analyser.getCommands().values().stream().mapToLong(c -> c.getNbTotalCmds()).sum()).isEqualTo(1L);
	}

	@Test
	public void testComplexConditionalStatements() {
		analyser.addInputResource("src/test/resources/java/analysers/ComplexConditionalStatements.java");
		analyser.run();
		assertThat(analyser.getCommands().values().size()).isEqualTo(1);
		assertThat(analyser.getCommands().values().stream().mapToLong(c -> c.getNbTotalCmds()).sum()).isEqualTo(2L);
	}

	@Test
	@Disabled
	public void testComplexConditionalStatements3() {
		analyser.addInputResource("src/test/resources/java/analysers/ComplexConditionalStatements3.java");
		analyser.run();
		assertThat(analyser.getCommands().values().size()).isEqualTo(1);
		assertThat(analyser.getCommands().values().stream().mapToLong(c -> c.getNbTotalCmds()).sum()).isEqualTo(3L);
	}

	@Test
	public void testComplexConditionalStatements4() {
		analyser.addInputResource("src/test/resources/java/analysers/ComplexConditionalStatements4.java");
		analyser.run();
		assertThat(analyser.getCommands().values().size()).isEqualTo(1);
		assertThat(analyser.getCommands().values().stream().mapToLong(c -> c.getNbTotalCmds()).sum()).isEqualTo(2L);
	}

	@Test
	public void testComplexConditionalStatementsPosition() {
		analyser.addInputResource("src/test/resources/java/analysers/ComplexConditionalStatements.java");
		analyser.run();
		List<Command> cmds = analyser.getCommands().values().stream().flatMap(c -> c.getCommands().stream()).collect(Collectors.toList());
		assertThat(cmds.get(1).getMainStatmtEntry().get().getLineStart()).isEqualTo(27);
		assertThat(cmds.get(1).getMainStatmtEntry().get().getLineEnd()).isEqualTo(30);
	}

	@Test
	public void testComplexBlob() {
		analyser.addInputResource("src/test/resources/java/refactoring/ComplexBlobs.java");
		analyser.run();
		assertThat(analyser.getCommands().values().size()).isEqualTo(1);
		assertThat(analyser.getCommands().values().stream().mapToLong(c -> c.getNbTotalCmds()).sum()).isEqualTo(6L);
	}

	@Test
	public void testSwitchCaseWithLog() {
		analyser.addInputResource("src/test/resources/java/analysers/SwitchCaseWithLog.java");
		analyser.run();
		assertThat(analyser.getCommands().values().size()).isEqualTo(1);
		assertThat(analyser.getCommands().values().stream().mapToLong(c -> c.getNbTotalCmds()).sum()).isEqualTo(1L);
	}

	@Test
	public void testUndirectActionCmd() {
		analyser.addInputResource("src/test/resources/java/analysers/UndirectActionCmd.java");
		analyser.run();
		assertThat(analyser.getCommands().values().size()).isEqualTo(1);
		assertThat(analyser.getCommands().values().stream().mapToLong(c -> c.getNbTotalCmds()).sum()).isEqualTo(3L);
	}

	@Test
	public void testMixedSwitchAndIfStatements() {
		analyser.addInputResource("src/test/resources/java/analysers/MixedSwitchAndIf.java");
		analyser.run();
		assertThat(analyser.getCommands().values().size()).isEqualTo(1);
		assertThat(analyser.getCommands().values().stream().mapToLong(c -> c.getNbTotalCmds()).sum()).isEqualTo(3L);
	}

	@Test
	public void testMixedSwitchAndIfStatementsCmdPosition() {
		analyser.addInputResource("src/test/resources/java/analysers/MixedSwitchAndIf.java");
		analyser.run();
		List<Command> cmds = analyser.getCommands().values().stream().flatMap(c -> c.getCommands().stream()).sorted(Comparator.comparing(cmd -> cmd.getLineStart())).collect(Collectors.toList());
		assertThat(cmds.get(0).getMainStatmtEntry().get().getLineStart()).isEqualTo(19);
		assertThat(cmds.get(0).getMainStatmtEntry().get().getLineEnd()).isEqualTo(20);
		assertThat(cmds.get(1).getMainStatmtEntry().get().getLineStart()).isEqualTo(23);
		assertThat(cmds.get(1).getMainStatmtEntry().get().getLineEnd()).isEqualTo(23);
		assertThat(cmds.get(2).getMainStatmtEntry().get().getLineStart()).isEqualTo(27);
		assertThat(cmds.get(2).getMainStatmtEntry().get().getLineEnd()).isEqualTo(28);
	}


	@Test
	public void testCommandComposedOfLocalVarAssignmentOnly() {
		analyser.addInputResource("src/test/resources/java/analysers/FollowedConditionals.java");
		analyser.run();
		Assertions.assertThat(analyser.getCommands().values()).hasSize(1);
		Assertions.assertThat(analyser.getCommands().values().stream().mapToLong(c -> c.getNbTotalCmds()).sum()).isEqualTo(3L);
	}

	@Test
	public void testLogger() {
		analyser.addInputResource("src/test/resources/java/analysers/Logger.java");
		analyser.run();
		assertThat(analyser.getCommands().values().size()).isEqualTo(1);
		assertThat(analyser.getCommands().values().stream().mapToLong(c -> c.getNbTotalCmds()).sum()).isEqualTo(2L);
	}

	@Test
	public void testSwitchCasesSameLine() {
		analyser.addInputResource("src/test/resources/java/analysers/SwitchCasesSameLine.java");
		analyser.run();
		assertThat(analyser.getCommands().values().size()).isEqualTo(1);
		assertThat(analyser.getCommands().values().stream().mapToLong(c -> c.getNbTotalCmds()).sum()).isEqualTo(6L);
	}


	@Test
	public void testSwitchCasesStartLine() {
		analyser.addInputResource("src/test/resources/java/analysers/SwitchCasesSameLine.java");
		analyser.run();
		List<Command> cmds = analyser.getCommands().values().stream().flatMap(c -> c.getCommands().stream()).sorted(Comparator.comparing(cmd -> cmd.getLineStart())).collect(Collectors.toList());
		assertThat(cmds.get(0).getAllLocalStatmtsOrdered().get(0).getPosition().getLine()).isEqualTo(47);
		assertThat(cmds.get(1).getAllLocalStatmtsOrdered().get(0).getPosition().getLine()).isEqualTo(47);
		assertThat(cmds.get(2).getAllLocalStatmtsOrdered().get(0).getPosition().getLine()).isEqualTo(47);
		assertThat(cmds.get(3).getAllLocalStatmtsOrdered().get(0).getPosition().getLine()).isEqualTo(47);
	}

	@Test
	public void testSharedInheritedCommandsEachListener() {
		analyser.addInputResource("src/test/resources/java/analysers/SharedInheritedCommands.java");
		analyser.run();
		assertThat(analyser.getCommands().values().size()).isEqualTo(3);
		List<Map.Entry<CtExecutable<?>, UIListener>> listeners =
			analyser.getCommands().entrySet().stream().sorted(Comparator.comparing(entry -> entry.getKey().getPosition().getLine())).collect(Collectors.toList());
		assertThat(listeners.get(0).getValue().getNbTotalCmds()).isEqualTo(1);
		assertThat(listeners.get(1).getValue().getNbTotalCmds()).isEqualTo(2);
		assertThat(listeners.get(2).getValue().getNbTotalCmds()).isEqualTo(3);
	}

	@Test
	public void testSharedInheritedCommandsTotalListeners() {
		analyser.addInputResource("src/test/resources/java/analysers/SharedInheritedCommands.java");
		analyser.run();
		assertThat(analyser.getCommands().values().stream().mapToLong(c -> c.getNbLocalCmds()).sum()).isEqualTo(4L);
	}

	@Test
	public void testSuperSwitchActionListenerGoodStatements() {
		analyser.addInputResource("src/test/resources/java/refactoring/SuperSwitchActionListener.java");
		analyser.run();
		List<Command> cmds = analyser.getCommands().values().stream().flatMap(c -> c.getCommands().stream()).collect(Collectors.toList());
		assertThat(cmds.get(0).getAllLocalStatmtsOrdered().size()).isEqualTo(2);
		assertThat(cmds.get(1).getAllLocalStatmtsOrdered().size()).isEqualTo(2);
	}

	@Test
	public void testBreakAtEnd() {
		analyser.addInputResource("src/test/resources/java/refactoring/BreakAtEnd.java");
		analyser.run();
		Command cmd = analyser.getCommands().values().stream().flatMap(c -> c.getCommands().stream()).collect(Collectors.toList()).get(0);
		assertThat(cmd.getStatements().size()).isEqualTo(1);
	}

	@Test
	public void testAssert() {
		analyser.addInputResource("src/test/resources/java/analysers/Assert.java");
		analyser.run();
		assertThat(analyser.getCommands().values().size()).isEqualTo(1);
		assertThat(analyser.getCommands().values().stream().mapToLong(c -> c.getNbTotalCmds()).sum()).isEqualTo(2L);
	}

	@Test
	public void testRecursiveAnalysis() {
		analyser.addInputResource("src/test/resources/java/analysers/RecursiveAnalysis.java");
		analyser.run();
		assertThat(analyser.getCommands().values().size()).isEqualTo(1);
		assertThat(analyser.getCommands().values().stream().mapToLong(c -> c.getNbTotalCmds()).sum()).isEqualTo(1L);
	}
}
