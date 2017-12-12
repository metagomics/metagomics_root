package org.uwpr.metagomics.go_counts_processor.main;

import org.apache.log4j.Logger;
import org.uwpr.metagomics.go_counts_processor.run_system_command.RunSystemCommand;

/**
 * 
 *
 */
public class RunMetagomicsGoCountsProcessor_Main {

	private static Logger log = Logger.getLogger(RunMetagomicsGoCountsProcessor_Main.class);
	
	
	//  Private constructor

	private RunMetagomicsGoCountsProcessor_Main() { }
	

	/**
	 * @return
	 */
	public static RunMetagomicsGoCountsProcessor_Main getInstance() {

		return new RunMetagomicsGoCountsProcessor_Main();
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
