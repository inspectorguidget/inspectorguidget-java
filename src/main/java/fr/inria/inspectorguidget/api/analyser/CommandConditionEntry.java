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

import fr.inria.inspectorguidget.internal.helper.SpoonHelper;
import java.util.Set;
import org.jetbrains.annotations.NotNull;
import spoon.reflect.code.CtCase;
import spoon.reflect.code.CtCodeElement;
import spoon.reflect.code.CtExpression;
import spoon.reflect.code.CtLocalVariable;
import spoon.reflect.code.CtSwitch;

/**
 * An entry class used in the Command class to represent a conditional statement that conditions the command.
 */
public class CommandConditionEntry {
	/**
	 * The real statement pointed out by the command (can be an 'else', a 'case' ,or a real boolean expression for instance).
	 */
	public final CtCodeElement realStatmt;
	/**
	 * The effective boolean expression that condition the command. For example, if the real statement is an 'else' one, then
	 * the effective statement may be the negation of the if condition. It means that the returns expression may have been created
	 * during the process and is not part of the AST.
	 */
	public final CtExpression<Boolean> effectiveStatmt;

	public CommandConditionEntry(final @NotNull CtCodeElement realStatmt, final @NotNull CtExpression<Boolean> effectiveStatmt) {
		super();
		this.realStatmt = realStatmt;
		this.effectiveStatmt = effectiveStatmt;
	}

	public CommandConditionEntry(final @NotNull CtExpression<Boolean> statmt) {
		this(statmt, statmt);
	}

	public boolean isSameCondition() {
		return realStatmt==effectiveStatmt;
	}


	/**
	 * @return All the local variables used in the real conditional statement. If the statement is a switch case, the selector of the
	 * switch is also analysed. Cannot be null.
	 */
	public @NotNull Set<CtLocalVariable<?>> getAllLocalVariables() {
		final Set<CtLocalVariable<?>> all = SpoonHelper.INSTANCE.getAllLocalVarDeclaration(realStatmt);

		if(realStatmt instanceof CtCase<?>) {
			all.addAll(SpoonHelper.INSTANCE.getAllLocalVarDeclaration(realStatmt.getParent(CtSwitch.class).getSelector()));
		}

		return all;
	}

	@Override
	public String toString() {
		return "CommandConditionEntry{real: " + realStatmt + ", line " + SpoonHelper.INSTANCE.getLinePosition(realStatmt) +
			(isSameCondition() ? "" : ", effective: " + effectiveStatmt) + "}";
	}
}
