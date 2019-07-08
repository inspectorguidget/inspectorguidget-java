package fr.inria.inspectorguidget.api.analyser;

import fr.inria.inspectorguidget.api.TestInspectorGuidget;
import fr.inria.inspectorguidget.api.processor.InspectorGuidgetProcessor;
import fr.inria.inspectorguidget.api.processor.WidgetProcessor;
import fr.inria.inspectorguidget.internal.helper.SpoonStructurePrinter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

public class TestCommandWidgetFinder {
	private CommandAnalyser cmdAnalyser;
	private WidgetProcessor widgetProc;
	private CommandWidgetFinder finder;
	Map<Command, CommandWidgetFinder.WidgetFinderEntry> results;

	@BeforeAll
	public static void setUpBeforeClass() {
		InspectorGuidgetProcessor.LOG.addHandler(TestInspectorGuidget.HANDLER_FAIL);
	}

	@BeforeEach
	public void setUp() {
		cmdAnalyser = new CommandAnalyser();
		widgetProc = new WidgetProcessor(true);
	}

	@AfterEach
	public void tearsDown() {
		if(TestInspectorGuidget.SHOW_MODEL) {
			final SpoonStructurePrinter printer = new SpoonStructurePrinter();
			printer.scan(Collections.singletonList(cmdAnalyser.getModelBuilder().getFactory().Package().getRootPackage()));
		}
	}

	private void initTest(final String... paths) {
		Stream.of(paths).forEach(p -> cmdAnalyser.addInputResource(p));
		cmdAnalyser.run();

		final InspectorGuidetAnalyser launcher = new InspectorGuidetAnalyser(Collections.singletonList(widgetProc), cmdAnalyser.getModelBuilder());
		launcher.process();

		finder = new CommandWidgetFinder(
			cmdAnalyser.getCommands().values().parallelStream().flatMap(s -> s.getCommands().stream()).collect(Collectors.toList()),
			widgetProc.getWidgetUsages());
		finder.process();
		results = finder.getResults();
	}

	@Test
	public void testAnonClassOnSingleFieldWidgetNoCond() {
		initTest("src/test/resources/java/widgetsIdentification/AnonClassOnSingleFieldWidgetNoCond.java");
		assertThat( results.size()).isEqualTo(1);
		assertThat(new ArrayList<>(results.values()).get(0).getRegisteredWidgets().iterator().next().widgetVar.getSimpleName()).isEqualTo("b");
	}

	@Test
	public void testLambdaOnSingleFieldWidgetNoCond() {
		initTest("src/test/resources/java/widgetsIdentification/LambdaOnSingleFieldWidgetNoCond.java");
		assertThat(results.size()).isEqualTo(1);
		assertThat(new ArrayList<>(results.values()).get(0).getWidgetUsages(results.values()).size()).isEqualTo(1L);
		assertThat(new ArrayList<>(results.values()).get(0).getRegisteredWidgets().iterator().next().widgetVar.getSimpleName()).isEqualTo("b");
	}

	@Test
	public void testAnonClassOnSingleLocalVarWidgetNoCond() {
		initTest("src/test/resources/java/widgetsIdentification/AnonClassOnSingleLocalVarWidgetNoCond.java");
		assertThat(results.size()).isEqualTo(1);
		assertThat(new ArrayList<>(results.values()).get(0).getWidgetUsages(results.values()).size()).isEqualTo(1L);
		assertThat(new ArrayList<>(results.values()).get(0).getRegisteredWidgets().iterator().next().widgetVar.getSimpleName()).isEqualTo("b");
	}

	@Test
	public void testAnonClassOnSingleFieldWidgetEqualCond() {
		initTest("src/test/resources/java/widgetsIdentification/AnonClassOnFieldWidgetsEqualCond.java");
		assertThat(results.size()).isEqualTo(1);
		assertThat(new ArrayList<>(results.values()).get(0).getWidgetUsages(results.values()).size()).isEqualTo(2);
		assertThat(new ArrayList<>(results.values()).get(0).getRegisteredWidgets().iterator().next().widgetVar.getSimpleName()).isEqualTo("b");
		assertThat(new ArrayList<>(results.values()).get(0).getWidgetsUsedInConditions().iterator().next().widgetVar.getSimpleName()).isEqualTo("a");
	}

