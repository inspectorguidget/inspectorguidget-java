package inspectorguidget.eclipse.actions;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.jdt.core.IClasspathContainer;
import org.eclipse.jdt.core.IClasspathEntry;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.ui.IObjectActionDelegate;
import org.eclipse.ui.ISelectionService;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.PlatformUI;
//import org.eclipse.ui.internal.Workbench;
import org.eclipse.ui.progress.UIJob;

import spoon.SpoonAPI;
import spoon.reflect.factory.Factory;

public abstract class AbstractAction<T extends SpoonAPI> implements IObjectActionDelegate {
//	private Shell	shell;
//	public Factory factory;
	long spoonloading;
	protected T analyser;


	@Override
	public void run(IAction action) {
		final IProject project = getCurrentProject();

		Job job = new Job("InspectorGuidget.eclipse") {
			@Override
			public IStatus run(IProgressMonitor monitor) {
				// set total number of work units
				monitor.beginTask("Spoon analysis", 2);
				// Measuring the time for InspectorWidget
				// final long startTime = System.currentTimeMillis();
				initAction(monitor, project);
				Job job2 = new UIJob("Add markers") {// To switch to the UI thread
					@Override
					public IStatus runInUIThread(IProgressMonitor monit) {
						addMarkers(project);
						// Measuring the time of InspectorWidget
						// long endTime = System.currentTimeMillis();
						// System.out.println("---------------------------");
						// System.out.println("spoonLoading time " +
						// (spoonloading - startTime)); //The time just for
						// spoonloading
						// System.out.println("blob detection time " + (endTime
						// - spoonloading)); //The time just for blob detection
						// System.out.println("Total of time " + (endTime -
						// startTime));
						return Status.OK_STATUS;
					}
				};
				job2.schedule();

				return Status.OK_STATUS;
			}
		};
		job.schedule();
	}

	
	protected void loadProjectDeps(final Set<String> classpath, final Set<File> libs, final Set<String> projects,
									final IProject project, final boolean mainProject) {
		try {
			if (project.isOpen() && project.hasNature(JavaCore.NATURE_ID)) {
				IJavaProject jProject = JavaCore.create(project);

				for (IClasspathEntry entry : jProject.getRawClasspath()) {
					switch (entry.getEntryKind()) {
						case IClasspathEntry.CPE_SOURCE:
							classpath.add(ResourcesPlugin.getWorkspace().getRoot().getLocation().toString()+entry.getPath());
							break;
							
						case IClasspathEntry.CPE_LIBRARY:
							IPath rel = entry.getPath().makeRelativeTo(project.getFullPath());
							String path = project.getFile(rel).getLocation().toString();
							File file = new File(path);
							libs.add(file);
							break;
						case IClasspathEntry.CPE_PROJECT:
							IProject proj = ResourcesPlugin.getWorkspace().getRoot()
									.getProject(entry.getPath().toOSString());
							if (proj != null && !projects.contains(proj.getName())) {
								projects.add(proj.getName());
								loadProjectDeps(classpath, libs, projects, proj, false);
							}
							break;
						case IClasspathEntry.CPE_CONTAINER:
							try {
								final IClasspathContainer container = JavaCore.getClasspathContainer(entry.getPath(),
										jProject);
								if (container != null) {
									for (IClasspathEntry en : container.getClasspathEntries()) {
										libs.add(en.getPath().toFile());
									}
								}
							} catch (JavaModelException e) {
								e.printStackTrace();
							}
							break;
						default:
							// TODO
					}
				}
			}
		} catch (CoreException e) {
			e.printStackTrace();
		}
	}


	protected void initAction(final IProgressMonitor monitor, final IProject project) {
		monitor.subTask("Collect source files");

		Set<String> classpath = new HashSet<>();
		Set<File> libs = new HashSet<>();
		Set<String> projects = new HashSet<>();

		projects.add(project.getName());
		loadProjectDeps(classpath, libs, projects, project, true);

		monitor.worked(1);
		monitor.subTask("Spoon build");
		spoonBuild(classpath, libs);
		analyser.process();
		monitor.worked(2);
	}


