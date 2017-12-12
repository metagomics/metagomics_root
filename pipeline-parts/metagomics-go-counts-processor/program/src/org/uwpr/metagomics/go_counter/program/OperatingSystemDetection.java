package org.uwpr.metagomics.go_counter.program;

public class OperatingSystemDetection {

	private static boolean windowsOSCheckedFor;

	private static boolean windowsOSDetected;
	

	public static boolean isWindows(){
		
		if ( ! windowsOSCheckedFor ) {
			
			windowsOSCheckedFor = true;	
		 
			String os = System.getProperty("os.name").toLowerCase();
			//windows
			windowsOSDetected = (os.indexOf( "win" ) >= 0); 
		}
		
		return windowsOSDetected;
	}
}
