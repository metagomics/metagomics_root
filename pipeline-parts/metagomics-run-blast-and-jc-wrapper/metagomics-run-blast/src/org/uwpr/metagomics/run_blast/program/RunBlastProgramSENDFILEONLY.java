package org.uwpr.metagomics.run_blast.program;

import java.io.File;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.uwpr.metagomics.run_blast.config.GetConfigFromFileServiceImpl;
import org.uwpr.metagomics.run_blast.constants.WebURLQueryParamsConstants;
import org.uwpr.metagomics.run_blast.exceptions.RunBlastProgramAlreadyReportedErrorException;
import org.uwpr.metagomics.run_blast.webserver_get_send.SendFileToWebserver;

import jargs.gnu.CmdLineParser;
import jargs.gnu.CmdLineParser.IllegalOptionValueException;
import jargs.gnu.CmdLineParser.UnknownOptionException;

/**
 * Class to launch program
 *
 */
public class RunBlastProgramSENDFILEONLY {

	private static Logger log = Logger.getLogger(RunBlastProgramSENDFILEONLY.class);
	
	private static final int PROGRAM_EXIT_CODE_INVALID_INPUT = 1;

	private static final int PROGRAM_EXIT_CODE_ERROR = 1;

	private static final int PROGRAM_EXIT_CODE_HELP = 1;

	private static final String FOR_HELP_STRING = "For help, run without any parameters, -h, or --help";

	/**
	 * @param args
	 */
	public static void main(String[] args) {

		try {
			if ( args.length == 0 ) {

				System.err.println( "" );
				System.err.println( FOR_HELP_STRING );

				System.exit( PROGRAM_EXIT_CODE_HELP );
				
			}
			
			System.out.println( "command line args (Start)  Count: " + args.length );
			for ( int i = 0; i < args.length; i++ ) {
				System.out.println( args[ i ] );
			}
			System.out.println( "command line args (End)  Count: " + args.length );

			CmdLineParser cmdLineParser = new CmdLineParser();

			CmdLineParser.Option configFileCommandLineOpt = cmdLineParser.addStringOption( 'c', "config" );

			CmdLineParser.Option uploadIdCommandLineOpt = cmdLineParser.addStringOption( 'u', "upload-id" );

			CmdLineParser.Option jobcenterRequestIdCommandLineOpt = cmdLineParser.addStringOption( 'r', "jobcenter-request-id" );



			CmdLineParser.Option helpOpt = cmdLineParser.addBooleanOption('h', "help"); 


			// parse command line options
			try { cmdLineParser.parse(args); }
			catch (IllegalOptionValueException e) {

				System.err.println(e.getMessage());

				System.err.println( "" );
				System.err.println( FOR_HELP_STRING );

				System.exit( PROGRAM_EXIT_CODE_INVALID_INPUT );
			}
			catch (UnknownOptionException e) {

				System.err.println(e.getMessage());

				System.err.println( "" );
				System.err.println( FOR_HELP_STRING );

				System.exit( PROGRAM_EXIT_CODE_INVALID_INPUT );
			}

			Boolean help = (Boolean) cmdLineParser.getOptionValue(helpOpt, Boolean.FALSE);
			if(help) {

				printHelp();

				System.exit( PROGRAM_EXIT_CODE_HELP );
			}

			String configFile = (String)cmdLineParser.getOptionValue( configFileCommandLineOpt );
			String uploadId = (String)cmdLineParser.getOptionValue( uploadIdCommandLineOpt );
			String jobcenterRequestId = (String)cmdLineParser.getOptionValue( jobcenterRequestIdCommandLineOpt );
			
			// TEMP hard coded
//			uploadId = "1";
//			jobcenterRequestId = "16559";

			System.out.println( "configFile: |" + configFile + "|.");
			System.out.println( "uploadId: |" + uploadId + "|.");
			System.out.println( "jobcenterRequestId: |" + jobcenterRequestId + "|.");
			
			
			if ( StringUtils.isEmpty( configFile ) ) {
				System.err.println( "'config' cannot be empty or not specified" );
				System.exit( PROGRAM_EXIT_CODE_INVALID_INPUT );
			}
			if ( StringUtils.isEmpty( uploadId ) ) {
				System.err.println( "'upload-id' cannot be empty or not specified" );
				System.exit( PROGRAM_EXIT_CODE_INVALID_INPUT );
			}
			if ( StringUtils.isEmpty( jobcenterRequestId ) ) {
				System.err.println( "'jobcenter-request-id' cannot be empty or not specified" );
				System.exit( PROGRAM_EXIT_CODE_INVALID_INPUT );
			}
			
			GetConfigFromFileServiceImpl.getInstance().init( configFile );
			
			
//			RunBlastMain.getInstance().runBlastMain( threadsForBlast, databaseToBlast, uploadId, jobcenterRequestId );
			

			String post_results_webservice_url = GetConfigFromFileServiceImpl.getInstance().getPost_results_webservice_url();
			
			//  Get FASTA file from server
			
			String post_results_webservice_url_FullURL = 
					post_results_webservice_url 
					+ "?" 
					+ WebURLQueryParamsConstants.QUERY_PARAM_UPLOAD_ID
					+ "="
					+ uploadId
					+ "&"
					+ WebURLQueryParamsConstants.QUERY_PARAM_JOBCENTER_REQUEST_ID
					+ "="
					+ jobcenterRequestId
					;
			
			File blastResultsFileLocal = new File( GetConfigFromFileServiceImpl.getInstance().getBlast_output_filename() );
			
			if ( ! blastResultsFileLocal.exists() ) {
				String msg = "No Blast Results file exists: " + blastResultsFileLocal.getCanonicalPath();
				log.error(msg);
				throw new RunBlastProgramAlreadyReportedErrorException(msg);
			}
			try {
				SendFileToWebserver.getInstance().sendFileToWebserver( blastResultsFileLocal, post_results_webservice_url_FullURL );
			} catch (Exception e ) {
				throw e;
			}
			

			System.out.println( "Processing complete.");
			
			
		} catch ( RunBlastProgramAlreadyReportedErrorException e ) {
			// error already reported so just exit
			System.exit( PROGRAM_EXIT_CODE_INVALID_INPUT );
			
		} catch ( Exception e ) {
			System.out.println( "!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!");
			System.out.println( "Program exit with Exception. See syserr for exception." );
			System.err.println( "!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!");
			System.err.println( "Program exit with Exception" );
			e.printStackTrace();
			System.exit( PROGRAM_EXIT_CODE_ERROR );
			
		}
	}
	
	
	private static void printHelp() {
		
		System.err.println( "parameters (all required): ");

		System.err.println( "--config=<config file with path>");
		System.err.println( "--upload-id=<upload id>");
		System.err.println( "--jobcenter-request-id=<jobcenter-request-id>");

		System.err.println( "" );
		
		System.err.println( "Config file contents:" );
		
		System.err.println( "blast.program=<blast program with path>" );
		System.err.println( "blast.database.dir=<directory blast databases are in>" );
		
		System.err.println( "temp.file.dir=<base directory to write FASTA file and BLAST output to>" );
		
		System.err.println( "get.fasta.webservice.url=<URL to get FASTA file from>" );
		System.err.println( "post.results.webservice.url=<URL to send results to>" );
		
	}

}
