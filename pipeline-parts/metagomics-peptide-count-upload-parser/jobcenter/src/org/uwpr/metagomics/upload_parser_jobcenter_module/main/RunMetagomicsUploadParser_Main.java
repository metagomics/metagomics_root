package org.uwpr.metagomics.upload_parser_jobcenter_module.main;

import org.apache.log4j.Logger;
import org.uwpr.metagomics.upload_parser_jobcenter_module.run_system_command.RunSystemCommand;

/**
 * 
 *
 */
public class RunMetagomicsUploadParser_Main {

	private static Logger log = Logger.getLogger(RunMetagomicsUploadParser_Main.class);
	
	
	//  Private constructor

	private RunMetagomicsUploadParser_Main() { }
	

	/**
	 * @return
	 */
	public static RunMetagomicsUploadParser_Main getInstance() {

		return new RunMetagomicsUploadParser_Main();
	}

	
	public boolean runCommandForParams( 
			String runId,
			int jobcenterRequestId,
			String commandToExecuteBase ) throws Throwable {
		
		String cmd = 
				commandToExecuteBase 
				+ "  " + runId
				+ "  "  + jobcenterRequestId;
		

		RunSystemCommand.runCmd( cmd, true /* throwExceptionOnCommandFailure */ );
		
		return true;
	}


}
