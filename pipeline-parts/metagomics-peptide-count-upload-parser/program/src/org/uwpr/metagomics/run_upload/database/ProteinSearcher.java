package org.uwpr.metagomics.run_upload.database;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

import org.uwpr.metagomics.database.DBConnectionManager;
import org.uwpr.metagomics.run_upload.dto.PeptideDTO;
import org.uwpr.metagomics.run_upload.dto.ProteinSequenceDTO;
import org.uwpr.metagomics.run_upload.program.Constants;

public class ProteinSearcher {

	public static ProteinSearcher getInstance() { return new ProteinSearcher(); }
	
	/**
	 * Get all proteins matched by the given peptide for the given run
	 * 
	 * @param peptide
	 * @param runId
	 * @return
	 * @throws Exception
	 */
	public Collection<ProteinSequenceDTO> getProteinsForPeptides( Collection<PeptideDTO> peptides, int runId ) throws Exception {
		
		Collection<ProteinSequenceDTO> proteinsToReturn = new HashSet<>();
		Collection<PeptideDTO> peptidesToSearch = new HashSet<>();
		
		int fastaFileId = FastaSearcher.getInstance().getFastaFileIdForRun( runId );
		
		
		for( PeptideDTO peptide : peptides ) {

			Collection<ProteinSequenceDTO> peptideProteins = getProteinsForPeptideFromPreviousSearch( peptide, fastaFileId );
			
			if( peptideProteins != null && peptideProteins.size() > 0 ) {
				proteinsToReturn.addAll( peptideProteins);	// we previously found proteins for this peptide for this fasta file, us them
			} else {
				peptidesToSearch.add( peptide );	// we need to find proteins for this peptide for this fasta file
			}
			
		}

		
		// find all protein matches for these peptides from the FASTA data
		Map<PeptideDTO, Collection<ProteinSequenceDTO>> proteinsForPeptides = getProteinsForPeptidesFromFastaProteinSequences( peptidesToSearch, fastaFileId );

		// save these matches to the database
		for( PeptideDTO peptide : proteinsForPeptides.keySet() ) {
			for( ProteinSequenceDTO protein : proteinsForPeptides.get( peptide ) ) {

				// save this peptide protein match to the database
				savePeptideProteinMatchForFasta( peptide, protein, fastaFileId );
				
				// add this to returned collections of proteins to run blast on
				proteinsToReturn.add( protein );
			}
		}
		
		return proteinsToReturn;
		
	}
	
	/**
	 * Save the peptide-protein match for the given fasta file.
	 * 
	 * @param peptide
	 * @param protein
	 * @param fastaFileId
	 * @throws Exception
	 */
	public void savePeptideProteinMatchForFasta( PeptideDTO peptide, ProteinSequenceDTO protein, int fastaFileId ) throws Exception {
		
		Connection conn = null;
		PreparedStatement pstmt = null;
		
		String sql = "INSERT INTO peptide_protein_fasta_map (fasta_file_id, protein_sequence_id, peptide_id) VALUES (?,?,?)";
				
		try {

			conn = DBConnectionManager.getInstance().getConnection( DBConnectionManager.DB );
			
			pstmt = conn.prepareStatement( sql );
			pstmt.setInt( 1, fastaFileId );
			pstmt.setInt( 2, protein.getProteinSequenceId() );
			pstmt.setInt( 3, peptide.getId() );
			
			pstmt.executeUpdate();

		} finally {
			
			if( pstmt != null ) {
				try { pstmt.close(); } catch( Throwable t ) { ; }
				pstmt = null;
			}
			
			if( conn != null ) {
				try { conn.close(); } catch( Throwable t ) { ; }
				conn = null;
			}
		}
		
	}
	
