package org.uwpr.metagomics.fasta_importer.program;

public class MainProgram {

	/**
	 * Run the importer
	 * 
	 * @param args
	 * @throws Exception
	 */
	public static void main( String[] args ) throws Exception {
		
		FastaParserRunner.getInstance().runFastaImportPipeline( Integer.parseInt( args[ 0 ] ) ); 
		
		
	}
	
}
