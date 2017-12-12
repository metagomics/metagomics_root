package org.uwpr.metagomics.run_upload.program;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;

import org.uwpr.metagomics.run_upload.database.BlastResultSearcher;
import org.uwpr.metagomics.run_upload.database.FastaSearcher;
import org.uwpr.metagomics.run_upload.database.PeptideDAO;
import org.uwpr.metagomics.run_upload.database.ProteinSearcher;
import org.uwpr.metagomics.run_upload.database.RunDAO;
import org.uwpr.metagomics.run_upload.database.UploadedFastaFileDAO;
import org.uwpr.metagomics.run_upload.dto.PeptideDTO;
import org.uwpr.metagomics.run_upload.dto.ProteinSequenceDTO;
import org.uwpr.metagomics.run_upload.dto.RunDTO;
import org.uwpr.metagomics.run_upload.dto.UploadedFastaFileDTO;

public class MSUploadParserRunner {

	public static MSUploadParserRunner getInstance() { return new MSUploadParserRunner(); }
	
	/**
	 * Run the initial parsing of an uploaded text file of peptide sequences
	 * and quantitation data from a MS/MS run.
	 * 
	 * @param runId
	 * @param jobcenterRequestId
	 * @throws Exception
	 */
	public void RunMSUploadParser( int runId, int jobcenterRequestId ) throws Exception {
		
		Collection<ProteinSequenceDTO> proteinsToBlast = new HashSet<>();
		
		File runDataFile = new File( Constants.UPLOAD_RUN_TEMP_DIRECTORY, runId + ".txt" );
		
		if( !runDataFile.exists() ) {
			throw new Exception( "Can not find run data file: " + runDataFile.getAbsolutePath() );
		}
		
		// read through peptides, collect them into data structure
		Collection<PeptideDTO> peptideDTOs = new HashSet<>();
		BufferedReader br = null;
		try {
			
			br = new BufferedReader( new FileReader( runDataFile ) );
			
			String line = br.readLine();
			
			while( line != null ) {
				
				String[] fields = line.split( "\\t" );
				
				String sequence = fields[ 0 ];
				
				int peptideId = PeptideDAO.getInstance().getIdForPeptideSequence( sequence );
				
				PeptideDTO peptide = new PeptideDTO();
				peptide.setId( peptideId );
				peptide.setSequence( sequence );
				
				peptideDTOs.add( peptide );
				
				line = br.readLine();
			}			
			
		} finally {
			if( br != null ) {
				try { br.close(); }
				catch( Throwable t ) { ; }
			}
		}
		
		// find all peptides for these proteins from the FASTA file
		// and among those, find which don't already have blast results
		// and add those to the collection of proteins to blast
		proteinsToBlast.addAll( getProteinsToBlast( ProteinSearcher.getInstance().getProteinsForPeptides( peptideDTOs, runId ), runId ) );

		
		if( proteinsToBlast.size() > 0 ) {

			// write out proteins that need blasting to a FASTA file
			File blastFile = new File( Constants.UPLOAD_BLAST_FASTA_TEMP_DIRECTORY, runId + ".fasta" );
			FileWriter fw = null;
			
			try {
	
				fw = new FileWriter( blastFile );
				
				for( ProteinSequenceDTO protein : proteinsToBlast ) {
					fw.write( ">" + protein.getProteinSequenceId() + "\n" );
					fw.write( protein.getSequence() + "\n" );
				}			
				
			} finally {
				if( fw != null ) {
					try { fw.close(); }
					catch( Throwable t ) { ; }
				}
			}
			
			String blast_database = "";
			{
				RunDTO run = RunDAO.getInstance().getRun( runId );
				UploadedFastaFileDTO uploadedFasta = UploadedFastaFileDAO.getInstance().getUploadedFastaFile( run.getFastaUploadId() );
				blast_database = Constants.BLAST_DATABASE_BLAST_NAMES.get( uploadedFasta.getAnnotationDatabaseId() );
			}
			
			// submit jobcenter job to blast that file
			JobCenterUtils.getInstance().submitBlastRequest(runId, blast_database, jobcenterRequestId);
		}
		
		else {
			
			// no proteins needed blasting. great. skip to the end.
			
			JobCenterUtils.getInstance().submitFinalRunProcessing( runId, jobcenterRequestId );			
			
		}
	}
	
	/**
	 * Given the collection of proteins, return the subset of those that have no blast results yet for the given run's fasta upload id
	 * 
	 * @param allProteins
	 * @param runId
	 * @return
	 * @throws Exception
	 */
	private Collection<ProteinSequenceDTO> getProteinsToBlast( Collection<ProteinSequenceDTO> allProteins, int runId ) throws Exception {

		Collection<ProteinSequenceDTO> proteinsToBlast = new ArrayList<>();
		int fastaUploadId = FastaSearcher.getInstance().getFastaUploadIdForRun( runId );

		
		for( ProteinSequenceDTO protein : allProteins ) {
			
			if( !BlastResultSearcher.getInstance().proteinHasBlastResults( protein, fastaUploadId) ) {
				proteinsToBlast.add( protein );
			}
			
		}
		
		return proteinsToBlast;
	}
	
	
}