	@Test
	public void testLambdaOnSingleFieldWidgetEqualCond() {
		initTest("src/test/resources/java/widgetsIdentification/LambdaOnFieldWidgetsEqualCond.java");
		assertThat(results.size()).isEqualTo(1);
		final Set<WidgetProcessor.WidgetUsage> res = new ArrayList<>(results.values()).get(0).getWidgetUsages(results.values());
		System.out.println(res);
		assertThat(res.size()).isEqualTo(2);
		assertThat(new ArrayList<>(results.values()).get(0).getRegisteredWidgets().iterator().next().widgetVar.getSimpleName()).isEqualTo("b");
		assertThat(new ArrayList<>(results.values()).get(0).getWidgetsUsedInConditions().iterator().next().widgetVar.getSimpleName()).isEqualTo("a");
	}

	@Test
	public void testClassSingleWidgetNoCond() {
		initTest("src/test/resources/java/widgetsIdentification/ClassSingleWidgetNoCond.java");
		assertThat(results.size()).isEqualTo(1);
		assertThat(new ArrayList<>(results.values()).get(0).getWidgetUsages(results.values()).size()).isEqualTo(1);
		assertThat(new ArrayList<>(results.values()).get(0).getRegisteredWidgets().iterator().next().widgetVar.getSimpleName()).isEqualTo("fooo");
	}

	@Test
	public void testClassInheritanceSingleWidgetNoCond() {
		initTest("src/test/resources/java/widgetsIdentification/ClassInheritanceSingleWidgetNoCond.java");
		assertThat(results.size()).isEqualTo(1);
		assertThat(new ArrayList<>(results.values()).get(0).getWidgetUsages(results.values()).size()).isEqualTo(1);
		assertThat(new ArrayList<>(results.values()).get(0).getRegisteredWidgets().iterator().next().widgetVar.getSimpleName()).isEqualTo("fooo");
	}

	@Test
	public void testWidgetClassListener() {
		initTest("src/test/resources/java/widgetsIdentification/WidgetClassListener.java");
		assertThat(results.size()).isEqualTo(1);
		assertThat(new ArrayList<>(results.values()).get(0).getWidgetUsages(results.values()).size()).isEqualTo(0);
		assertThat(new ArrayList<>(results.values()).get(0).getWidgetClasses().isPresent()).isTrue();
		assertThat(new ArrayList<>(results.values()).get(0).getWidgetClasses().get().getSimpleName()).isEqualTo("Foo");
	}

	@Test
	public void testFalseNegativeWidgetClassListener() {
		initTest("src/test/resources/java/widgetsIdentification/FalsePositiveThisListener.java");
		assertThat(results.size()).isEqualTo(1);
		assertThat(new ArrayList<>(results.values()).get(0).getWidgetUsages(results.values()).size()).isEqualTo(0);
	}


	@Test
	public void testClassListenerInheritance() {
		initTest("src/test/resources/java/widgetsIdentification/ClassListenerInheritance.java");
		assertThat(results.size()).isEqualTo(2);;

		final List<Map.Entry<Command, CommandWidgetFinder.WidgetFinderEntry>> entries = results
			.entrySet()
			.stream()
			.sorted(Comparator.comparingInt(a -> a.getKey().getExecutable().getPosition().getLine()))
			.collect(Collectors.toList());

		assertThat(entries.get(0).getValue().getWidgetUsages(results.values()).size()).isEqualTo(1);
		assertThat(entries.get(0).getValue().getWidgetUsages(results.values()).iterator().next().widgetVar.getSimpleName()).isEqualTo("fooo");

		assertThat(entries.get(1).getValue().getWidgetUsages(results.values()).size()).isEqualTo(1);
		assertThat(entries.get(1).getValue().getWidgetUsages(results.values()).iterator().next().widgetVar.getSimpleName()).isEqualTo("bar");
	}


	@Test
	public void testClassListenerExternal() {
		initTest("src/test/resources/java/widgetsIdentification/ClassListenerExternal.java");
		assertThat(results.size()).isEqualTo(2);
		assertThat(new ArrayList<>(results.values()).get(0).getWidgetUsages(results.values()).size()).isEqualTo(1);
		assertThat(new ArrayList<>(results.values()).get(1).getWidgetUsages(results.values()).size()).isEqualTo(1);
	}

