package org.uwpr.metagomics.utils;

import java.io.File;
import java.io.FileInputStream;
import java.util.Properties;

/**
 * Get the version of this build of metagomics, as defined in version.properties
 * @author mriffle
 *
 */
public class VersionUtils {

	public static String _VERSION;
	
	/**
	 * Get the version of this build of metagomics, as defined in version.properties
	 * @return
	 */
	public static String getVersion() {
		
		if( _VERSION == null ) {
			try {
				File file = new File("version.properties");
				FileInputStream fileInput = new FileInputStream(file);
				Properties properties = new Properties();
				properties.load(fileInput);
				fileInput.close();
				
				_VERSION = properties.getProperty( "version" );

			} catch( Exception e ) {
				_VERSION = "Error retrieving version.";
			}
		}

		
		return _VERSION;
		
	}
	
}
