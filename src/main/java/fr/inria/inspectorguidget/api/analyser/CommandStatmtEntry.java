/*
 * This file is part of InspectorGuidget.
 * InspectorGuidget is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * InspectorGuidget is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * You should have received a copy of the GNU General Public License
 * along with InspectorGuidget.  If not, see <https://www.gnu.org/licenses/>.
 */

package fr.inria.inspectorguidget.api.analyser;

import fr.inria.inspectorguidget.internal.filter.FindElementFilter;
import java.util.Objects;
import org.jetbrains.annotations.NotNull;
import spoon.reflect.code.CtStatement;
import spoon.reflect.declaration.CtElement;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

/**
 * An entry class used in the Command class to represent a statement that composed the code of the command.
 */
public class CommandStatmtEntry {
	final List<CtElement> statmts;
	final boolean mainEntry;
	boolean dispatchMethod;

	public CommandStatmtEntry(final boolean main) {
		super();
		statmts = new ArrayList<>();
		mainEntry = main;
		dispatchMethod = false;
	}

	public CommandStatmtEntry(final boolean main, final @NotNull Collection<CtElement> listStatmts) {
		this(main);
		addStatements(listStatmts);
	}

	public CommandStatmtEntry(final boolean main, final @NotNull List<CtStatement> listStatmts) {
		this(main, listStatmts, false);
	}

	public CommandStatmtEntry(final boolean main, final @NotNull List<CtStatement> listStatmts, final boolean fromDispatch) {
		this(main);
		dispatchMethod = fromDispatch;
		listStatmts.forEach(s -> addStatement(s));
	}

	public boolean isDispatchedCode() {
		return dispatchMethod;
	}

	public void addStatement(final @NotNull CtElement statmt) {
		statmts.add(statmt);
	}

	public void addStatements(final @NotNull Collection<CtElement> listStatmts) {
		statmts.addAll(listStatmts);
	}

	public boolean isMainEntry() {
		return mainEntry;
	}

	public List<CtElement> getStatmts() {
		return Collections.unmodifiableList(statmts);
	}

	public int getLineStart() {
		return getStatmts().stream().map(s -> s.getPosition()).filter(p -> p!=null).mapToInt(p -> p.getLine()).min().orElse(-1);
	}

	public int getLineEnd() {
		return getStatmts().stream().map(s -> s.getPosition()).filter(p -> p!=null).mapToInt(p -> p.getEndLine()).max().orElse(-1);
	}

	/**
	 * Checks the line numbers of the entries to state whether the calling one containsLine the given one.
	 * @param entry The given entry to test against the calling one.
	 * @return True if the calling entry contains the given one. False otherwise.
	 */
	public boolean containsLine(final @NotNull CommandStatmtEntry entry) {
		return getLineStart()<=entry.getLineStart() && getLineEnd()>=entry.getLineEnd();
	}


	public boolean containsElement(final @NotNull CommandStatmtEntry entry) {
		return statmts.size()==1 && entry.statmts.size()==1 && !statmts.get(0).getElements(new FindElementFilter(entry.statmts.get(0), true)).isEmpty();
	}

	@Override
	public boolean equals(final Object o) {
		if(this == o) return true;
		if(o == null || getClass() != o.getClass()) return false;

		final CommandStatmtEntry that = (CommandStatmtEntry) o;

		if(mainEntry != that.mainEntry) return false;
		if(dispatchMethod != that.dispatchMethod) return false;
		return Objects.equals(statmts, that.statmts);

	}

	@Override
	public int hashCode() {
		int result = statmts != null ? statmts.hashCode() : 0;
		result = 31 * result + (mainEntry ? 1 : 0);
		result = 31 * result + (dispatchMethod ? 1 : 0);
		return result;
	}

	@Override
	public String toString() {
		return "CommandStatmtEntry{start: " + getLineStart() + ", end: " + getLineEnd() + ", main: " + mainEntry + " dispatched: " + dispatchMethod + "}";
	}
}