	/**
	 * Build the Spoon AST for the classes in @classpath
	 * @param libs
	 * @return Spoon model
	 */
	protected Factory spoonBuild(Set<String> classpath, Set<File> libs) {

		ClassLoader libLoader = new URLClassLoader(getDependencies(libs), Thread.currentThread().getContextClassLoader());
		Thread.currentThread().setContextClassLoader(libLoader);

		analyser = createAnalyser();
		System.out.println(classpath);
		System.out.println(libs);
		classpath.forEach(file -> analyser.addInputResource(file));

		try {
			analyser.buildModel();
		} catch (Exception e) {
			e.printStackTrace();
		}
//		env.setInputClassLoader(ClassLoader.getSystemClassLoader());
		spoonloading = System.currentTimeMillis();// Measuring the time of Spoon to load the classes
		return analyser.getFactory();
	}
	
	
	protected abstract T createAnalyser();
	

	private static URL[] getDependencies(Set<File> libs) {
		// List<URL[]> urls = new ArrayList<URL[]>();
		// for(String folder : folders){
		// urls.add(getDependencies(folder));
		// }

		List<URL[]> urls = new ArrayList<>();
		// for(String folder : folders){
		for (File file : libs) {
			urls.add(getDependencies(file));
		}

		int size = 0;
		for (URL[] url : urls) {
			size += url.length;
		}

		URL[] res = new URL[size];
		size = 0;
		for (int i = 0; i < urls.size(); i++) {
			URL[] url = urls.get(i);
			for (int j = 0; j < url.length; j++) {
				res[size + j] = url[j];
			}
			size += url.length;
		}

		for (URL url : res) {
			System.out.println(url);
		}

		return res;
	}

	// private static URL[] getDependencies(String folder){
	// TODO: check for errors :)
	private static URL[] getDependencies(File file) {
		// try {
		// URL path;
		// File folderDir;
		//
		// path = new URL("file://"+ folder);
		// folderDir = new File(path.toURI());
		//
		// File[] libs = folderDir.listFiles();

		List<URL> urls = new ArrayList<>();
		// for(File file : libs){
		// if(file.isDirectory()){
		// //URL[] deeperFolders = getDependencies(file.getAbsolutePath());
		// URL[] deeperFolders = getDependencies(file);//FIXME infinite loop!
		// for(URL r : deeperFolders){
		// urls.add(r);
		// }
		// }
		// else{
		try {
			// if(file.getName().endsWith(".jar")){
			urls.add(file.toURI().toURL());
			// }
		} catch (MalformedURLException e) {
			e.printStackTrace();
		}
		// }
		// }
		return urls.toArray(new URL[urls.size()]);

		// } catch (MalformedURLException | URISyntaxException e2) {
		// // TODO Auto-generated catch block
		// e2.printStackTrace();
		//
		// }
		// return null;
	}

//	/**
//	 * Find listeners in @factory and store them in @listeners
//	 */
//	protected void spoonProcess(Factory facto, List<AbstractProcessor<?>> processors) {
//
//		ProcessingManager processorManager = new QueueProcessingManager(facto);
//		processors.forEach(proc -> processorManager.addProcessor(proc));
//		processorManager.process();
//	}

	abstract protected void addMarkers(IProject project);

	/**
	 * Get the selected poject. Can return null
	 */
	protected static IProject getCurrentProject() {
		ISelectionService selectionService = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getSelectionService();
		ISelection selection = selectionService.getSelection();

		if(selection instanceof IStructuredSelection) {
			Object element = ((IStructuredSelection) selection).getFirstElement();
			if(element instanceof IProject) return (IProject) element;
			if(element instanceof IJavaProject) return ((IJavaProject)element).getProject();
		}

		return null;
	}

	@Override
	public void setActivePart(IAction action, IWorkbenchPart targetPart) {
//		shell = targetPart.getSite().getShell();
	}

	@Override
	public void selectionChanged(IAction action, ISelection selection) {
	}
}