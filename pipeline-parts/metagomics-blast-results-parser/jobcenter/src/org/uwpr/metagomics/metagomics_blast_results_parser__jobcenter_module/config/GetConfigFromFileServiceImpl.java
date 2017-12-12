package org.uwpr.metagomics.metagomics_blast_results_parser__jobcenter_module.config;

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

    private static final String CONFIG_FILE_NAME = "runMetagomicsBlastResultsParser.properties";

	public static final String CONFIG_PROPERTY_COMMAND_TO_EXECUTE_BASE = "command.to.execute.base";


	////////////////////////////////////////////


    private static final String INITIALIZATION_FAILED_MESSAGE = "Initialization failed";

    private static Properties configProps = null;


    private static boolean initializationCompleted = false;


    //  Stored as read from configuration file


	private static String commandToExecuteBase;








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
		
		
		


		log.warn( "!!!!!!!!!  Metagomics_FASTA_Importer_Module Module  Configuration values  !!!!!!!!!!!!" );


		log.warn( "Command to execute base = |" + commandToExecuteBase + "|." );

		initializationCompleted = true;

	}



	public static String getCommandToExecuteBase() {

		if ( ! initializationCompleted ) {

			throw new IllegalStateException( INITIALIZATION_FAILED_MESSAGE );
		}
		return commandToExecuteBase;
	}




	

}
