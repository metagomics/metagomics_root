package org.uwpr.metagomics.blast_parser.program;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

public class BlastParserRunner {

	public static BlastParserRunner getInstance() { return new BlastParserRunner(); }
	
	/**
	 * Run the blast results parsing code
	 * 
	 * @param runId
	 * @param jobcenterRequestId
	 * @throws Exception
	 */
	public void runBlastParser( int runId, int jobcenterRequestId ) throws Exception {
		
		File blastFile = new File( Constants.BLAST_RESULTS_FOLDER, runId + ".txt" );
		
		if( !blastFile.exists() ) {
			throw new Exception( "Blast results file for run " + runId + " can not be found." );
		}
		
		Integer fastaFileUploadId = DatabaseUtils.getInstance().getFastaUploadIdForRun( runId );
		Double blastCutoff = DatabaseUtils.getInstance().getBlastCutoffForFastaUpload( fastaFileUploadId );
		
		if( fastaFileUploadId == null ) {
			throw new Exception ( "Could not find fasta file upload if for run: " + runId );
		}
		
		BufferedReader br = null;

		
		if( DatabaseUtils.getInstance().getIsOnlyTopHit( fastaFileUploadId ) ) {
			
			// we're only keeping the top hit. we'll keep all uniprot matches that are tied w/ the top score
			
			Map< Integer, Collection< String> > proteinBlastMatches = new HashMap<>();
			Map< Integer, Double > bestScoresForProteins = new HashMap<>();
			
			try {
				
				br = new BufferedReader( new FileReader( blastFile ) );
				
				String line = br.readLine();
				
				while( line != null ) {
					
					String[] fields = line.split( "\\t" );
					
					int proteinSequenceId = Integer.parseInt( fields[ 0 ] );
					Double evalue = Double.parseDouble( fields[ 2 ] );
					
					if( evalue <= blastCutoff ) {	
						
						String[] tf = fields[ 1 ].split( "\\|" );
						
						if( tf.length < 2 ) {
							throw new Exception( "Error processing uniprot acc for acc: " + fields[ 1 ] + " for run: " + runId );
						}
						
						String uniprotAcc = tf[ 1 ];

						if( !bestScoresForProteins.containsKey( proteinSequenceId ) ) {
							proteinBlastMatches.put( proteinSequenceId, new HashSet<>() );
							proteinBlastMatches.get( proteinSequenceId ).add( uniprotAcc );
							
							bestScoresForProteins.put( proteinSequenceId, evalue );
						} else {
							
							if( evalue < bestScoresForProteins.get( proteinSequenceId ) ) {
								proteinBlastMatches.put( proteinSequenceId, new HashSet<>() );
								proteinBlastMatches.get( proteinSequenceId ).add( uniprotAcc );
								
								bestScoresForProteins.put( proteinSequenceId, evalue );
								
							} else if( evalue.equals( bestScoresForProteins.get( proteinSequenceId ) ) ) {
								
								proteinBlastMatches.get( proteinSequenceId ).add( uniprotAcc );
								
							}
						}						
					}
					
									
					line = br.readLine();
				}
				
				for( int proteinSequenceId : bestScoresForProteins.keySet() ) {
					Double evalue = bestScoresForProteins.get( proteinSequenceId );
					
					for( String uniprotAcc : proteinBlastMatches.get( proteinSequenceId ) ) {
						
						DatabaseUtils.getInstance().saveBlastHit( fastaFileUploadId, proteinSequenceId, uniprotAcc, evalue );
						
					}
					
				}
				
			} finally {
				
				if( br != null ) {
					try { br.close(); }
					catch( Throwable t ) { ; }
				}
			}
			
			
		} else {
		
			// we're meeting all hits that are better than the cutoff evalue, don't need to keep track of what's best
		
			try {
				
				br = new BufferedReader( new FileReader( blastFile ) );
				
				String line = br.readLine();
				
				while( line != null ) {
					
					String[] fields = line.split( "\\t" );
					
					int proteinSequenceId = Integer.parseInt( fields[ 0 ] );
					double evalue = Double.parseDouble( fields[ 2 ] );
					
					if( evalue <= blastCutoff ) {
						
						String[] tf = fields[ 1 ].split( "\\|" );
						
						if( tf.length < 2 ) {
							throw new Exception( "Error processing uniprot acc for acc: " + fields[ 1 ] + " for run: " + runId );
						}
						
						String uniprotAcc = tf[ 1 ];
						
						DatabaseUtils.getInstance().saveBlastHit( fastaFileUploadId, proteinSequenceId, uniprotAcc, evalue );
					}
					
									
					line = br.readLine();
				}
				
				
			} finally {
				
				if( br != null ) {
					try { br.close(); }
					catch( Throwable t ) { ; }
				}
			}
		}
		
		// submit jobcenter job
		JobCenterUtils.getInstance().submitGOCountProcessingRequest( runId, jobcenterRequestId );
		
	}
	
}
