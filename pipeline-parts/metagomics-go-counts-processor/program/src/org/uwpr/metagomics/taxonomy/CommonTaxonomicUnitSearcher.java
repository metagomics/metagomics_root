package org.uwpr.metagomics.taxonomy;

import java.sql.Connection;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

import org.uwpr.metagomics.database.DBConnectionManager;
import org.uwpr.metagomics.go_counter.database.BlastHitSearcher;
import org.uwpr.metagomics.go_counter.database.FastaSearcher;
import org.uwpr.metagomics.go_counter.database.ProteinSearcher;
import org.yeastrc.ncbi.taxonomy.db.NCBITaxonomyNodeFactory;
import org.yeastrc.ncbi.taxonomy.object.NCBITaxonomyNode;
import org.yeastrc.ncbi.taxonomy.utils.NCBITaxonomyUtils;

public class CommonTaxonomicUnitSearcher {

	private static final CommonTaxonomicUnitSearcher _INSTANCE = new CommonTaxonomicUnitSearcher();
	private CommonTaxonomicUnitSearcher() { }
	public static CommonTaxonomicUnitSearcher getInstance() { return _INSTANCE; }
	
	/**
	 * Map in the form of: query node id => parent id
	 */
	private Map<Integer, Integer> DIRECT_PARENT_MAP = new HashMap<>();

	/**
	 * Get the NCBI taxonomy term that is the direct parent term of the supplied term.
	 * 
	 * @param taxonomyId
	 * @return
	 * @throws Exception
	 */
	private Integer getDirectParentOfTaxonomyId( int taxonomyId ) throws Exception {
		
		if( DIRECT_PARENT_MAP.containsKey( taxonomyId ) )
				return DIRECT_PARENT_MAP.get( taxonomyId );
		
		
		Connection conn = null;
		
		try {
			
			conn = DBConnectionManager.getInstance().getConnection( DBConnectionManager.TAXONOMY_DB );
			
			int parentId = NCBITaxonomyUtils.getDirectParentOfNCBITaxonomyIdExcludeNoRank( taxonomyId, conn );

			DIRECT_PARENT_MAP.put( taxonomyId, parentId );
			
			return parentId;			
			
		} finally {
			try { conn.close(); }
			catch( Exception e ) { ; }
		}
	}
	
	/**
	 * Cache of taxonomy nodes as a map: NCBI taxonomy ID => NCBITaxonomyNode
	 */
	private Map<Integer, NCBITaxonomyNode> TAXONOMY_NODE_CACHE = new HashMap<>();
	

	/**
	 * Get the NCBITaxonomyNode for the given taxonomy id
	 * @param taxonomyId
	 * @return
	 * @throws Exception
	 */
	private NCBITaxonomyNode getNCBITaxonomyNode( int taxonomyId ) throws Exception {
		
		if( TAXONOMY_NODE_CACHE.containsKey( taxonomyId ) )
			return TAXONOMY_NODE_CACHE.get( taxonomyId );
		
		Connection conn = null;
		
		try {
			
			conn = DBConnectionManager.getInstance().getConnection( DBConnectionManager.TAXONOMY_DB );
			
			NCBITaxonomyNode node = NCBITaxonomyNodeFactory.getNCBITaxonomyNode( taxonomyId, conn );				

			TAXONOMY_NODE_CACHE.put( taxonomyId, node );
			
			return node;			
			
		} finally {
			try { conn.close(); }
			catch( Exception e ) { ; }
		}		
	}
	
	/*
	 * Cache of ncbi taxonomy id => collection of all paren ids
	 */
	private Map<Integer, Collection<Integer>> ALL_PARENTS_CACHE = new HashMap<>();

	private Collection<Integer> getAllParentsOfTaxonomyId( int taxonomyId ) throws Exception {
		
		if( ALL_PARENTS_CACHE.containsKey( taxonomyId ) )
			return ALL_PARENTS_CACHE.get( taxonomyId );
		
		Connection conn = null;
		
		try {
			
			conn = DBConnectionManager.getInstance().getConnection( DBConnectionManager.TAXONOMY_DB );
			
			Collection<Integer> parentIds = NCBITaxonomyUtils.getAllAncestorsOfNCBITaxonomyId( taxonomyId, conn );

			ALL_PARENTS_CACHE.put( taxonomyId, parentIds );
			
			return parentIds;			
			
		} finally {
			try { conn.close(); }
			catch( Exception e ) { ; }
		}	
		
		
	}
	
	
	/**
	 * Given two taxa, find the taxonomic unit that is the most specif common ancestor of those two terms.
	 * For example, for two different species with the same class, it will return the class.
	 * 
	 * @param node1
	 * @param node2
	 * @return
	 * @throws Exception
	 */
	private NCBITaxonomyNode getCommonTaxonomicUnit( NCBITaxonomyNode node1, NCBITaxonomyNode node2 ) throws Exception {
			
		// build a collection containing node1's id and all of its parents ids
		Collection<Integer> parents1 = new HashSet<>();
		parents1.add( node1.getId() );
			
		int parentId = getDirectParentOfTaxonomyId( node1.getId() );
		parents1.add( parentId );
			
		while( parentId != 1 ) {
			NCBITaxonomyNode pNode = getNCBITaxonomyNode( parentId );				
			parentId = getDirectParentOfTaxonomyId( pNode.getId() );
				
			parents1.add( parentId );
		}
			
			
		// iterate over node2's id and its parents ids until we find one that is in collection of node1's ids--then return it
		NCBITaxonomyNode testNode = node2;
			
		while( !parents1.contains( testNode.getId() ) ) {
				
			int testNodeParentId = getDirectParentOfTaxonomyId( testNode.getId() );
				
			// if this happens just return it--failsafe in case somehow node1s parents didn't contain the root node (1)...
			if( testNodeParentId == 1 )
				return getNCBITaxonomyNode( 1 );
				
				testNode = getNCBITaxonomyNode( testNodeParentId );				
		}

		return testNode;
	}
	
	
	/**
	 * Given a collection of metagomics protein ids, return a map of protein id => { uniprot accession strings } found for that
	 * protein id from a blast search.
	 * 
	 * @param proteinIds
	 * @param fastaUploadId
	 * @return
	 * @throws Exception
	 */
	private Map<Integer, Collection<String>> getUniprotAccsForProteinIds( Collection<Integer> proteinIds, int fastaUploadId ) throws Exception {
		
		Map<Integer, Collection<String>> uniprotAccsForProteinIds = new HashMap<>();
		
		for( int proteinId : proteinIds ) {
			Collection<String> uniprotAccs = BlastHitSearcher.getInstance().getBlastHitsForProteinInFastaUpload( proteinId, fastaUploadId );
			uniprotAccsForProteinIds.put( proteinId, uniprotAccs );
		}
		
		return uniprotAccsForProteinIds;
		
	}
	
