package org.uwpr.metagomics.run_upload.program;

public class MainProgram {

	public static void main( String[] args ) throws Exception {
		
		int runId = Integer.parseInt( args[ 0 ] );
		int jobcenterRequestId = Integer.parseInt( args[ 1 ] );
		
		MSUploadParserRunner.getInstance().RunMSUploadParser( runId, jobcenterRequestId);
		
	}
	
}
