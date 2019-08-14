package fr.inria.inspectorguidget.api;

import fr.inria.inspectorguidget.internal.helper.SpoonStructurePrinter;
import java.io.File;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.LogRecord;

import org.junit.jupiter.api.BeforeEach;
import spoon.processing.Processor;
import spoon.reflect.declaration.CtElement;
import spoon.reflect.factory.FactoryImpl;
import spoon.support.DefaultCoreFactory;
import spoon.support.StandardEnvironment;
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

import spoon.support.compiler.jdt.JDTBasedSpoonCompiler;

import static org.assertj.core.api.AssertionsForClassTypes.fail;


public abstract class TestInspectorGuidget<T extends Processor<? extends CtElement>> {
	protected static final String SWT_LIB = System.getProperty("user.home") +  "/.m2/repository/org/eclipse/swt/org.eclipse.swt.gtk.linux.x86/4.6.1/org.eclipse.swt.gtk.linux.x86-4.6.1.jar";
	protected Collection<T> processors;
	protected JDTBasedSpoonCompiler modelBuilder;

	public static final boolean SHOW_MODEL = false;

	public static final Handler HANDLER_FAIL = new Handler() {
		@Override
		public void publish(final LogRecord record) {
			if(record.getLevel() == Level.SEVERE) {
				fail(record.getMessage());
			}
		}

		@Override
		public void flush() {
		}

		@Override
		public void close() throws SecurityException {
		}
	};

	@BeforeEach
	public void setUp() {
		final StandardEnvironment evt = new StandardEnvironment();
		evt.setComplianceLevel(8);
		modelBuilder = new JDTBasedSpoonCompiler(new FactoryImpl(new DefaultCoreFactory(), evt));
		processors = createProcessor();
//		Launcher.LOGGER.setLevel(Level.OFF);
	}

	protected abstract Collection<T> createProcessor();

	protected void run(final String... srcPath) {
		runWithCP(null, srcPath);
	}

	protected void runWithCP(final List<String> cp, final String... srcPath) {
		Arrays.stream(srcPath).forEach(src -> modelBuilder.addInputSource(new File(src)));
		if(cp != null) {
			modelBuilder.setSourceClasspath(cp.toArray(new String[cp.size()]));
		}
		modelBuilder.build();

		if(SHOW_MODEL) {
			SpoonStructurePrinter printer = new SpoonStructurePrinter();
			printer.scan(Collections.singletonList(modelBuilder.getFactory().Package().getRootPackage()));
		}

		modelBuilder.process((Collection<Processor<? extends CtElement>>) processors);
	}
}
