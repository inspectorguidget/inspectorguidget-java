package fr.inria.inspectorguidget.api.processor;

import fr.inria.inspectorguidget.api.TestInspectorGuidget;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;


public class TestWidgetProcessor extends TestInspectorGuidget<WidgetProcessor> {
	private WidgetProcessor wproc;

	@Override
	@BeforeEach
	public void setUp() {
		super.setUp();
	}

	@BeforeAll
	public static void setUpBeforeClass() {
		InspectorGuidgetProcessor.LOG.addHandler(HANDLER_FAIL);
	}


	@Override
	public List<WidgetProcessor> createProcessor() {
		wproc = new WidgetProcessor(true);
		return Collections.singletonList(wproc);
	}

	@Test
	public void testWidgetsAsExplicitAttr() {
		run("src/test/resources/java/widgets/WidgetAsStdAttr.java");
		assertThat(wproc.getWidgetUsages().size()).isEqualTo(3);
	}

	@Test
	public void testWidgetsAsExplicitAttrConstructor() {
		run("src/test/resources/java/widgets/WidgetAsStdAttr.java");
		assertThat(wproc.getWidgetUsages().stream().filter(u -> u.creation.isPresent()).count()).isEqualTo(1L);
	}

	@Test
	public void testWidgetsAsListAttr() {
		run("src/test/resources/java/widgets/WidgetAsListAttr.java");
		assertThat(wproc.getWidgetUsages().size()).isEqualTo(0);
	}

	@Test
	public void testWidgetsConstructorObject() {
		run("src/test/resources/java/widgets/WidgetConstructorObject.java");
		assertThat(wproc.getWidgetUsages().size()).isEqualTo(1);
	}

	@Test
	public void testWidgetsConstructorListObject() {
		run("src/test/resources/java/widgets/WidgetConstructorListObject.java");
		assertThat(wproc.getWidgetUsages().size()).isEqualTo(0);
	}

	@Test
	public void testWidgetsConstructorContainer() {
		run("src/test/resources/java/widgets/WidgetConstructorContainer.java");
		assertThat(wproc.getWidgetUsages().size()).isEqualTo(1);
		assertThat(new ArrayList<>(wproc.getWidgetUsages()).get(0).creation.isPresent()).isTrue();
		assertThat(wproc.getRefWidgets().size()).isEqualTo(1);
	}

	@Test
	public void testWidgetsConstructorContainerUsage() {
		run("src/test/resources/java/widgets/WidgetConstructorContainer.java");
		assertThat(new ArrayList<>(wproc.getWidgetUsages()).get(0).accesses.size()).isEqualTo(1); // The panel is used to add the window
	}

	@Test
	public void testWidgetsConstructorObjectUndirect() {
		run("src/test/resources/java/widgets/WidgetConstructorObjectUndirect.java");
		assertThat(wproc.getWidgetUsages().size()).isEqualTo(1);
	}

	@Test
	public void testWidgetAsClass() {
		run("src/test/resources/java/widgets/WidgetAsClass.java");
		assertThat(wproc.getWidgetUsages().size()).isEqualTo(2);
	}


	@Test
	public void testWidgetsConstructorObjectFunction() {
		run("src/test/resources/java/widgets/WidgetConstructorObjectFunction.java",
				"src/test/resources/java/widgets/WidgetConstructorObjectFunction2.java");
		assertThat(wproc.getWidgetUsages().size()).isEqualTo(4);
	}

	@Test
	public void testWidgetsConstructorObjectFunction3() {
		run("src/test/resources/java/widgets/WidgetConstructorObjectFunction3.java");
		assertThat(wproc.getWidgetUsages().size()).isEqualTo(1);
		assertThat(wproc.getRefWidgets().size()).isEqualTo(1);
	}