	/**
	 * Given a map of protein ids to collection of uniprot accs, return a map of uniprot acc => ncbi taxonomy id
	 * for all uniprot accs.
	 * 
	 * @param allUniprotAccsForAllProteinIds
	 * @return
	 * @throws Exception
	 */
	private Map<String,Integer> getAllTaxaForAllUniprotAccs( Map<Integer, Collection<String>> allUniprotAccsForAllProteinIds ) throws Exception {
		
		Collection<String> allUniprotAccs = new HashSet<>();

		for( int proteinId : allUniprotAccsForAllProteinIds.keySet() ) {
			allUniprotAccs.addAll( allUniprotAccsForAllProteinIds.get( proteinId ) );
		}
		
		return UniprotTaxonomySearcher.getInstance().getNCBITaxonomiesForUniprotIds( allUniprotAccs );
	}
	
	
	/**
	 * For a given peptide in a given run (a run being a specific FASTA file and blast parameters), find and return
	 * the common taxonomic unit and all of its parent terms.
	 * 
	 * @param peptideId
	 * @param runId
	 * @return
	 * @throws Exception
	 */
	public Collection<NCBITaxonomyNode> getCommonTaxonomicUnitAndAncestorsForPeptide( int peptideId, int runId ) throws Exception {
		
		int fastaFileId = FastaSearcher.getInstance().getFastaFileIdForRun( runId );
		int fastaUploadId = FastaSearcher.getInstance().getFastaUploadIdForRun( runId );
		
		Collection<NCBITaxonomyNode> retNodes = new HashSet<>();

		
		// proteins matched by this peptide
		Collection<Integer> proteinIds = ProteinSearcher.getInstance().getProteinsForPeptide( peptideId, fastaFileId );

		if( proteinIds == null || proteinIds.size() < 1 )
			return null;
		
		
		// This is the common taxonomic unit found so far.
		NCBITaxonomyNode testNode = null;
		
		// all uniprot accs found for all proteins found for this peptide
		Map<Integer, Collection<String>> allUniprotAccsForAllProteinIds = getUniprotAccsForProteinIds( proteinIds, fastaUploadId );

		// taxonomies found for all uniprotAccs found for any protein for this peptide
		Map<String,Integer> allTaxaForAllUniprotAccs = getAllTaxaForAllUniprotAccs( allUniprotAccsForAllProteinIds );
		
		/*
		 * Iterate over all proteins from the FASTA file matched by this peptide.
		 */
		for( int proteinId : allUniprotAccsForAllProteinIds.keySet() ) {
			Collection<String> uniprotAccs = allUniprotAccsForAllProteinIds.get( proteinId );
	
			if( uniprotAccs == null ) continue;
			
			/*
			 * Iterate over all the uniprot proteins matched by this FASTA protein
			 */
			for( String uniprotAcc : uniprotAccs ) {
				
				Integer thisNodeId = null;
				if( allTaxaForAllUniprotAccs.containsKey( uniprotAcc ) ) {
					thisNodeId = allTaxaForAllUniprotAccs.get( uniprotAcc );
				}
				
				if( thisNodeId == null ) continue;
					
				NCBITaxonomyNode thisNode = getNCBITaxonomyNode( thisNodeId );
				if( thisNode == null ) continue;

				// this is the first node we've found, set this to our test node and go to the next uniprot protein
				if( testNode == null ) {
					testNode = thisNode;
					continue;
				}
					
					
				NCBITaxonomyNode commonNode = this.getCommonTaxonomicUnit( testNode, thisNode );
				if( commonNode == null )
					throw new Exception( "Could not find common node between: " + testNode.getId() + " and " + thisNode.getId() );
					
					
				testNode = commonNode;

				// if the common taxonomic term so far is the root node, we can just stop--it'll always be the root node
				if( testNode.getId() == 1 ) {
						break;
				}

			}
				
		}
			
		// at this point testNode should be the common taxonomic unit--the most specific taxonomic term in common for all proteins
		// matched by this peptide.
			
		if( testNode == null )
				return null;
			
		retNodes.add( testNode );
			
		if( testNode.getId() != 1 ) {

			for( int ancestorNodeId : getAllParentsOfTaxonomyId( testNode.getId() ) ) {
				retNodes.add( getNCBITaxonomyNode( ancestorNodeId ) );
			}
				
		}
			

		
		
		return retNodes;
	}
	
}
