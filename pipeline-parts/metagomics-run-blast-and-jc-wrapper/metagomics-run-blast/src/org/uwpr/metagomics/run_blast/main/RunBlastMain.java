package org.uwpr.metagomics.run_blast.main;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

import org.apache.log4j.Logger;
import org.uwpr.metagomics.run_blast.config.GetConfigFromFileServiceImpl;
import org.uwpr.metagomics.run_blast.constants.BlastProgramConstants;
import org.uwpr.metagomics.run_blast.constants.FilenameConstants;
import org.uwpr.metagomics.run_blast.constants.WebURLQueryParamsConstants;
import org.uwpr.metagomics.run_blast.exceptions.RunBlastProgramAlreadyReportedErrorException;
import org.uwpr.metagomics.run_blast.run_system_command.RunSystemCommand;
import org.uwpr.metagomics.run_blast.run_system_command.RunSystemCommandResponse;
import org.uwpr.metagomics.run_blast.webserver_get_send.GetFileFromWebserver;
import org.uwpr.metagomics.run_blast.webserver_get_send.SendFileToWebserver;


/**
 * 
 *
 */
public class RunBlastMain {

	private static Logger log = Logger.getLogger(RunBlastMain.class);
	
	//  private constructor
	private RunBlastMain() { }
	/**
	 * @return newly created instance
	 */
	public static RunBlastMain getInstance() { 
		return new RunBlastMain(); 
	}
	
	public void runBlastMain(
			String threadsForBlast,
			String databaseToBlast,
			String runId,
			String jobcenterRequestId
			) throws Exception {
		
		String get_fasta_webservice_url = GetConfigFromFileServiceImpl.getInstance().getGet_fasta_webservice_url();
		String post_results_webservice_url = GetConfigFromFileServiceImpl.getInstance().getPost_results_webservice_url();
		
		String programToRun = GetConfigFromFileServiceImpl.getInstance().getBlast_program();
		String blastOutputFilename = GetConfigFromFileServiceImpl.getInstance().getBlast_output_filename();
		String blastOutputFormat = GetConfigFromFileServiceImpl.getInstance().getBlast_output_format();
		String blast_database_dir = GetConfigFromFileServiceImpl.getInstance().getBlast_database_dir();
		
		//  Get FASTA file from server
		
		String get_fasta_webservice_url_FullURL = 
				get_fasta_webservice_url 
				+ "?" 
				+ WebURLQueryParamsConstants.QUERY_PARAM_UPLOAD_ID
				+ "="
				+ runId;
		
		File fastaFileLocal = new File( FilenameConstants.LOCAL_FASTA_FILENAME );
		
		try {
			GetFileFromWebserver.getInstance().getFileFromWebserver( fastaFileLocal, get_fasta_webservice_url_FullURL );
		} catch (Exception e ) {
			throw e;
		}
		
		//  Run Blast

		String blast_command = 
				 programToRun +
				" -query " +  FilenameConstants.LOCAL_FASTA_FILENAME +   
				" -task " +  BlastProgramConstants.BLAST_PROGRAM + 
				" -db " + blast_database_dir +  "/" +  databaseToBlast +  
				" -outfmt \"" +  blastOutputFormat + "\"" +
				" -out " +  blastOutputFilename + 
				" -num_threads " +  threadsForBlast 
		
		//  Left off
//				 +
//				" -num_descriptions " + "20" //  num_descriptions
				
				;
		
		System.out.println( "Command to run Blast (in file " + FilenameConstants.RUN_BLAST_SHELL_SCRIPT_FILENAME
				+ "): " + blast_command);

		File outputFile = new File( FilenameConstants.RUN_BLAST_SHELL_SCRIPT_FILENAME );
		
		BufferedWriter writer = null;
		try {
			writer= new BufferedWriter( new FileWriter( outputFile ) );
			writer.write( "#!/bin/bash" );
			writer.newLine();
			writer.write( blast_command );
			writer.newLine();
		} finally {
			if ( writer != null ) {
				writer.close();
			}
		}
		
		if ( ! outputFile.setExecutable(true) ) {
			String msg = "Failed to set as executable: " + outputFile.getCanonicalPath();
			log.error(msg);
			throw new RunBlastProgramAlreadyReportedErrorException( msg );
		}
		
		String[] run_blast_command = { "./" + FilenameConstants.RUN_BLAST_SHELL_SCRIPT_FILENAME };
		
		File dirToRunCommandIn = new File( "." );
		File fileToWriteSysoutTo = new File( "Z_actualRunBlastPgm_sysout.txt" );
		File fileToWriteSyserrTo = new File( "Z_actualRunBlastPgm_syserr.txt" );
		try {
			RunSystemCommandResponse runSystemCommandResponse = 
					RunSystemCommand.getInstance().runCmd( 
							run_blast_command, dirToRunCommandIn, fileToWriteSysoutTo, fileToWriteSyserrTo, 
							false /* throwExceptionOnCommandFailure */ );
			if ( ! runSystemCommandResponse.isCommandSuccessful() ) {
				String msg = "Run Blast failed";
				log.error( msg );
				throw new RunBlastProgramAlreadyReportedErrorException(msg);
			}
		} catch (Exception e ) {
			throw e;
		}

		//  Send BLAST results to server

		File blastResultsFileLocal = new File( blastOutputFilename );
		
		if ( ! blastResultsFileLocal.exists() ) {
			String msg = "No Blast Results file exists: " + blastResultsFileLocal.getCanonicalPath();
			log.error(msg);
			throw new RunBlastProgramAlreadyReportedErrorException(msg);
		}
		
		String blastOutputFormatURIEncoded = URLEncoder.encode( blastOutputFormat, StandardCharsets.UTF_8.name() );
		
		String post_results_webservice_url_FullURL = 
				post_results_webservice_url 
				+ "?" 
				+ WebURLQueryParamsConstants.QUERY_PARAM_RUN_ID
				+ "="
				+ runId
				+ "&"
				+ WebURLQueryParamsConstants.QUERY_PARAM_UPLOAD_ID
				+ "="
				+ runId
				+ "&"
				+ WebURLQueryParamsConstants.QUERY_PARAM_JOBCENTER_REQUEST_ID
				+ "="
				+ jobcenterRequestId
				+ "&"
				+ WebURLQueryParamsConstants.QUERY_PARAM_BLAST_OUTPUT_FORMAT
				+ "="
				+ blastOutputFormatURIEncoded
				;

		try {
			SendFileToWebserver.getInstance().sendFileToWebserver( blastResultsFileLocal, post_results_webservice_url_FullURL );
		} catch (Exception e ) {
			throw e;
		}
		
		System.out.println( "Processing complete for run id:" + runId );
	}
}
