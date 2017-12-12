package org.uwpr.metagomics.go_counts_processor.main;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.jobcenter.constants.JobStatusValuesConstants;
import org.jobcenter.constants.RunMessageTypesConstants;
import org.jobcenter.job_client_module_interface.ModuleInterfaceClientMainInterface;
import org.jobcenter.job_client_module_interface.ModuleInterfaceClientServices;
import org.jobcenter.job_client_module_interface.ModuleInterfaceJobProgress;
import org.jobcenter.job_client_module_interface.ModuleInterfaceRequest;
import org.jobcenter.job_client_module_interface.ModuleInterfaceResponse;
import org.uwpr.metagomics.go_counts_processor.config.GetConfigFromFileServiceImpl;

/**
 * main class executed by  Client
 *
 */
public class RunMetagomicsGoCountsProcessor_JobcenterModule implements ModuleInterfaceClientMainInterface {

	private static Logger log = Logger.getLogger(ModuleInterfaceClientMainInterface.class);

	
	private static final String JOB_PARAM_RUN_ID = "run_id";

	private static final String MODULE_LABEL = "Metagomics_RunUpload_Module";

	

	//  Values from config properties file
	
	
	private String commandToExecuteBase;

//	private File impRunTempRootDirectory;


	//  has a shutdown request been received.  Volatile since the shutdown request will be on a different thread

	private volatile boolean shutdownRequested;


	//  Has a stopRunningAfterProcessingJob request been received.
	//      Volatile since the stopRunningAfterProcessingJob request will be on a different thread

	private volatile boolean stopRunningAfterProcessingJobRequested = false;


	/////////////////////////////////

	//  has the module been initialized

	private boolean initialized = false;

	/////////////////////////////////


	//////////////////////////////////////////////////////////////////////////


//	Job Manager lifecycle methods


	/**
	 * Called when the object is instantiated.
	 * The object will not be used to process requests if this method throws an exception
	 *
	 * @param moduleInstanceNumber - The instance number of this module in this client,
	 *         incremented for each time the module instantiated while the client is running,
	 *         reset to 1 when the client is restarted.
	 *         This can be useful for separating logging or other file resources between
	 *         instances of the same module in the same client.
	 * @throws Throwable
	 */
	@Override
	public void init(int moduleInstanceNumber) throws Throwable {

		log.info( "!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!! Starting " + MODULE_LABEL + " .....  Called init()" );

		commandToExecuteBase = GetConfigFromFileServiceImpl.getCommandToExecuteBase();

		initialized = true;
	}





	/**
	 * Called when the object will no longer be used
	 */
	@Override
	public void destroy() {

		log.info( "destroy() called " );


		log.info( "destroy()  exiting " );
	}




	@Override
	/**
	 * Called on a separate thread when a shutdown request comes from the operating system.
	 * If this is not heeded, the process may be killed by the operating system after some time has passed ( controlled by the operating system )
	 */
	public void shutdown() {


		log.info( "shutdown() called " );


		shutdownRequested = true;



		log.info( "shutdown() exitting " );


	}


	@Override
	/**
	 * Called on a separate thread when a request comes from the user via updating the control file.
	 * The user has indicated that they want the job manager client to to stop processing new jobs
	 * and complete the jobs that are running.
	 *
	 * The module needs this information since if it is in an endless retry mode to submit the next job or
	 *   other efforts, it needs to fail the job and return.
	 */
	public void stopRunningAfterProcessingJob() {


		stopRunningAfterProcessingJobRequested = true;

	}

	/**
	 * called before and possibly after each request is processed to clear input parameters and stored errors
	 */
	@Override
	public void reset() throws Throwable {

		if ( ! initialized ) {

			throw new Exception( "Module has not been initialized" );
		}


	}


