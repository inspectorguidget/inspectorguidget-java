package inspectorguidget.eclipse.actions;

import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.eclipse.core.resources.IMarker;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;

import fr.inria.diverse.torgen.inspectorguidget.analyser.BlobListenerAnalyser;
import fr.inria.diverse.torgen.inspectorguidget.analyser.Command;
import fr.inria.diverse.torgen.inspectorguidget.helper.SpoonHelper;
import inspectorguidget.eclipse.views.BlobView;
import spoon.reflect.declaration.CtExecutable;

public class DetectBlobListenerAction extends AbstractAction<BlobListenerAnalyser> {
	/** Link Markers to their methods */
	private static final Map<IMarker, Entry<CtExecutable<?>, List<Command>>> INFO_MARKERS = new HashMap<>();

	public DetectBlobListenerAction() {
		super();
	}
	
	@Override
	protected BlobListenerAnalyser createAnalyser() {
		return new BlobListenerAnalyser();
	}


	public static void clearMarkers() {
		BlobView.getSingleton().clearMarkers();
		INFO_MARKERS.keySet().forEach(marker -> {
			try { marker.delete(); } 
			catch(Exception e) { e.printStackTrace();}
		});
		INFO_MARKERS.clear();
	}
	
	
	/** Attach a warning marker for each listeners */
	@Override
	protected void addMarkers(final IProject project) {
		try {
			PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage().showView(BlobView.ID);
		}catch(PartInitException e1) {
			e1.printStackTrace();
		}

		analyser.getBlobs().entrySet().forEach(entry -> markCtElement(entry, project));
	}
	
	
	private void markCtElement(final Entry<CtExecutable<?>, List<Command>> entry, final IProject project) {
		final String projectName = project.getName();
		final File source = entry.getKey().getPosition().getFile();
		// FIXME: little hack here
		final String absPath = source.getAbsolutePath();
		final int begin = absPath.indexOf(projectName) + projectName.length() + 1; 
		String path = absPath.substring(begin);
		IResource r = project.findMember(path);
		
		if(r==null) {
			int i = path.indexOf('/');
			if(i!=-1)
				path = path.substring(i);
			r = project.findMember(path);
		}
		
		if(r==null && path.startsWith("/"+project.getName())) {
			path = path.replaceFirst("/"+project.getName(), "");
			r = project.findMember(path);
		}
		
		if(r!=null) {
			IMarker m;
			try {
				m = r.createMarker(IMarker.PROBLEM);
				m.setAttribute(IMarker.MARKER, ClearMarkersAction.INSPECTOR_MARKER_NAME);
				m.setAttribute(IMarker.MESSAGE, "Blob Listener detected here with " + entry.getValue().size() + " commands");
				m.setAttribute(IMarker.LINE_NUMBER, SpoonHelper.INSTANCE.getLinePosition(entry.getKey()));
				m.setAttribute(IMarker.SEVERITY, IMarker.SEVERITY_WARNING);
				INFO_MARKERS.put(m, entry); // store mapping
				BlobView.getSingleton().addMarker(m); // update the view
			} catch (CoreException e) {
				e.printStackTrace();
			}
		}
	}

	
	public static String getMethod(IMarker marker) {
		Entry<CtExecutable<?>, List<Command>> entry = INFO_MARKERS.get(marker);
		if(entry==null) return "Blob listener";
		return entry.getKey().getSimpleName();
	}
	

	/**
	 * Convert the marker to String "methodName;sourceFile;line"
	 */
	public static String getInfo(final IMarker marker) {
		String res = "";
		Entry<CtExecutable<?>, List<Command>> entry = INFO_MARKERS.get(marker);
		
		if(entry != null) {
			String sourceFile = entry.getKey().getPosition().getFile().getName();
			int line = entry.getKey().getPosition().getLine();
			String name = entry.getKey().getSignature();
			res = name + ";" + sourceFile + ";" + line;
		}

		return res;
	}
}