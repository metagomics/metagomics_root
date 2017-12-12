package org.uwpr.metagomics.go_counter.database;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.Collection;
import java.util.HashSet;

import org.uwpr.metagomics.database.DBConnectionManager;
import org.uwpr.metaproteomics.emma.go.GONode;
import org.uwpr.metaproteomics.emma.go.GONodeFactory;
import org.uwpr.metaproteomics.emma.go.GOSearchUtils;
import org.uwpr.metaproteomics.emma.go.GOUtils;

public class GOSearcher {

	public static GOSearcher getInstance() { return new GOSearcher(); }
	
	public Collection<GONode> getAllGONodesForPeptide( int peptideId, int runId ) throws Exception {
		
		Collection<GONode> ALL_GO_NODES = new HashSet<>();
		
		int fastaFileId = FastaSearcher.getInstance().getFastaFileIdForRun( runId );
		int fastaUploadId = FastaSearcher.getInstance().getFastaUploadIdForRun( runId );
		
		
		// iterate over all proteins to which this peptide was matched in this fasta file
		Collection<Integer> proteinIds = ProteinSearcher.getInstance().getProteinsForPeptide( peptideId, fastaFileId );
		for( int proteinId : proteinIds ) {
			
			// iterate over all blast hits to which this protein was matched
			Collection<String> uniprotAccs = BlastHitSearcher.getInstance().getBlastHitsForProteinInFastaUpload( proteinId, fastaUploadId );
			for( String uniprotAcc : uniprotAccs ) {
				
				Collection<GONode> directGoNodes = getDirectGONodesForUniprotAcc( uniprotAcc );
				for( GONode goNode : directGoNodes ) {
					ALL_GO_NODES.add( goNode );
					ALL_GO_NODES.addAll( GOSearchUtils.getAllParentNodes( goNode ) );
				}
				
			}
		}
		
		// ensure we have a GO node for all aspects, if not add the unknown node for that aspect
		boolean found_process = false;
		boolean found_function = false;
		boolean found_component = false;
		
		for( GONode gnode : ALL_GO_NODES ) {
			if( gnode.getTermType().equals( "biological_process" ) )
				found_process = true;
			
			if( gnode.getTermType().equals( "cellular_component" ) )
				found_component = true;
			
			if( gnode.getTermType().equals( "molecular_function" ) )
				found_function = true;
		}
		
		if( !found_process ) {
			ALL_GO_NODES.add( GONodeFactory.NODE_UNKNOWN_BIOLOGICAL_PROCESS );
			ALL_GO_NODES.add( GOUtils.getAspectRootNode( "biological_process" ) );
		}

		if( !found_function ) {
			ALL_GO_NODES.add( GONodeFactory.NODE_UNKNOWN_MOLECULAR_FUNCTION );
			ALL_GO_NODES.add( GOUtils.getAspectRootNode( "molecular_function" ) );
		}
		
		if( !found_component ) {
			ALL_GO_NODES.add( GONodeFactory.NODE_UNKNOWN_CELLULAR_COMPONENT );
			ALL_GO_NODES.add( GOUtils.getAspectRootNode( "cellular_component" ) );
		}
		
		
		return ALL_GO_NODES;
	}
	
	/**
	 * Get all go nodes associated directly w/ this uniprot acc (no ancestors or descendants)
	 * @param uniprotAcc
	 * @return
	 * @throws Exception
	 */
	private Collection<GONode> getDirectGONodesForUniprotAcc( String uniprotAcc ) throws Exception {
		
		Collection<GONode> goNodes = new HashSet<>();
		
		Connection conn = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		
		String sql = "SELECT go_id FROM go_annotation WHERE db_object_id = ? AND qualifier = ''";
				
		try {

			conn = DBConnectionManager.getInstance().getConnection( DBConnectionManager.GO_ANNO_DB );
			
			pstmt = conn.prepareStatement( sql );
			pstmt.setString( 1, uniprotAcc );
			
			rs = pstmt.executeQuery();
			
			while( rs.next() ) {

					
				GONode node = null;
					
				try {
					node = GONodeFactory.getInstance().getGONode( rs.getString( 1 ) );
				} catch (IllegalArgumentException e ) {
					// means node acc wasn't found. probably result of newer go term than the anno database being used
					;
				}
					
				if( node != null ) {
					goNodes.add( node );
				}

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
		
		
		return goNodes;
	}	
	
}