	/**
	 * Process the request
	 */
	@Override
	public void processRequest( ModuleInterfaceRequest moduleInterfaceRequest, ModuleInterfaceResponse moduleInterfaceResponse, ModuleInterfaceJobProgress moduleInterfaceJobProgress, ModuleInterfaceClientServices moduleInterfaceClientServices  ) throws Throwable {

		final String methodName = "processRequest";

		if ( ! initialized ) {

			throw new Exception( "Module has not been initialized" );
		}
		
		int jobcenterRequestId = moduleInterfaceRequest.getRequestId();
		

		log.info( "Entering Module '" + MODULE_LABEL + "', ::processRequest() called.  jobcenterRequestId: " + jobcenterRequestId );


		Map<String, String> jobParameters = moduleInterfaceRequest.getJobParameters();

		//////////////////////////


		List<String> responseMessages = new ArrayList<>();

		//////////////////////////

		

		String runId = jobParameters.get( JOB_PARAM_RUN_ID );
		try {
			if ( StringUtils.isEmpty( runId ) ) {
				String msg = methodName + ": job parameter " + JOB_PARAM_RUN_ID + " cannot be empty or null.";
				log.error( msg );
				throw new Exception( msg );
			}
			

			if ( log.isInfoEnabled() ) {
				
//				Integer jobRequiredExecutionThreads = moduleInterfaceRequest.getJobRequiredExecutionThreads();

				log.info( "Parameters from job:  '" + JOB_PARAM_RUN_ID + "' "
						+ " = '" + runId + "'" );
			}
			

			String msg = null;
			
			int overallMessageType = RunMessageTypesConstants.RUN_MESSAGE_TYPE_MSG;
			
			boolean runResult = 
					RunMetagomicsGoCountsProcessor_Main
					.getInstance().runCommandForParams( runId, jobcenterRequestId, commandToExecuteBase );
			
			if ( runResult ) {
				
				moduleInterfaceResponse.setStatusCode( JobStatusValuesConstants.JOB_STATUS_FINISHED );


				overallMessageType = RunMessageTypesConstants.RUN_MESSAGE_TYPE_MSG;
				
				msg = "Successful completion of uploadId: " + runId;
				moduleInterfaceResponse.addRunMessage( overallMessageType , msg );
				responseMessages.add( msg );


			} else {
				
				moduleInterfaceResponse.setStatusCode( JobStatusValuesConstants.JOB_STATUS_HARD_ERROR );

				overallMessageType = RunMessageTypesConstants.RUN_MESSAGE_TYPE_ERROR;
				
				msg = "Failed completion of Request Id: " + jobcenterRequestId;

				moduleInterfaceResponse.addRunMessage( overallMessageType , msg );
				responseMessages.add( msg );


			}


	} catch ( Throwable ex ) {

			log.error("General Exception: " + MODULE_LABEL + ".  This error may have already been logged.  Exception = " + ex.toString(), ex );

			moduleInterfaceResponse.setStatusCode( JobStatusValuesConstants.JOB_STATUS_HARD_ERROR );

//
//
//			//  TODO   Improve exception reporting
//
//			ByteArrayOutputStream baos = new ByteArrayOutputStream( 1000 );
//
//			PrintStream printStream = new PrintStream( baos );
//
//			ex.printStackTrace( printStream );
//
//			printStream.close();
//
//			String exStackTrace = baos.toString();
//
//
//
//			String msg = null;
//			
//			msg = "Failed completion";
//
//
//			moduleInterfaceResponse.addRunMessage( RunMessageTypesConstants.RUN_MESSAGE_TYPE_ERROR , msg );
//			responseMessages.add( msg );
//
//			msg = "submitted command to run: " + submittedCommandToRun;
//			msg = RemoveInvalidCharactersInXMLUTF8FromString.removeInvalidCharactersInXMLUTF8FromString( msg );
//			moduleInterfaceResponse.addRunMessage( RunMessageTypesConstants.RUN_MESSAGE_TYPE_ERROR , msg );
//			responseMessages.add( msg );
//
//			msg = "directory job submitted in: " + directoryJobSubmittedIn;
//			msg = RemoveInvalidCharactersInXMLUTF8FromString.removeInvalidCharactersInXMLUTF8FromString( msg );
//			moduleInterfaceResponse.addRunMessage( RunMessageTypesConstants.RUN_MESSAGE_TYPE_ERROR , msg );
//			responseMessages.add( msg );
//
//
//			
//			msg = "Exception type = " + ex.getClass().getName() + ", Exception string = " + ex.toString()
//					+ "\n" + exStackTrace;
//			msg = RemoveInvalidCharactersInXMLUTF8FromString.removeInvalidCharactersInXMLUTF8FromString( msg );
//			moduleInterfaceResponse.addRunMessage( RunMessageTypesConstants.RUN_MESSAGE_TYPE_ERROR , msg );
//			responseMessages.add( msg );

		}


		log.info( "Exitting Module " + MODULE_LABEL + " , processRequest() ." );

	}


}