	/**
	 * Get all proteins associated with the given peptide in the given fasta file.
	 * 
	 * @param peptideId
	 * @param fastaFileId
	 * @return
	 * @throws Exception
	 */
	public Map<PeptideDTO, Collection<ProteinSequenceDTO>> getProteinsForPeptidesFromFastaProteinSequences( Collection<PeptideDTO> peptides, int fastaFileId ) throws Exception {
		
		Map<PeptideDTO, Collection<ProteinSequenceDTO>> peptideProteinDTOMap = new HashMap<>();
		Map<PeptideDTO, Collection<Integer>> peptideProteinIdMap = new HashMap<>();
		
		Map<Integer, String> proteinSequenceBatch = loadProteinSequencesFromDatabase( 0, fastaFileId );
		int iterations = 1;
		while( proteinSequenceBatch != null && proteinSequenceBatch.size() > 0 ) {

			for( int proteinId : proteinSequenceBatch.keySet() ) {
				
				String proteinSequence = proteinSequenceBatch.get( proteinId );
				
				// handle leucine/iso-leucine substitution
				proteinSequence = proteinSequence.replaceAll( "[IL]", "J" );
				
				
				for( PeptideDTO peptide : peptides ) {
					
					String peptideSequence = peptide.getSequence();
					
					// do some basic sanitation
					peptideSequence = peptideSequence.replaceAll( ";", "" );
					peptideSequence = peptideSequence.replaceAll( "\\\\", "" );
					
					// handle leucine/iso-leucine substitution
					peptideSequence = peptideSequence.replaceAll( "[IL]", "J" );
					
					// if protein contains a tryptic site for this peptide or the peptide is n-terminal in the protein
					if( proteinSequence.contains( "K" + peptideSequence ) ||
						proteinSequence.contains( "R" + peptideSequence ) ||
						proteinSequence.startsWith( peptideSequence ) ) {

						if( !peptideProteinIdMap.containsKey( peptide ) )
							peptideProteinIdMap.put( peptide, new HashSet<>() );
						
						peptideProteinIdMap.get( peptide ).add( proteinId );
					}					
				}
			}
						
			proteinSequenceBatch = loadProteinSequencesFromDatabase( iterations * Constants.NUMBER_PROTEIN_SEQUENCES_PER_ITERATION, fastaFileId );
			iterations++;
		}
		
		/*
		 * At this point, peptideProteinIdMap should contain peptide=>protein ids for the entire fasta file.
		 * convert these to proteinDTOs and return them
		 */
		for( PeptideDTO peptide : peptideProteinIdMap.keySet() ) {
			peptideProteinDTOMap.put( peptide, new HashSet<>() );
			
			for( int proteinId : peptideProteinIdMap.get( peptide ) ) {
				peptideProteinDTOMap.get( peptide ).add( ProteinSequenceDAO.getInstance().getProteinSequenceDTO( proteinId ) );
			}
			
		}
		
		return peptideProteinDTOMap;
	}
	
	/**
	 * Get a batch of protein sequences from the database for searching.
	 * 
	 * @param offset
	 * @param fastaFileId
	 * @return
	 * @throws Exception
	 */
	private Map<Integer, String> loadProteinSequencesFromDatabase( int offset, int fastaFileId ) throws Exception {
		
		Map<Integer, String> proteinMap = new HashMap<>();
		
		Connection conn = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		
		String sql = "SELECT ps.id, ps.sequence FROM protein_sequence AS ps INNER JOIN fasta_file_protein_sequence AS ffps ";
		sql += "ON ps.id = ffps.protein_sequence_id WHERE ffps.fasta_file_id = ? ORDER BY ffps.id ";
		sql += "LIMIT " + Constants.NUMBER_PROTEIN_SEQUENCES_PER_ITERATION + " OFFSET " + offset;
		
		try {

			conn = DBConnectionManager.getInstance().getConnection( DBConnectionManager.DB );
			
			pstmt = conn.prepareStatement( sql );
			pstmt.setInt( 1,  fastaFileId );
			rs = pstmt.executeQuery();

			
			while( rs.next() ) {
				proteinMap.put( rs.getInt( 1 ), rs.getString( 2 ) );
			}

		} finally {
			
			// be sure database handles are closed
			if( rs != null ) {
				try { rs.close(); } catch( Throwable t ) { ; }
				rs = null;
			}
			
			if( pstmt != null ) {
				try { pstmt.close(); } catch( Throwable t ) { ; }
				pstmt = null;
			}
			
			if( conn != null ) {
				try { conn.close(); } catch( Throwable t ) { ; }
				conn = null;
			}
		}
		
		return proteinMap;
	}
	
	
	
	/**
	 * Get all proteins associated with the given peptide in the given fasta file.
	 * 
	 * @param peptideId
	 * @param fastaFileId
	 * @return
	 * @throws Exception
	 */
	public Collection<ProteinSequenceDTO> getProteinsForPeptideFromPreviousSearch( PeptideDTO peptide, int fastaFileId ) throws Exception {
		
		Collection<ProteinSequenceDTO> proteins = new ArrayList<>();
		
		Connection conn = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		
		String sql = "SELECT protein_sequence_id FROM peptide_protein_fasta_map WHERE fasta_file_id = ? AND peptide_id = ?";
				
		try {

			conn = DBConnectionManager.getInstance().getConnection( DBConnectionManager.DB );
			
			pstmt = conn.prepareStatement( sql );
			pstmt.setInt( 1, fastaFileId );
			pstmt.setInt( 2, peptide.getId() );
			
			rs = pstmt.executeQuery();
			
			while( rs.next() ) {

				int proteinSequenceId = rs.getInt( 1 );
				
				proteins.add( ProteinSequenceDAO.getInstance().getProteinSequenceDTO( proteinSequenceId ) );
			
			}

		} finally {
			
			// be sure database handles are closed
			if( rs != null ) {
				try { rs.close(); } catch( Throwable t ) { ; }
				rs = null;
			}
			
			if( pstmt != null ) {
				try { pstmt.close(); } catch( Throwable t ) { ; }
				pstmt = null;
			}
			
			if( conn != null ) {
				try { conn.close(); } catch( Throwable t ) { ; }
				conn = null;
			}
		}
		
		return proteins;		
	}
	
	
}
