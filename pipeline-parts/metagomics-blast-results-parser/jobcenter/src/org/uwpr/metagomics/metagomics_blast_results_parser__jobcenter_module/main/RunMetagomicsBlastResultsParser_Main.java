package org.uwpr.metagomics.metagomics_blast_results_parser__jobcenter_module.main;

import org.apache.log4j.Logger;
import org.uwpr.metagomics.metagomics_blast_results_parser__jobcenter_module.run_system_command.RunSystemCommand;

/**
 * 
 *
 */
public class RunMetagomicsBlastResultsParser_Main {

	private static Logger log = Logger.getLogger(RunMetagomicsBlastResultsParser_Main.class);
	
	
	//  Private constructor

	private RunMetagomicsBlastResultsParser_Main() { }
	

	/**
	 * @return
	 */
	public static RunMetagomicsBlastResultsParser_Main getInstance() {

		return new RunMetagomicsBlastResultsParser_Main();
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