	@Test
	public void testClassListenerExternalString() {
		initTest("src/test/resources/java/widgetsIdentification/ClassListenerExternalString.java");
		assertThat(results.size()).isEqualTo(2);
		assertThat(new ArrayList<>(results.values()).get(0).getWidgetUsages(results.values()).size()).isEqualTo(1);
		assertThat(new ArrayList<>(results.values()).get(1).getWidgetUsages(results.values()).size()).isEqualTo(1);
	}

	@Test
	public void testClassListenerExternalLocalVar() {
		initTest("src/test/resources/java/widgetsIdentification/ClassListenerExternalLocalVar.java");
		assertThat(results.size()).isEqualTo(2);
		assertThat(new ArrayList<>(results.values()).get(0).getWidgetUsages(results.values()).size()).isEqualTo(1);
		assertThat(new ArrayList<>(results.values()).get(1).getWidgetUsages(results.values()).size()).isEqualTo(1);
	}

	@Test
	public void testClassListenerExternal2() {
		initTest("src/test/resources/java/widgetsIdentification/ClassListenerExternal2.java");
		assertThat(results.size()).isEqualTo(3);
		assertThat(new ArrayList<>(results.values()).get(0).getWidgetUsages(results.values()).size()).isEqualTo(1);
		assertThat(new ArrayList<>(results.values()).get(1).getWidgetUsages(results.values()).size()).isEqualTo(1);
		assertThat(new ArrayList<>(results.values()).get(2).getWidgetUsages(results.values()).size()).isEqualTo(1);
	}

	@Test
	public void testMenuWidgetAndListener() {
		initTest("src/test/resources/java/widgetsIdentification/MenuWidgetAndListener.java");
		assertThat(results.size()).isEqualTo(3);
		assertThat(new ArrayList<>(results.values()).get(0).getWidgetUsages(results.values()).size()).isEqualTo(1);
		assertThat(new ArrayList<>(results.values()).get(1).getWidgetUsages(results.values()).size()).isEqualTo(1);
		assertThat(new ArrayList<>(results.values()).get(2).getWidgetUsages(results.values()).size()).isEqualTo(1);
	}

	@Test
	public void testAnotherExample() {
		initTest("src/test/resources/java/widgetsIdentification/AnotherExample.java");
		assertThat(results.size()).isEqualTo(1);
		assertThat(new ArrayList<>(results.values()).get(0).getWidgetUsages(results.values()).size()).isEqualTo(1);
	}

	@Test
	public void testUseSameStringVar() {
		initTest("src/test/resources/java/widgetsIdentification/UseSameStringVar.java");
		assertThat(results.size()).isEqualTo(1);
		assertThat(new ArrayList<>(results.values()).get(0).getWidgetUsages(results.values()).size()).isEqualTo(1);
	}

	@Test
	public void testAnotherExample2() {
		initTest("src/test/resources/java/widgetsIdentification/AnotherExample2.java");
		assertThat(results.size()).isEqualTo(1);
		assertThat(new ArrayList<>(results.values()).get(0).getWidgetUsages(results.values()).size()).isEqualTo(1);
	}

	@Test
	public void testAnotherExample3() {
		initTest("src/test/resources/java/widgetsIdentification/AnotherExample3.java");
		assertThat(results.size()).isEqualTo(2);
		assertThat(new ArrayList<>(results.values()).get(0).getWidgetUsages(results.values()).size()).isEqualTo(1);
	}

	@Test
	public void testAnotherExample3CorrectStatementsIndentification() {
		initTest("src/test/resources/java/widgetsIdentification/AnotherExample3.java");
		assertThat(new ArrayList<>(results.values()).get(0).getWidgetUsages(results.values()).size()).isEqualTo(1);
		assertThat(new ArrayList<>(results.values()).get(1).getWidgetUsages(results.values()).size()).isEqualTo(1);
	}

	@Test
	public void testListenerRegisterOnInvocation() {
		initTest("src/test/resources/java/analysers/ListenerRegisterOnInvocation.java");
		assertThat(results.size()).isEqualTo(1);
		assertThat(new ArrayList<>(results.values()).get(0).getWidgetUsages(results.values()).size()).isEqualTo(1);
	}

	@Test
	public void testAnotherExample4() {
		initTest("src/test/resources/java/widgetsIdentification/AnotherExample4.java");
		assertThat(results.size()).isEqualTo(2);
		assertThat(new ArrayList<>(results.values()).get(0).getWidgetUsages(results.values()).size()).isEqualTo(1);
		assertThat(new ArrayList<>(results.values()).get(1).getWidgetUsages(results.values()).size()).isEqualTo(1);
	}