	@Test
	public void testWidgetUsages() {
		run("src/test/resources/java/widgetsIdentification/ClassListenerExternal.java");
		assertThat(wproc.getWidgetUsages().size()).isEqualTo(2);
		assertThat(new ArrayList<>(wproc.getWidgetUsages()).get(0).accesses.size()).isEqualTo(2);
		assertThat(new ArrayList<>(wproc.getWidgetUsages()).get(1).accesses.size()).isEqualTo(2);
	}

	@Test
	public void testWidgetAsLocalVarAddedToContainer() {
		run("src/test/resources/java/widgetsIdentification/ClassListenerExternal2.java");
		assertThat(wproc.getWidgetUsages().size()).isEqualTo(5);
		assertThat(wproc.getRefWidgets().size()).isEqualTo(0);
		final List<WidgetProcessor.WidgetUsage> usages = wproc.getWidgetUsages()
			.stream()
			.filter(u -> u.creation.isPresent())
			.sorted(Comparator.comparingInt(a -> a.creation.orElseThrow().getPosition().getLine()))
			.collect(Collectors.toList());
		assertThat(usages.get(0).creation.orElseThrow().getPosition().getLine()).isEqualTo(27);
		assertThat(usages.get(1).creation.orElseThrow().getPosition().getLine()).isEqualTo(29);
		assertThat(usages.get(2).creation.orElseThrow().getPosition().getLine()).isEqualTo(35);
		assertThat(usages.get(3).creation.orElseThrow().getPosition().getLine()).isEqualTo(40);
	}

	@Test
	public void testWidgetAsLocalVarAddedToContainerAccesses() {
		run("src/test/resources/java/widgetsIdentification/ClassListenerExternal2.java");
		final List<WidgetProcessor.WidgetUsage> usages = wproc.getWidgetUsages()
			.stream()
			.filter(u -> u.creation.isPresent())
			.sorted(Comparator.comparingInt(a -> a.creation.orElseThrow().getPosition().getLine()))
			.collect(Collectors.toList());
		assertThat(usages.get(0).accesses.size()).isEqualTo(4);
		assertThat(usages.get(1).accesses.size()).isEqualTo(3);
		assertThat(usages.get(2).accesses.size()).isEqualTo(3);
		assertThat(usages.get(3).accesses.size()).isEqualTo(3);
	}

	@Test
	public void testAnotherExample3CorrectStatementsIndentification() {
		run("src/test/resources/java/widgetsIdentification/AnotherExample3.java");
		final List<WidgetProcessor.WidgetUsage> usages = wproc.getWidgetUsages()
			.stream()
			.filter(u -> u.creation.isPresent())
			.sorted(Comparator.comparingInt(a -> a.creation.orElseThrow().getPosition().getLine()))
			.collect(Collectors.toList());
		assertThat(wproc.getWidgetUsages().size()).isEqualTo(4);
		assertThat(wproc.getRefWidgets().size()).isEqualTo(0);
		assertThat(usages.get(0).accesses.size()).isEqualTo(2);
		assertThat(usages.get(1).accesses.size()).isEqualTo(2);
		assertThat(usages.get(2).accesses.size()).isEqualTo(0);
	}

	@Test
	public void testAnotherExample5() {
		run("src/test/resources/java/widgetsIdentification/AnotherExample5.java");
		assertThat(wproc.getWidgetUsages().size()).isEqualTo(2);
	}


	@Test
	public void testWidgetsWithSameName() {
		run("src/test/resources/java/widgetsIdentification/WidgetsWithSameName.java");
		assertThat(wproc.getWidgetUsages().size()).isEqualTo(2);
	}

	@Test
	public void testGoodNumberOfUsages() {
		run("src/test/resources/java/refactoring/SuperSwitchActionListener.java");
		assertThat(wproc.getWidgetUsages().size()).isEqualTo(2);
	}

	@Test
	public void testWidgetCreatedInFunction() {
		run("src/test/resources/java/widgetsIdentification/WidgetCreatedInFunction.java");
		assertThat(wproc.getWidgetUsages().size()).isEqualTo(2);
	}
}
