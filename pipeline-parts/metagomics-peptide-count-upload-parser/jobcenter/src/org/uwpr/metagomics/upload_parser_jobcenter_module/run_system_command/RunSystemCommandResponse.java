package org.uwpr.metagomics.upload_parser_jobcenter_module.run_system_command;

/**
 * commandExitCode, stdOut, and stdErr from running a system command
 * 
 * commandSuccessful = true if commandExitCode == zero
 */
public class RunSystemCommandResponse {

	private boolean commandSuccessful;
	
	private int commandExitCode;

	private boolean shutdownRequested;
	
	private String stdOut;
	private String stdErr;
	


	public String getStdOut() {
		return stdOut;
	}
	public void setStdOut(String stdOut) {
		this.stdOut = stdOut;
	}
	public String getStdErr() {
		return stdErr;
	}
	public void setStdErr(String stdErr) {
		this.stdErr = stdErr;
	}
	public int getCommandExitCode() {
		return commandExitCode;
	}
	public void setCommandExitCode(int commandExitCode) {
		this.commandExitCode = commandExitCode;
	}
	public boolean isCommandSuccessful() {
		return commandSuccessful;
	}
	public void setCommandSuccessful(boolean commandSuccessful) {
		this.commandSuccessful = commandSuccessful;
	}
	public boolean isShutdownRequested() {
		return shutdownRequested;
	}
	public void setShutdownRequested(boolean shutdownRequested) {
		this.shutdownRequested = shutdownRequested;
	}
}
