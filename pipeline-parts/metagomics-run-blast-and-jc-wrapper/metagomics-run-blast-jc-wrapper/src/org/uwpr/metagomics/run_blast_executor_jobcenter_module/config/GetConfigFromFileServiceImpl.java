package org.uwpr.metagomics.run_blast_executor_jobcenter_module.config;

import java.io.FileInputStream;
import java.io.InputStream;
import java.net.URL;
import java.util.Properties;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;

/**
 *
 *
 */
public class GetConfigFromFileServiceImpl {

    private static final Logger log = Logger.getLogger( GetConfigFromFileServiceImpl.class );

    private static final String CONFIG_FILE_NAME = "runMetagomicsBlastRun.properties";

    public static final String TRUE = "true";
    public static final String FALSE = "false";
    
	public static final String CONFIG_PROPERTY_COMMAND_TO_EXECUTE_BASE = "command.to.execute.base";
	public static final String CONFIG_PROPERTY_RUNDIR_BASE = "run.dir.base";
	public static final String CONFIG_PROPERTY_RUNDIR_DELETE_RUN_SUCCESS = "run.dir.delete.run.success";
	public static final String CONFIG_PROPERTY_RUNDIR_DELETE_RUN_FAIL = "run.dir.delete.run.fail";

	////////////////////////////////////////////


    private static final String INITIALIZATION_FAILED_MESSAGE = "Initialization failed";

    private static Properties configProps = null;


    private static boolean initializationCompleted = false;


    //  Stored as read from configuration file


	private static String commandToExecuteBase;
	private static String runDirBase;
	private static boolean runDirDeleteRunSuccess;
	private static boolean runDirDeleteRunFail;


	static {   //  Static initializer run when class is loaded

    	GetConfigFromFileServiceImpl instance = new GetConfigFromFileServiceImpl();

    	instance.getProperties();

    	instance.getAndValidateProperties();
    }


    /**
     *
     */
    private void getProperties() {

		ClassLoader thisClassLoader = this.getClass().getClassLoader();

		URL propURL = thisClassLoader.getResource( CONFIG_FILE_NAME );

		if ( propURL == null ) {

			String msg = "Config file '" + CONFIG_FILE_NAME + "' not found ";

			log.error( msg );

			throw new RuntimeException( msg );
		}

		log.info( "Config file = '" + propURL.getFile() + "'." );

		InputStream props;

		try {
			props = new FileInputStream( propURL.getFile() );

			configProps = new Properties();

			configProps.load(props);

		} catch (Exception ex) {

			String msg = "Error reading Config file '" + CONFIG_FILE_NAME + "'";

			log.error( msg + ex.toString(), ex );

			throw new RuntimeException( msg, ex );

		}
    }


	/**
	 * @param configKey
	 * @return
	 * @throws Exception
	 */
	private static String getConfigValue(String configKey) throws Exception {

		String configValue = null;

		try {

			configValue = configProps.getProperty( configKey );

		} catch (Exception ex) {
			log.error("getConfigValue(), exception = " + ex.toString(), ex );

			throw ex;
		}


		if ( configValue == null ) {

			String msg = "Config_system file entry '" + configKey + "' is missing.";

			throw new RuntimeException( msg  );
		}

		return configValue;
	}



