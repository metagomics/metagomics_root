package org.uwpr.metagomics.blast_parser.program;

public class MainProgram {

	public static void main( String[] args ) throws Exception {
		
		int runId = Integer.parseInt( args[ 0 ] );
		int requestId = Integer.parseInt( args[ 1 ] );
		
		BlastParserRunner.getInstance().runBlastParser( runId, requestId );
		
	}
	
}
