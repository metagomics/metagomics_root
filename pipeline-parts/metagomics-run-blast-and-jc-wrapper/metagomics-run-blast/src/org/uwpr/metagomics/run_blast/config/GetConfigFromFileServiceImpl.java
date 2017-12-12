package org.uwpr.metagomics.run_blast.config;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.Properties;

import org.apache.commons.lang3.StringUtils;
import org.uwpr.metagomics.run_blast.exceptions.RunBlastProgramAlreadyReportedErrorException;
//import org.apache.log4j.Logger;

/**
 *
 * Singleton Class
 */
public class GetConfigFromFileServiceImpl {

//    private static final Logger log = Logger.getLogger( GetConfigFromFileServiceImpl.class );

	private static final String CONFIG_PROPERTY_BLAST_PROGRAM = "blast.program";
	private static final String CONFIG_PROPERTY_BLAST_OUTPUT_FILENAME = "blast.output.filename";
	private static final String CONFIG_PROPERTY_BLAST_OUTPUT_FORMAT = "blast.output.format";
	private static final String CONFIG_PROPERTY_BLAST_DATABASE_DIR = "blast.database.dir";
	private static final String CONFIG_PROPERTY_GET_FASTA_WEBSERVICE_URL = "get.fasta.webservice.url";
	private static final String CONFIG_PROPERTY_POST_RESULTS_WEBSERVICE_URL = "post.results.webservice.url";

//	blast.program=<blast program with path>
//	blast.database.dir=<directory blast databases are in>
//	
//	get.fasta.webservice.url=<URL to get FASTA file from>
//	post.results.webservice.url=<URL to send results to>
	

	////////////////////////////////////////////


    private static final String INITIALIZATION_FAILED_MESSAGE = "Initialization failed";
    
    public static GetConfigFromFileServiceImpl getInstance() {
    	return instance;
    }
    
    /**
     * private constructor
     */
    private GetConfigFromFileServiceImpl() {}

    
    private static final GetConfigFromFileServiceImpl instance = new GetConfigFromFileServiceImpl();
    
    private Properties configProps = null;


    private boolean initializationCompleted = false;


    //  Stored as read from configuration file

	private String blast_program;
	private String blast_output_filename;
	private String blast_output_format;

	private String blast_database_dir;
	private String get_fasta_webservice_url;
	private String post_results_webservice_url;
	
	public String getBlast_program() {
		if ( ! initializationCompleted ) {
			throw new IllegalStateException( INITIALIZATION_FAILED_MESSAGE );
		}
		return blast_program;
	}
	public String getBlast_output_filename() {
		if ( ! initializationCompleted ) {
			throw new IllegalStateException( INITIALIZATION_FAILED_MESSAGE );
		}
		return blast_output_filename;
	}
	public String getBlast_output_format() {
		if ( ! initializationCompleted ) {
			throw new IllegalStateException( INITIALIZATION_FAILED_MESSAGE );
		}
		return blast_output_format;
	}
	public String getBlast_database_dir() {
		if ( ! initializationCompleted ) {
			throw new IllegalStateException( INITIALIZATION_FAILED_MESSAGE );
		}
		return blast_database_dir;
	}
	public String getGet_fasta_webservice_url() {
		if ( ! initializationCompleted ) {
			throw new IllegalStateException( INITIALIZATION_FAILED_MESSAGE );
		}
		return get_fasta_webservice_url;
	}
	public String getPost_results_webservice_url() {
		if ( ! initializationCompleted ) {
			throw new IllegalStateException( INITIALIZATION_FAILED_MESSAGE );
		}
		return post_results_webservice_url;
	}



