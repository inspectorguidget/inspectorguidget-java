package fr.inria.inspectorguidget.internal.filter;

import spoon.reflect.code.CtReturn;
import spoon.reflect.visitor.Filter;

public class ReturnFilter implements Filter<CtReturn<?>> {
	public ReturnFilter() {
	}

	@Override
	public boolean matches(CtReturn<?> ret) {
		return ret != null;
	}
}
