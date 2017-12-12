package org.uwpr.metagomics.taxonomy;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

import org.uwpr.metagomics.database.DBConnectionManager;

public class UniprotTaxonomySearcher {

	private static UniprotTaxonomySearcher _INSTANCE = new UniprotTaxonomySearcher();	
	private UniprotTaxonomySearcher() { }
	public static UniprotTaxonomySearcher getInstance() { return _INSTANCE; }
	
	// cache the lookups
	private Map<String, Integer> uniprotTaxonomyMap = new HashMap<>();
	
	
	/**
	 * Get the taxonomy id for the uniprot id. If not found, returns null.
	 * 
	 * @param uniprotId
	 * @return
	 * @throws Exception
	 */
	public Integer getNCBITaxonomyForUniprotId( String uniprotId ) throws Exception {
		
		if( uniprotTaxonomyMap.containsKey( uniprotId ) )
			return uniprotTaxonomyMap.get( uniprotId );
		
		Integer taxId = null;
		
		Connection conn = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		
		String sql = "SELECT ncbi_taxon_id FROM uniprot_taxa WHERE uniprot_acc = ?";
				
		try {

			conn = DBConnectionManager.getInstance().getConnection( DBConnectionManager.UNIPROT_TAXON_LOOKUP );
			
			pstmt = conn.prepareStatement( sql );
			pstmt.setString( 1, uniprotId );
			
			rs = pstmt.executeQuery();
			
			if( rs.next() ) {
				taxId = rs.getInt( 1 );
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
		
		return taxId;
	}
	
	
	
	public Map<String,Integer> getNCBITaxonomiesForUniprotIds( Collection<String> uniprotIds ) throws Exception {
		
		Map<String,Integer> returnMap = new HashMap<>();
		
		Collection<String> uniprotIdsToSearch = new HashSet<>();
		
		// see if any of these are already cached, if so use those
		for( String uniprotId : uniprotIds ) {
		
			if( uniprotTaxonomyMap.containsKey( uniprotId ) )
				returnMap.put( uniprotId, uniprotTaxonomyMap.get( uniprotId ) );
		
			else
				uniprotIdsToSearch.add( uniprotId );

		}
		
		Connection conn = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		
		String sql = "SELECT ncbi_taxon_id FROM uniprot_taxa WHERE uniprot_acc = ?";
				
		try {

			conn = DBConnectionManager.getInstance().getConnection( DBConnectionManager.UNIPROT_TAXON_LOOKUP );
			
			pstmt = conn.prepareStatement( sql );
			
			
			for( String uniprotId : uniprotIdsToSearch ) {
			
				pstmt.setString( 1, uniprotId );
				
				rs = pstmt.executeQuery();
				
				if( rs.next() ) {
					int taxonId = rs.getInt( 1 );
					
					uniprotTaxonomyMap.put( uniprotId, taxonId );
					returnMap.put( uniprotId, taxonId );
				}
				
				rs.close(); rs = null;				
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
		
		return returnMap;
	}
	
}
