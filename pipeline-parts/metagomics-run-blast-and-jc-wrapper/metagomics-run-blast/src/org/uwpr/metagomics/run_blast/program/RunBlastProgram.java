package org.uwpr.metagomics.run_blast.program;

import org.apache.commons.lang3.StringUtils;
import org.uwpr.metagomics.run_blast.config.GetConfigFromFileServiceImpl;
import org.uwpr.metagomics.run_blast.exceptions.RunBlastProgramAlreadyReportedErrorException;
import org.uwpr.metagomics.run_blast.main.RunBlastMain;

import jargs.gnu.CmdLineParser;
import jargs.gnu.CmdLineParser.IllegalOptionValueException;
import jargs.gnu.CmdLineParser.UnknownOptionException;

/**
 * Class to launch program
 *
 */
public class RunBlastProgram {

	private static final int PROGRAM_EXIT_CODE_INVALID_INPUT = 1;

	private static final int PROGRAM_EXIT_CODE_ERROR = 1;

	private static final int PROGRAM_EXIT_CODE_HELP = 1;

	private static final String FOR_HELP_STRING = "For help, run without any parameters, -h, or --help";

	/**
	 * @param args
	 */
	public static void main(String[] args) {

		System.out.println( "Starting execution of MetagomicsRunBlast_MainCode.jar");
		
		System.out.println( "START: Command line parameters (1 per line):" );
		if ( args.length == 0 ) {
			System.out.println( "NO COMMAND LINE PARAMETERS");
		} else {
			for ( String arg : args) {
				System.out.println( arg );
			}
		}
		System.out.println( "END: Command line parameters:" );
		
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

			CmdLineParser.Option threadsForBlastCommandLineOpt = cmdLineParser.addStringOption( 't', "threads-for-blast" );

			CmdLineParser.Option databaseToBlastCommandLineOpt = cmdLineParser.addStringOption( 'd', "database-to-blast" );

			CmdLineParser.Option runIdCommandLineOpt = cmdLineParser.addStringOption( 'r', "run-id" );

			CmdLineParser.Option jobcenterRequestIdCommandLineOpt = cmdLineParser.addStringOption( 'j', "jobcenter-request-id" );



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
			String threadsForBlast = (String)cmdLineParser.getOptionValue( threadsForBlastCommandLineOpt );
			String databaseToBlast = (String)cmdLineParser.getOptionValue( databaseToBlastCommandLineOpt );
			String runId = (String)cmdLineParser.getOptionValue( runIdCommandLineOpt );
			String jobcenterRequestId = (String)cmdLineParser.getOptionValue( jobcenterRequestIdCommandLineOpt );

			if ( StringUtils.isEmpty( configFile ) ) {
				System.err.println( "'config' cannot be empty or not specified" );
				System.exit( PROGRAM_EXIT_CODE_INVALID_INPUT );
			}
			if ( StringUtils.isEmpty( threadsForBlast ) ) {
				System.err.println( "'threads-for-blast' cannot be empty or not specified" );
				System.exit( PROGRAM_EXIT_CODE_INVALID_INPUT );
			}
			if ( StringUtils.isEmpty( databaseToBlast ) ) {
				System.err.println( "'database-to-blast' cannot be empty or not specified" );
				System.exit( PROGRAM_EXIT_CODE_INVALID_INPUT );
			}
			if ( StringUtils.isEmpty( runId ) ) {
				System.err.println( "'run-id' cannot be empty or not specified" );
				System.exit( PROGRAM_EXIT_CODE_INVALID_INPUT );
			}
			if ( StringUtils.isEmpty( jobcenterRequestId ) ) {
				System.err.println( "'jobcenter-request-id' cannot be empty or not specified" );
				System.exit( PROGRAM_EXIT_CODE_INVALID_INPUT );
			}
			
			GetConfigFromFileServiceImpl.getInstance().init( configFile );
			
			
			RunBlastMain.getInstance().runBlastMain( threadsForBlast, databaseToBlast, runId, jobcenterRequestId );
			
			
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
		System.err.println( "--threads-for-blast=<# threads to pass to blast>");
		System.err.println( "--database-to-blast=<database to blast against>");
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
