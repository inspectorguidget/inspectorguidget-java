package fr.inria.inspectorguidget.internal.filter;

import spoon.reflect.code.CtVariableAccess;
import spoon.reflect.visitor.filter.AbstractFilter;

public class VariableAccessFilter extends AbstractFilter<CtVariableAccess<?>> {
	@Override
	public boolean matches(final CtVariableAccess<?> element) {
		return element != null;
	}
}