	/**
	 * @param configFilenameWithPath
	 * @throws RunBlastProgramAlreadyReportedErrorException 
	 */
	public void init( String configFilenameWithPath ) throws RunBlastProgramAlreadyReportedErrorException {
		
		File configFile = new File( configFilenameWithPath );
		
		if ( ! configFile.exists() ) {
			System.err.println( "Config file not found: " + configFilenameWithPath );
			throw new RunBlastProgramAlreadyReportedErrorException();
		}
		
		InputStream props;

		try {
			props = new FileInputStream( configFile );

			configProps = new Properties();

			configProps.load(props);

		} catch (Exception ex) {

			String msg = "Error reading Config file '" + configFilenameWithPath + "'";
			System.err.println( msg );
			ex.printStackTrace();
			throw new RunBlastProgramAlreadyReportedErrorException( msg, ex );

		}
		


		try {
			blast_program = getConfigValue( CONFIG_PROPERTY_BLAST_PROGRAM );
		} catch (Exception e) {
			String message = "The configuration file value for '" + CONFIG_PROPERTY_BLAST_PROGRAM + "' was unable to be retrieved.";
			System.err.println( message );
			e.printStackTrace();
			throw new RunBlastProgramAlreadyReportedErrorException( message, e );
		}
		
		if ( StringUtils.isEmpty( blast_program ) ) {
			String message = "The configuration file value for '" + CONFIG_PROPERTY_BLAST_PROGRAM + "' cannot be empty.";
			System.err.println( message );
			throw new RunBlastProgramAlreadyReportedErrorException( message );
		}
		
		try {
			blast_output_filename = getConfigValue( CONFIG_PROPERTY_BLAST_OUTPUT_FILENAME );
		} catch (Exception e) {
			String message = "The configuration file value for '" + CONFIG_PROPERTY_BLAST_OUTPUT_FILENAME + "' was unable to be retrieved.";
			System.err.println( message );
			e.printStackTrace();
			throw new RunBlastProgramAlreadyReportedErrorException( message, e );
		}
		if ( StringUtils.isEmpty( blast_output_filename ) ) {
			String message = "The configuration file value for '" + CONFIG_PROPERTY_BLAST_OUTPUT_FILENAME + "' cannot be empty.";
			System.err.println( message );
			throw new RunBlastProgramAlreadyReportedErrorException( message );
		}
		
		try {
			blast_output_format = getConfigValue( CONFIG_PROPERTY_BLAST_OUTPUT_FORMAT );
		} catch (Exception e) {
			String message = "The configuration file value for '" + CONFIG_PROPERTY_BLAST_OUTPUT_FORMAT + "' was unable to be retrieved.";
			System.err.println( message );
			e.printStackTrace();
			throw new RunBlastProgramAlreadyReportedErrorException( message, e );
		}
		if ( StringUtils.isEmpty( blast_output_format ) ) {
			String message = "The configuration file value for '" + CONFIG_PROPERTY_BLAST_OUTPUT_FORMAT + "' cannot be empty.";
			System.err.println( message );
			throw new RunBlastProgramAlreadyReportedErrorException( message );
		}

		try {
			blast_database_dir = getConfigValue( CONFIG_PROPERTY_BLAST_DATABASE_DIR );
		} catch (Exception e) {
			String message = "The configuration file value for '" + CONFIG_PROPERTY_BLAST_DATABASE_DIR + "' was unable to be retrieved.";
			System.err.println( message );
			e.printStackTrace();
			throw new RunBlastProgramAlreadyReportedErrorException( message, e );
		}
		if ( StringUtils.isEmpty( blast_database_dir ) ) {
			String message = "The configuration file value for '" + CONFIG_PROPERTY_BLAST_DATABASE_DIR + "' cannot be empty.";
			System.err.println( message );
			throw new RunBlastProgramAlreadyReportedErrorException( message );
		}

		try {
			get_fasta_webservice_url = getConfigValue( CONFIG_PROPERTY_GET_FASTA_WEBSERVICE_URL );
		} catch (Exception e) {
			String message = "The configuration file value for '" + CONFIG_PROPERTY_GET_FASTA_WEBSERVICE_URL + "' was unable to be retrieved.";
			System.err.println( message );
			e.printStackTrace();
			throw new RunBlastProgramAlreadyReportedErrorException( message, e );
		}
		if ( StringUtils.isEmpty( get_fasta_webservice_url ) ) {
			String message = "The configuration file value for '" + CONFIG_PROPERTY_GET_FASTA_WEBSERVICE_URL + "' cannot be empty.";
			System.err.println( message );
			throw new RunBlastProgramAlreadyReportedErrorException( message );
		}

		try {
			post_results_webservice_url = getConfigValue( CONFIG_PROPERTY_POST_RESULTS_WEBSERVICE_URL );
		} catch (Exception e) {
			String message = "The configuration file value for '" + CONFIG_PROPERTY_POST_RESULTS_WEBSERVICE_URL + "' was unable to be retrieved.";
			System.err.println( message );
			e.printStackTrace();
			throw new RunBlastProgramAlreadyReportedErrorException( message, e );
		}
		if ( StringUtils.isEmpty( post_results_webservice_url ) ) {
			String message = "The configuration file value for '" + CONFIG_PROPERTY_POST_RESULTS_WEBSERVICE_URL + "' cannot be empty.";
			System.err.println( message );
			throw new RunBlastProgramAlreadyReportedErrorException( message );
		}

		initializationCompleted = true;
    }


	/**
	 * @param configKey
	 * @return
	 * @throws Exception
	 */
	private String getConfigValue(String configKey) throws Exception {

		String configValue = null;
		try {
			configValue = configProps.getProperty( configKey );
		} catch (Exception ex) {
			System.err.println( "getConfigValue(), exception = " + ex.toString() );
			ex.printStackTrace();
			throw ex;
		}
		if ( configValue == null ) {
			String msg = "Config_system file entry '" + configKey + "' is missing.";
			System.err.println( msg );
			throw new RunBlastProgramAlreadyReportedErrorException( msg  );
		}

		return configValue;
	}


	

}
