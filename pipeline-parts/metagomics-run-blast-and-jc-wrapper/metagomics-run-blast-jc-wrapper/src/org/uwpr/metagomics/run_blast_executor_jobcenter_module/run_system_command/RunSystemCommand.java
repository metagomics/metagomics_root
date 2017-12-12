package org.uwpr.metagomics.run_blast_executor_jobcenter_module.run_system_command;

import java.io.File;
import java.io.OutputStream;
import java.lang.ProcessBuilder.Redirect;
import java.util.Arrays;
import java.util.List;
import org.apache.log4j.Logger;
import org.uwpr.metagomics.run_blast_executor_jobcenter_module.exceptions.MetagomicsConfigException;

/**
 * 
 *
 */
public class RunSystemCommand {
	
	private static Logger log = Logger.getLogger(RunSystemCommand.class);
	
	//  private constructor
	private RunSystemCommand() { }
	/**
	 * @return newly created instance
	 */
	public static RunSystemCommand getInstance() { 
		return new RunSystemCommand(); 
	}
	
	//  wait time before check for program done
//	private final int WAIT_TIME_TO_FOR_STREAM_COPY_THREADS_TO_COMPLETE = 10;  //  in seconds
	
	private volatile boolean shutdownRequested = false;
	private volatile Process process = null;
	private volatile OutputStream outputStreamToProcessStdIn = null;
	private volatile boolean inWaitFor = false;
	/**
	 * awaken thread to get next import or to complete
	 */
	public void awaken() {
		synchronized (this) {
//			exittedWaitDueToAwakenNotify = true;
			notify();
		}
	}
	
