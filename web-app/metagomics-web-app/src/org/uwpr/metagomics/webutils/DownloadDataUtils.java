package org.uwpr.metagomics.webutils;

import java.io.File;

import org.uwpr.metagomics.webconstants.WebAppConstants;

public class DownloadDataUtils {

	public static File getReportFileSingleRun( String uid, int runId ) {
		
		File dataDirectory = new File( WebAppConstants.DATA_DOWNLOAD_DIRECTORY, uid );
		if( !dataDirectory.exists() )
			return null;
		
		File dataFile = new File( dataDirectory, "go_report_" + runId + ".txt" );
		
		return dataFile;
	}
	
	public static File getTaxonomyReportFileSingleRun( String uid, int runId ) {
		
		File dataDirectory = new File( WebAppConstants.DATA_DOWNLOAD_DIRECTORY, uid );
		if( !dataDirectory.exists() )
			return null;
		
		File dataFile = new File( dataDirectory, "taxonomy_report_" + runId + ".txt" );
		
		return dataFile;
	}
	
	public static File getImageFileSingleRun( String uid, int runId, String aspect, String format ) {
		
		File dataDirectory = new File( WebAppConstants.DATA_DOWNLOAD_DIRECTORY, uid );
		if( !dataDirectory.exists() )
			return null;
		
		File dataFile = new File( dataDirectory, "go_image_" + aspect + "_" + runId + "." + format );
		return dataFile;
	}
	
	public static File getReportFileTwoRuns( String uid, int run1, int run2 ) {
		
		File dataDirectory = new File( WebAppConstants.DATA_DOWNLOAD_DIRECTORY, uid );
		if( !dataDirectory.exists() )
			return null;
		
		Integer r1 = null;
		Integer r2 = null;
		
		if( run1 <= run2 ) {
			r1 = run1;
			r2 = run2;
		} else {
			r1 = run2;
			r2 = run1;
		}
		
		File dataFile = new File( dataDirectory, "go_compare_" + r1 + "_" + r2 + ".txt" );
		return dataFile;
	}
	
	public static File getImageFileTwoRuns( String uid, int run1, int run2, String aspect, String format ) {
		
		File dataDirectory = new File( WebAppConstants.DATA_DOWNLOAD_DIRECTORY, uid );
		if( !dataDirectory.exists() )
			return null;
		
		
		Integer r1 = null;
		Integer r2 = null;
		
		if( run1 <= run2 ) {
			r1 = run1;
			r2 = run2;
		} else {
			r1 = run2;
			r2 = run1;
		}
		
		
		
		File dataFile = new File( dataDirectory, "go_compare_" + r1 + "_" + r2 + "_" + aspect + "." + format );
		return dataFile;
	}
	
}
