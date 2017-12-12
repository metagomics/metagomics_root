package org.uwpr.metagomics.fasta_importer_executor_jobcenter_module.main;

import org.apache.log4j.Logger;
import org.uwpr.metagomics.fasta_importer_executor_jobcenter_module.run_system_command.RunSystemCommand;

/**
 * 
 *
 */
public class RunMetagomicsFastaParser_Main {

	private static Logger log = Logger.getLogger(RunMetagomicsFastaParser_Main.class);
	
	
	//  Private constructor

	private RunMetagomicsFastaParser_Main() { }
	

	/**
	 * @return
	 */
	public static RunMetagomicsFastaParser_Main getInstance() {

		return new RunMetagomicsFastaParser_Main();
	}

	
	public boolean runCommandForParams( String fastaFileId, String commandToExecuteBase ) throws Throwable {
		
		String cmd = commandToExecuteBase + " " + fastaFileId;
		
		RunSystemCommand.runCmd( cmd, true /* throwExceptionOnCommandFailure */ );
		
		return true;
	}


}
