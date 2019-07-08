package fr.inria.inspectorguidget.api.analyser;

import com.beust.klaxon.Klaxon;
import fr.inria.inspectorguidget.data.UIData;
import org.apache.log4j.Level;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

public class TestUIDataAnalyser {
	UIDataAnalyser analyser;

	@BeforeAll
	static void logOff() {
		spoon.Launcher.LOGGER.setLevel(Level.ERROR);
	}

	@BeforeEach
	void setup() {
		analyser = new UIDataAnalyser();
	}

	@Disabled
	@Test
	void testArgoUML() {
		analyser.setSourceClasspath("/media/data/dev/IST18-xp/repoAnalysisBlob/argouml/lib/antlrall-2.7.2.jar",
			"/media/data/dev/IST18-xp/repoAnalysisBlob/argouml/lib/log4j-1.2.6.jar",
			"/media/data/dev/IST18-xp/repoAnalysisBlob/argouml/lib/gef-0.12.1.jar",
			"/media/data/dev/IST18-xp/repoAnalysisBlob/argouml/lib/ocl-argo-1.1.jar",
			"/media/data/dev/IST18-xp/repoAnalysisBlob/argouml/lib/toolbar-1.3.jar",
			"/media/data/dev/IST18-xp/repoAnalysisBlob/argouml/lib/swidgets-0.1.4.jar",
			"/media/data/dev/IST18-xp/repoAnalysisBlob/argouml/lib/commons-logging-1.0.2.jar",
			"/media/data/dev/IST18-xp/repoAnalysisBlob/argouml/src/model/build/classes");

		analyser.addInputResource("/media/data/dev/IST18-xp/repoAnalysisBlob/argouml/src_new/");

		final UIData uiData = analyser.extractUIData();

		System.out.println(new Klaxon().toJsonString(uiData, null));
	}
}
