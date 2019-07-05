package fr.inria.inspectorguidget.api;

import fr.inria.inspectorguidget.data.UIData;

public interface UIDataExtractor {
	/**
	 * Launches the analysis
	 * @return The UI data extracted from the analysed code.
	 */
	UIData extractUIData();

	/**
	 * Adds an input resource to be processed by Spoon (either a file or a folder).
	 */
	void addInputResource(String file);

	void setSourceClasspath(final String ... args);
}
