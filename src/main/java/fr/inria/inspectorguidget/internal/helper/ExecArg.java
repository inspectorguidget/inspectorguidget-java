package fr.inria.inspectorguidget.internal.helper;

import fr.inria.inspectorguidget.api.analyser.InspectorGuidetAnalyser;
import java.util.Arrays;
import java.util.stream.Collectors;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class ExecArg {
	public static final String EXEC_CP_ARG = "-c";
	public static final String EXEC_SRC_ARG = "-s";

	public ExecArg() {
		super();
	}

	public void parse(final @Nullable String[] args, final @NotNull InspectorGuidetAnalyser launcher) {
		if(args == null || args.length == 0 || !EXEC_SRC_ARG.equals(args[0])) {
			throw getArgumentException();
		}

		int i = 1;
		while(i < args.length && !args[i].equals(EXEC_CP_ARG)) {
			launcher.addInputResource(args[i]);
			i++;
		}

		if(i < args.length && EXEC_CP_ARG.equals(args[i])) {
			i++;
			System.out.println(Arrays.stream(args, i, args.length).collect(Collectors.toList()));
			launcher.setSourceClasspath(Arrays.stream(args, i, args.length).toArray(String[]::new));
		}
	}


	private @NotNull IllegalArgumentException getArgumentException() {
		return new IllegalArgumentException("Arguments: -s path/to/scr/to/analyse -c path/to/optional/classpath/libs.jar");
	}
}