	/**
	 * @param commandAndItsArgumentsAsList
	 * @param dirToRunCommandIn
	 * @param fileToWriteSysoutTo
	 * @param fileToWriteSyserrTo
	 * @param throwExceptionOnCommandFailure
	 * @return
	 * @throws Exception 
	 * @throws Throwable
	 */
	public RunSystemCommandResponse runCmd( 
			String[] commandAndItsArgumentsArray,
			File dirToRunCommandIn, 
			File fileToWriteSysoutTo,
			File fileToWriteSyserrTo,
			boolean throwExceptionOnCommandFailure ) throws Exception
	{
		List<String> commandAndItsArgumentsList = Arrays.asList( commandAndItsArgumentsArray );

		if ( shutdownRequested ) {
			String msg = "Object not usable once shutdownRequested is true";
			log.error( msg );
			throw new IllegalStateException(msg);
		}
		if ( log.isInfoEnabled() ) {
			log.info( "runCmd: running program (arguments list comma delim):  " + commandAndItsArgumentsList );
		}
		if ( fileToWriteSysoutTo.exists() ) {
			if ( ! fileToWriteSysoutTo.delete() ) {
				String msg = "Unable to delete previous file to fileToWriteSysoutTo: " + fileToWriteSysoutTo.getCanonicalPath();
				log.error( msg );
				throw new MetagomicsConfigException(msg);
			}
		}
		if ( fileToWriteSyserrTo.exists() ) {
			if ( ! fileToWriteSyserrTo.delete() ) {
				String msg = "Unable to delete previous file to fileToWriteSyserrTo: " + fileToWriteSyserrTo.getCanonicalPath();
				log.error( msg );
				throw new MetagomicsConfigException(msg);
			}
		}
		try {
			if ( ! fileToWriteSysoutTo.createNewFile() ) {
				String msg = "Unable to write to fileToWriteSysoutTo: " + fileToWriteSysoutTo.getCanonicalPath();
				log.error( msg );
				throw new MetagomicsConfigException(msg);
			}
		} catch (Exception e ) {
			String msg = "Unable to write to fileToWriteSysoutTo: " + fileToWriteSysoutTo.getCanonicalPath();
			log.error( msg, e );
			throw new MetagomicsConfigException(msg, e);
		}
		try {
			if ( ! fileToWriteSyserrTo.createNewFile() ) {
				String msg = "Unable to write to fileToWriteSyserrTo: " + fileToWriteSyserrTo.getCanonicalPath();
				log.error( msg );
				throw new MetagomicsConfigException(msg);
			}
		} catch (Exception e ) {
			String msg = "Unable to write to fileToWriteSyserrTo: " + fileToWriteSyserrTo.getCanonicalPath();
			log.error( msg, e );
			throw new MetagomicsConfigException(msg, e);
		}
		RunSystemCommandResponse runSystemCommandResponse = new RunSystemCommandResponse();
		runSystemCommandResponse.setCommandSuccessful( false );
		outputStreamToProcessStdIn = null;
		try {
			ProcessBuilder processBuilder = new ProcessBuilder( commandAndItsArgumentsArray );
			processBuilder.directory( dirToRunCommandIn );
			processBuilder.redirectOutput( Redirect.to( fileToWriteSysoutTo ) );
			processBuilder.redirectError( Redirect.to( fileToWriteSyserrTo ) );
			process = processBuilder.start();
			outputStreamToProcessStdIn = process.getOutputStream();
			try {
				inWaitFor = true;
				process.waitFor();
			} finally {
				inWaitFor = false;
			}
			if ( shutdownRequested ) {
				//  Shutdown Requested so early exit
				String msg = "Shutdown requested so early exit with runSystemCommandResponse.setShutdownRequested( true );";
				log.warn( msg );
				runSystemCommandResponse.setShutdownRequested( true );
				return runSystemCommandResponse;  //  EARLY EXIT
			}
			int exitValue = process.exitValue();
			runSystemCommandResponse.setCommandExitCode( exitValue );
			if ( exitValue != 0 ) {
				String message = "runCmd: execute command returned non-zero.  exitValue = " + exitValue 
						+ ", commandAndItsArgumentsList (arguments list comma delim) = " + commandAndItsArgumentsList;
				log.error(message);
				if ( throwExceptionOnCommandFailure ) {
					throw new Exception( message );
				}
				runSystemCommandResponse.setCommandSuccessful( false );
			} else {
				runSystemCommandResponse.setCommandSuccessful( true );
				if ( log.isInfoEnabled() ) {
					log.info( "runCmd: run program (arguments list comma delim) SUCCESSFUL:  " + commandAndItsArgumentsList );
				}
			}
			if ( shutdownRequested ) {
				//  Shutdown Requested so early exit
				runSystemCommandResponse.setShutdownRequested( true );
			}
		} catch ( Exception e ) {
			log.error( "Exception running program (arguments list comma delim) |" + commandAndItsArgumentsList + "|.  Exception: " + e.toString(), e );
			throw e;
		} finally {
//			process.getInputStream() and process.getErrorStream() are closed in the StreamCopyToFileThread objects
			if ( outputStreamToProcessStdIn != null ) {
				try {
					outputStreamToProcessStdIn.close();
				} catch ( Throwable t ) {
					log.info( "Exception closing stdin to process: " + t.toString(), t );
				}
				outputStreamToProcessStdIn = null;
			}
		}
		return runSystemCommandResponse;
	}
	
	/**
	 * Called on a separate thread when a shutdown request comes from the operating system.
	 * If this is not heeded, the process may be killed by the operating system after some time has passed ( controlled by the operating system )
	 */
	public void shutdown() {
		shutdownRequested = true;
//		try {
//
//			if ( outputStreamToProcessStdIn != null ) {
//
//				outputStreamToProcessStdIn.write( StringSendImporterToRequestShutdownConstants.SHUTDOWN );
//				
//				outputStreamToProcessStdIn.flush();
//				
//				outputStreamToProcessStdIn.close();
//				
//				outputStreamToProcessStdIn = null;
//
//				//  TODO  maybe do something here
//			}
//
//		} catch ( NullPointerException e ) {
//			
//			//  Eat the NullPointerException since that meant that nothing had to be done.
//			
//		} catch ( Throwable t ) {
//			
//			String msg = "Exception thrown sending shutdown text to child import process";
//			log.error( msg );
//			
//		}
		if ( inWaitFor ) {
			//  TODO  May want to issue an interrupt to exit the waitFor() immediately
		}
		try {
			if ( process != null ) {
				log.info( "killing child import process" );
				process.destroy();
			}
		} catch ( NullPointerException e ) {
			//  Eat the NullPointerException since that meant that nothing had to be done.
		} catch ( Throwable t ) {
			String msg = "Exception thrown killing child import process";
			log.error( msg );
		}
		this.awaken();
	}
}
