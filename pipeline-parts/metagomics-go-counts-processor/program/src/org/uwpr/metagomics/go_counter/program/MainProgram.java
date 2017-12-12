package org.uwpr.metagomics.go_counter.program;

public class MainProgram {

	public static void main(String[] args) throws Exception {
		
		int runId = Integer.parseInt( args[ 0 ] );
		
		GOCounterRunner.getInstance().runGOCounter( runId );
		
	}
	
}
