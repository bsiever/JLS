package jls;

import jls.sim.*;
import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Date;
import java.util.jar.JarFile;
import java.util.prefs.Preferences;
import java.util.zip.ZipEntry;

import javax.help.*;

/**
 * Constants for JLS.
 * 
 * @author David A. Poplawski
 */
public final class JLSInfo { 
	
	// version info
	public static final String build = "<p>"+getClassBuildTime();
	public static final int vers = 4;
	public static final int release = 10;
	public static final int buildNum = 0;
	public static final int year = 2025;
	public static final String authors = "David A. Poplawski (primary/original author);\n Joshua Marshall (contributor);\n Other MTU.EDU students,\n and Bill Siever (minor modifications and packaging)";
	public static final String repository = "https://github.com/bsiever/JLS";
	public static final String version = "JLS " + vers + "." + release;
	public static final String LAST_WD_KEY = "last directory";  // Working directory of last file browser operation. 

	
	// miscellaneous parameters
	public static final int windowsize = 600;
	public static final int circuitsize = 1000;			// square circuit (can be increased)
	public static final int spacing = 12;					// for snap-to
	public static final int pointDiameter = 6;			// for inputs and outputs
	public static final int stateDiameter = 40;			// state machine states
	public static final int arrowSize = 6;				// state machine arrows
	public static final Color touchColor = Color.green;	// when connections line up
	public static final Color highlightColor =
		Color.pink;										// when elements are highlighted
	public static final Color selectionColor = 
		new Color(240,240,240);							// when elements are selected
	public static final Color watchColor =
		Color.cyan;										// watched elements
	public static final Color nonZeroColor =
		Color.red;										// wires with non-zero values
	public static final Color initialStateColor =
		Color.lightGray;									// initial states of state machines
	public static final int checkPointFreq = 10;			// how many changes between checkpoint file writes
	public static final int undoStackDepth = 10;			// maximum number of undos
	public static final long defaultTimeLimit = 10_000;		// default simulation time
	public static Frame frame = null;					// for dialog boxes
	public static Simulator sim = null;					// for undo/redo
	public static boolean isApplet = false;				// set to true by applet
	public static boolean batch = false;				// batch mode
	public static boolean printTrace = false;			// print signal trace
	public static boolean imgexport = false;			// export image from command line
	public static HelpBroker hb = null;
	public enum Orientation { UP, DOWN, LEFT, RIGHT; }
	public static Color gridColor = 
		new Color(240,240,240);							// editor window grid
	public static Color backgroundColor =  Color.white;	// editor window grid
	public static String loadError = "";				// error message when loading a circuit
	
	/**
	 * Private constructor to keep this class from being instantiated.
	 */
	private JLSInfo() {}

	
	/**
	 * Returns the last selected/saved directory (with the trailing /)
	 * @return
	 */
	public static String getLastSelectedDirectory() {
		Preferences prefs = Preferences.userNodeForPackage(JLSInfo.class);
		String dir = prefs.get(JLSInfo.LAST_WD_KEY, "");
		// If it doesn't seem valid, use the home directory
		if(dir.equals("/") || dir.equals("") || (Files.exists(Paths.get(dir)) == false)) {
			dir = System.getProperty("user.home");
		} 	
		return dir;
	}
	
	/**
	 * 
	 * @param dir Directory to save/store
	 */
	public static void setLastSelectedDirectory(String dir) {
		// Use home as default
		String toStore = System.getProperty("user.home");
		
		// Check if passed value seems valid 
		if(dir!=null && dir.length()!=0) {
			if(dir.charAt(dir.length()-1)!='/')
				dir = dir+"/";
			if(Files.exists(Paths.get(dir))) {
				toStore = dir;
			}
		}
		// Save/store the given directory
		Preferences prefs = Preferences.userNodeForPackage(JLSInfo.class);
		prefs.put(JLSInfo.LAST_WD_KEY, toStore);
			
	}
	
	
	// https://stackoverflow.com/questions/3336392/java-print-time-of-last-compilation
	/**
	 * Handles files, jar entries, and deployed jar entries in a zip file (EAR).
	 * @return The date if it can be determined, or null if not.
	 */
	private static Date getClassBuildTime() {
	    Date d = null;
	    Class<?> currentClass = new Object() {}.getClass().getEnclosingClass();
	    URL resource = currentClass.getResource(currentClass.getSimpleName() + ".class");
	    if (resource != null) {
	        if (resource.getProtocol().equals("file")) {
	            try {
	                d = new Date(new File(resource.toURI()).lastModified());
	            } catch (URISyntaxException ignored) { }
	        } else if (resource.getProtocol().equals("jar")) {
	            String path = resource.getPath();
	            d = new Date( new File(path.substring(5, path.indexOf("!"))).lastModified() );    
	        } else if (resource.getProtocol().equals("zip")) {
	            String path = resource.getPath();
	            File jarFileOnDisk = new File(path.substring(0, path.indexOf("!")));
	            //long jfodLastModifiedLong = jarFileOnDisk.lastModified ();
	            //Date jfodLasModifiedDate = new Date(jfodLastModifiedLong);
	            try(JarFile jf = new JarFile (jarFileOnDisk)) {
	                ZipEntry ze = jf.getEntry (path.substring(path.indexOf("!") + 2));//Skip the ! and the /
	                long zeTimeLong = ze.getTime ();
	                Date zeTimeDate = new Date(zeTimeLong);
	                d = zeTimeDate;
	            } catch (IOException|RuntimeException ignored) { }
	        }
	    }
	    return d;
	}

} // end of JLSInfo class
