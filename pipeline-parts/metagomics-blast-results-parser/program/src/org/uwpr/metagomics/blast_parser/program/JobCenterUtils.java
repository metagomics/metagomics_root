package org.uwpr.metagomics.blast_parser.program;

import java.util.HashMap;
import java.util.Map;

import org.jobcenter.client.main.SubmissionClientConnectionToServer;
import org.jobcenter.client_exceptions.JobcenterSubmissionHTTPErrorException;
import org.jobcenter.client_exceptions.JobcenterSubmissionIOErrorException;
import org.jobcenter.coreinterfaces.JobSubmissionInterface;

public class JobCenterUtils {

	private final String connectionURL = "http://localhost/JobCenter_Server_Jersey/";

	
	public static JobCenterUtils getInstance() { return new JobCenterUtils(); }
	
	public void submitGOCountProcessingRequest( int runId, int jobcenterRequestId ) throws Exception {
		
		String requestName = "metagomics_run_upload";
		String jobName = "metagomics_process_GO_counts";
		

		Map<String, String> jobParameters = new HashMap<String, String> ();

		jobParameters.put( "run_id", String.valueOf( runId ) );

		JobSubmissionInterface jobSubmissionClient = null;

		try {

			jobSubmissionClient = new SubmissionClientConnectionToServer();

			jobSubmissionClient.init( connectionURL );

			//  Should succeed

			int requestId = jobSubmissionClient.submitJob(
						requestName,
						jobcenterRequestId,
						jobName,
						"metagomics server",
						1,
						jobParameters );



		} catch ( JobcenterSubmissionHTTPErrorException e ) {

			System.err.println( "JobcenterSubmissionHTTPErrorException thrown:" );
			System.err.println( "Http Error Code: " + e.getHttpErrorCode() );
			System.err.println( "Full URL: " + e.getFullConnectionURL() );

			try {
				if ( e.getErrorStreamContents() == null ) {

					System.err.println( "e.getErrorStreamContents() == null" );
				} else {
					String errorStreamContents = new String( e.getErrorStreamContents() ); // Use default character set
					System.err.println( "Error Stream Contents: " + errorStreamContents );
				}
			} catch (Exception ex ) {

				System.err.println( "Error printing 'Error Stream Contents': " + ex.toString() );
			}
			e.printStackTrace();

			throw e;


		} catch ( JobcenterSubmissionIOErrorException e ) {

			System.err.println( "JobcenterSubmissionIOErrorException thrown:" );

			System.err.println( "Full URL: " + e.getFullConnectionURL() );
			try {
				if ( e.getErrorStreamContents() == null ) {

					System.err.println( "e.getErrorStreamContents() == null" );
				} else {
					String errorStreamContents = new String( e.getErrorStreamContents() ); // Use default character set
					System.err.println( "Error Stream Contents: " + errorStreamContents );
				}
			} catch (Exception ex ) {

				System.err.println( "Error printing 'Error Stream Contents': " + ex.toString() );
			}
			e.printStackTrace();

			throw e;

		} catch (Exception e) {
			e.printStackTrace();

			throw e;
		} catch (Throwable e) {
			e.printStackTrace();
		} finally {

			if ( jobSubmissionClient != null ) {

				jobSubmissionClient.destroy();
			}
		}
	}
}