	@Test
	public void testFilterOutRegistrationWidgetUsingVars() {
		initTest("src/test/resources/java/widgetsIdentification/FilterOutRegistrationWidgetUsingVars.java");
		assertThat(new ArrayList<>(results.values()).get(0).getWidgetUsages(results.values()).size()).isEqualTo(1);
	}

	@Test
	public void testFilterOutRegistrationWidgetUsingLiterals() {
		initTest("src/test/resources/java/widgetsIdentification/FilterOutRegistrationWidgetUsingLiterals.java");
		assertThat(new ArrayList<>(results.values()).get(0).getWidgetUsages(results.values()).size()).isEqualTo(1);
	}

	@Test
	public void testFilterOutRegistrationWidgetUsingWidgetVars() {
		initTest("src/test/resources/java/widgetsIdentification/FilterOutRegistrationWidgetUsingWidgetVars.java");
		assertThat(new ArrayList<>(results.values()).get(0).getWidgetUsages(results.values()).size()).isEqualTo(1);
	}

	@Test
	public void testInsertPSTricksCodeFrame() {
		initTest("src/test/resources/java/widgetsIdentification/InsertPSTricksCodeFrame.java");
		assertThat(new ArrayList<>(results.values()).get(0).getWidgetUsages(results.values()).size()).isEqualTo(1);
		assertThat(new ArrayList<>(results.values()).get(1).getWidgetUsages(results.values()).size()).isEqualTo(1);
		assertThat(new ArrayList<>(results.values()).get(2).getWidgetUsages(results.values()).size()).isEqualTo(1);
		assertThat(new ArrayList<>(results.values()).get(3).getWidgetUsages(results.values()).size()).isEqualTo(1);
	}

	@Test
	public void testAnotherExample5() {
		initTest("src/test/resources/java/widgetsIdentification/AnotherExample5.java");
		assertThat(new ArrayList<>(results.values()).get(0).getWidgetUsages(results.values()).size()).isEqualTo(1);
		assertThat(new ArrayList<>(results.values()).get(1).getWidgetUsages(results.values()).size()).isEqualTo(1);
	}

	@Test
	public void testWidgetsWithSameName() {
		initTest("src/test/resources/java/widgetsIdentification/WidgetsWithSameName.java");
		assertThat(new ArrayList<>(results.values()).get(0).getWidgetUsages(results.values()).size()).isEqualTo(1);
		assertThat(new ArrayList<>(results.values()).get(1).getWidgetUsages(results.values()).size()).isEqualTo(1);
	}

	@Test
	@Disabled
	public void testNonDeterministActionCmd() {
		initTest("src/test/resources/java/refactoring/ListenerTab.java");
		assertThat(new ArrayList<>(results.values()).get(0).getWidgetUsages(results.values()).size()).isEqualTo(1);
		assertThat(new ArrayList<>(results.values()).get(1).getWidgetUsages(results.values()).size()).isEqualTo(1);
	}

	@Test
	public void testNoMatchingWidgetButTwoCandidates() {
		initTest("src/test/resources/java/refactoring/RefactoringCommandNotPossible.java");
		final List<Map.Entry<Command, CommandWidgetFinder.WidgetFinderEntry>> entries =
			results.entrySet().stream().sorted(Comparator.comparing(entry -> entry.getKey().getLineStart())).collect(Collectors.toList());
		assertThat(entries.get(0).getValue().getWidgetUsages(results.values()).size()).isEqualTo(0);
		assertThat(entries.get(1).getValue().getWidgetUsages(results.values()).size()).isEqualTo(0);
		assertThat(entries.get(2).getValue().getWidgetUsages(results.values()).size()).isEqualTo(1);
		assertThat(entries.get(3).getValue().getWidgetUsages(results.values()).size()).isEqualTo(1);
	}

	@Test
	public void testSuperSwitchActionListener() {
		initTest("src/test/resources/java/refactoring/SuperSwitchActionListener.java");
		final List<Map.Entry<Command, CommandWidgetFinder.WidgetFinderEntry>> entries =
			results.entrySet().stream().sorted(Comparator.comparing(entry -> entry.getKey().getLineStart())).collect(Collectors.toList());
		assertThat(entries.get(0).getValue().getWidgetUsages(results.values()).size()).isEqualTo(1);
		assertThat(entries.get(1).getValue().getWidgetUsages(results.values()).size()).isEqualTo(1);
	}
}