	/**
	 * Validate that the combination of property settings are valid
	 */
	private void getAndValidateProperties() {


		try {
			commandToExecuteBase = getConfigValue( CONFIG_PROPERTY_COMMAND_TO_EXECUTE_BASE );
		} catch (Exception e) {
			String message = "The configuration file value for '" + CONFIG_PROPERTY_COMMAND_TO_EXECUTE_BASE
							+ "' was unable to be retrieved.";
			log.error( message );
			throw new RuntimeException( message, e );
		}

		if ( StringUtils.isEmpty( commandToExecuteBase ) ) {
			String message = "The configuration file value for '" + CONFIG_PROPERTY_COMMAND_TO_EXECUTE_BASE
							+ "' cannot be empty or non-existent.";
			log.error( message );
			throw new RuntimeException( message );
		}
		
		


		try {
			runDirBase = getConfigValue( CONFIG_PROPERTY_RUNDIR_BASE );
		} catch (Exception e) {
			String message = "The configuration file value for '" + CONFIG_PROPERTY_RUNDIR_BASE
							+ "' was unable to be retrieved.";
			log.error( message );
			throw new RuntimeException( message, e );
		}

		if ( StringUtils.isEmpty( runDirBase ) ) {
			String message = "The configuration file value for '" + CONFIG_PROPERTY_RUNDIR_BASE
							+ "' cannot be empty or non-existent.";
			log.error( message );
			throw new RuntimeException( message );
		}
		
		String runDirDeleteRunSuccessString = null;
		try {
			runDirDeleteRunSuccessString = getConfigValue( CONFIG_PROPERTY_RUNDIR_DELETE_RUN_SUCCESS );
		} catch (Exception e) {
			String message = "The configuration file value for '" + CONFIG_PROPERTY_RUNDIR_DELETE_RUN_SUCCESS
							+ "' was unable to be retrieved.";
			log.error( message );
			throw new RuntimeException( message, e );
		}

		if ( StringUtils.isEmpty( runDirDeleteRunSuccessString ) ) {
			String message = "The configuration file value for '" + CONFIG_PROPERTY_RUNDIR_DELETE_RUN_SUCCESS
							+ "' cannot be empty or non-existent.";
			log.error( message );
			throw new RuntimeException( message );
		}
		
		if ( TRUE.equals( runDirDeleteRunSuccessString ) ) {
			runDirDeleteRunSuccess = true;
		} else if ( FALSE.equals( runDirDeleteRunSuccessString ) ) {
			runDirDeleteRunSuccess = false;
		} else {
			String message = "The configuration file value for '" + CONFIG_PROPERTY_RUNDIR_DELETE_RUN_SUCCESS
					+ "' must be '" + TRUE + "' or '" + FALSE +"' .";
			log.error( message );
			throw new RuntimeException( message );
		}
		
		

		String runDirDeleteRunFailString = null;
		try {
			runDirDeleteRunFailString = getConfigValue( CONFIG_PROPERTY_RUNDIR_DELETE_RUN_FAIL );
		} catch (Exception e) {
			String message = "The configuration file value for '" + CONFIG_PROPERTY_RUNDIR_DELETE_RUN_FAIL
							+ "' was unable to be retrieved.";
			log.error( message );
			throw new RuntimeException( message, e );
		}

		if ( StringUtils.isEmpty( runDirDeleteRunFailString ) ) {
			String message = "The configuration file value for '" + CONFIG_PROPERTY_RUNDIR_DELETE_RUN_FAIL
							+ "' cannot be empty or non-existent.";
			log.error( message );
			throw new RuntimeException( message );
		}
		
		if ( TRUE.equals( runDirDeleteRunFailString ) ) {
			runDirDeleteRunFail = true;
		} else if ( FALSE.equals( runDirDeleteRunFailString ) ) {
			runDirDeleteRunFail = false;
		} else {
			String message = "The configuration file value for '" + CONFIG_PROPERTY_RUNDIR_DELETE_RUN_FAIL
					+ "' must be '" + TRUE + "' or '" + FALSE +"' .";
			log.error( message );
			throw new RuntimeException( message );
		}
		
		log.warn( "!!!!!!!!!  Metagomics_Run_Blast_Module Module  Configuration values  !!!!!!!!!!!!" );


		log.warn( "Command to execute base = |" + commandToExecuteBase + "|, rundirBase = |" + runDirBase 
				+ "|, " + CONFIG_PROPERTY_RUNDIR_DELETE_RUN_SUCCESS
				+ " = " + runDirDeleteRunSuccess
				+ ", " + CONFIG_PROPERTY_RUNDIR_DELETE_RUN_FAIL
				+ " = " + runDirDeleteRunFail
				 );

		initializationCompleted = true;

	}



	public static String getCommandToExecuteBase() {
		if ( ! initializationCompleted ) {
			throw new IllegalStateException( INITIALIZATION_FAILED_MESSAGE );
		}
		return commandToExecuteBase;
	}

	public static String getRunDirBase() {
		if ( ! initializationCompleted ) {
			throw new IllegalStateException( INITIALIZATION_FAILED_MESSAGE );
		}
		return runDirBase;
	}

	public static boolean isRunDirDeleteRunSuccess() {
		if ( ! initializationCompleted ) {
			throw new IllegalStateException( INITIALIZATION_FAILED_MESSAGE );
		}
		return runDirDeleteRunSuccess;
	}


	public static boolean isRunDirDeleteRunFail() {
		if ( ! initializationCompleted ) {
			throw new IllegalStateException( INITIALIZATION_FAILED_MESSAGE );
		}
		return runDirDeleteRunFail;
	}

	

}
