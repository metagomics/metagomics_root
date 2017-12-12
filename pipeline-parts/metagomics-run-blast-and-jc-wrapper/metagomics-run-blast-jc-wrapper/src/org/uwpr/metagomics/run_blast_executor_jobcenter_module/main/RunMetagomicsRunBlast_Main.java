package org.uwpr.metagomics.run_blast_executor_jobcenter_module.main;

import java.io.File;
import java.text.DecimalFormat;
import java.util.Arrays;
import java.util.List;

import org.apache.log4j.Logger;
import org.uwpr.metagomics.run_blast_executor_jobcenter_module.config.GetConfigFromFileServiceImpl;
import org.uwpr.metagomics.run_blast_executor_jobcenter_module.exceptions.MetagomicsRunException;
import org.uwpr.metagomics.run_blast_executor_jobcenter_module.run_system_command.RunSystemCommand;
import org.uwpr.metagomics.run_blast_executor_jobcenter_module.run_system_command.RunSystemCommandResponse;


/**
 * 
 *
 */
public class RunMetagomicsRunBlast_Main {

	private static Logger log = Logger.getLogger(RunMetagomicsRunBlast_Main.class);
	
	
	//  Private constructor

	private RunMetagomicsRunBlast_Main() { }
	

	/**
	 * @return
	 */
	public static RunMetagomicsRunBlast_Main getInstance() {
		return new RunMetagomicsRunBlast_Main();
	}
	
	
	public void runCommandForParams( 
			String runId, 
			String databaseToBlast, 
			int threads,
			int jobcenterRequestId ) throws Throwable {

		//  Values from config properties file
		
		String commandToExecuteBaseString = GetConfigFromFileServiceImpl.getCommandToExecuteBase();
		
		//  Split   commandToExecuteBaseString into program and parameters on white space
		
		String[] commandToExecuteBaseStringSplit = commandToExecuteBaseString.split( "\\s+" );
		
		
		
		String runDirBaseString = GetConfigFromFileServiceImpl.getRunDirBase();
		
		File runDirBase = new File ( runDirBaseString  ); 

		if ( ! runDirBase.exists() ) {
			String msg = "Run directory specified in config dir does not exist.  Config key: " 
					+ GetConfigFromFileServiceImpl.CONFIG_PROPERTY_RUNDIR_BASE
					+ "   Run Directory: " + runDirBaseString;
			log.error( msg );
			throw new Exception( msg );
		}
		
		//  Create a directory to process this JC request in

		//  Increment "counter" until get a value where the directory name is not on the system
		File dirToRunCommandIn = null;
		int counter = 0;
		boolean dirNotFound = false;

		DecimalFormat fmtIdsDecimalFormat = new DecimalFormat( "00000" );

		String runIdForFilename = runId;
		try {
			int runIdInt = Integer.parseInt( runId );
			runIdForFilename = fmtIdsDecimalFormat.format( runIdInt );
		} catch ( Exception e ) {
			
		}
			
		String jobcenterRequestIdFmt = fmtIdsDecimalFormat.format( jobcenterRequestId );

		while ( ! dirNotFound ) {
			counter++;
			String dirToRunCommandInString = "run_id_" + runIdForFilename
					+ "_jc_req_id_" + jobcenterRequestIdFmt
					+ "_try_counter_" + counter;
			dirToRunCommandIn = new File ( runDirBase, dirToRunCommandInString  );
			if ( ! dirToRunCommandIn.exists() ) {
				dirNotFound = true;
			}
		}
		
		try {
			if ( ! dirToRunCommandIn.mkdir() ) {
				String msg = "Failed to make directory to process this JC request in: " + dirToRunCommandIn.getCanonicalPath();
				log.error( msg );
				throw new Exception( msg );
			}
		} catch ( Exception e ) {
			String msg = "Failed to make directory to process this JC request in: " + dirToRunCommandIn.getCanonicalPath();
			log.error( msg, e );
			throw new Exception( msg, e );

		}

		String[] commandArguments =
			{
				"--run-id=" + runId
				, "--database-to-blast=" + databaseToBlast
				, "--threads-for-blast=" + threads
				, "--jobcenter-request-id=" + jobcenterRequestId  };

		int commandAndItsArgumentsLength = 
				commandToExecuteBaseStringSplit.length
				+ commandArguments.length;

		//  Create commandAndItsArguments and populate it
		String[] commandAndItsArguments = new String[ commandAndItsArgumentsLength ];
		int commandAndItsArgumentsIndex = 0;
		for ( int index = 0; index < commandToExecuteBaseStringSplit.length; index++ ) {
			commandAndItsArguments[ commandAndItsArgumentsIndex ] = commandToExecuteBaseStringSplit[ index ];
			commandAndItsArgumentsIndex++;
		}
		for ( int index = 0; index < commandArguments.length; index++ ) {
			commandAndItsArguments[ commandAndItsArgumentsIndex ] = commandArguments[ index ];
			commandAndItsArgumentsIndex++;
		}
		
		File fileToWriteSysoutTo = new File( dirToRunCommandIn, "Z_runPgmScript_RunBlast_sysout.txt" );
		File fileToWriteSyserrTo = new File( dirToRunCommandIn, "Z_runPgmScript_RunBlast_syserr.txt" );

		RunSystemCommandResponse runSystemCommandResponse =
				RunSystemCommand.getInstance().runCmd(
						commandAndItsArguments, 
						dirToRunCommandIn, fileToWriteSysoutTo, fileToWriteSyserrTo, 
						false /* throwExceptionOnCommandFailure */ );

		if ( ! runSystemCommandResponse.isCommandSuccessful() ) {
			
			List<String> commandAndItsArgumentsList = Arrays.asList( commandAndItsArguments );

			String msg = "Failed to run command '" + commandAndItsArgumentsList
					+ "' in directory " + dirToRunCommandIn.getCanonicalPath();
			log.error( msg );
			
			throw new MetagomicsRunException( msg );
		}
	}


}
