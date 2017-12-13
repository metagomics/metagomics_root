package org.uwpr.metagomics.utils;

import java.io.InputStream;
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
				
				ClassLoader thisClassLoader = VersionUtils.class.getClassLoader();
				InputStream propertiesFileAsStream = thisClassLoader.getResourceAsStream( "version.properties" );

				if ( propertiesFileAsStream == null ) {

					throw new Exception( "Could not find version.properties in class path." );
				}

				Properties properties = new Properties();
				properties.load( propertiesFileAsStream );

				_VERSION = properties.getProperty( "version" );

				
				try {
					propertiesFileAsStream.close();
				} catch( Exception e ) { ; }
				
			} catch( Exception e ) {
				_VERSION = "Error retrieving version.";
			}
		}

		
		return _VERSION;
		
	}
	
}
