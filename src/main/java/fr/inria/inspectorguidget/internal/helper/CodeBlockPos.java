package fr.inria.inspectorguidget.internal.helper;

public class CodeBlockPos {
	public final String file;
	public final int startLine;
	public final int endLine;

	public CodeBlockPos(final String file, final int startLine, final int endLine) {
		super();
		this.file = file;
		this.startLine = startLine;
		this.endLine = endLine;
	}

	@Override
	public String toString() {
		return file + ";" + startLine + ";" + endLine;
	}
}
