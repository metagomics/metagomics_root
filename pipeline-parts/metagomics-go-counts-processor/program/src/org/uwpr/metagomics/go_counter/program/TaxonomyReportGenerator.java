package org.uwpr.metagomics.go_counter.program;

import java.io.File;
import java.io.FileWriter;
import java.sql.Connection;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import org.uwpr.metagomics.dto.RunDTO;
import org.uwpr.metagomics.dto.UploadedFastaFileDTO;
import org.uwpr.metagomics.database.DBConnectionManager;
import org.uwpr.metagomics.go_counter.database.RunDAO;
import org.uwpr.metagomics.go_counter.database.UploadedFastaFileDAO;
import org.uwpr.metagomics.taxonomy.CommonTaxonomicUnitSearcher;
import org.uwpr.metaproteomics.emma.go.GONode;
import org.yeastrc.ncbi.taxonomy.object.NCBITaxonomyNode;
import org.yeastrc.ncbi.taxonomy.utils.NCBITaxonomyUtils;

public class TaxonomyReportGenerator {

	public static TaxonomyReportGenerator getInstance() { return new TaxonomyReportGenerator(); }
	
	public void generateTaxonomyReport( int runId,
										int totalSpectra,
										Map<Integer, Integer> peptideCounts,
										Map<GONode, Integer> GO_NODE_COUNT_MAP,
										Map<GONode, Collection<Integer>> GO_NODE_PEPTIDE__ID_MAP ) throws Exception  {
		
		/*
		 * For each GO term, iterate over all the peptides that contributed to that GO term, find the
		 * common taxonomic unit for all proteins matching to that peptide, and increment the spectral
		 * counts for those taxa by the spectral count for the peptide.
		 */
		Map<GONode, Map<NCBITaxonomyNode, Integer>> GO_TAXON_COUNTS = new HashMap<>();
				
		for( GONode node : GO_NODE_COUNT_MAP.keySet() ) {
						
			Map<NCBITaxonomyNode, Integer> taxonomyCountsForThisGOTerm = new HashMap<>();
			
			for( int peptideId : GO_NODE_PEPTIDE__ID_MAP.get( node ) ) {
				
				// get the common taxonomic unit + parents for this peptide
				Collection<NCBITaxonomyNode> taxa = CommonTaxonomicUnitSearcher.getInstance().getCommonTaxonomicUnitAndAncestorsForPeptide( peptideId, runId );
				
				if( taxa == null ) continue;	// probably the result of being unable to find any uniprot ids for the proteins matched by this peptide.
				
				for( NCBITaxonomyNode taxonNode : taxa ) {
					
					int count = peptideCounts.get( peptideId );
					
					if( taxonomyCountsForThisGOTerm.containsKey( taxonNode ) ) {
						count += taxonomyCountsForThisGOTerm.get( taxonNode );
					}
										
					taxonomyCountsForThisGOTerm.put( taxonNode, count );
				}
			}
			
			GO_TAXON_COUNTS.put( node, taxonomyCountsForThisGOTerm );			
		}

		
		/*
		 * Can now output the report using the data gathered above
		 */
		RunDTO run = RunDAO.getInstance().getRun( runId );
		
		UploadedFastaFileDTO upload = UploadedFastaFileDAO.getInstance().getUploadedFastaFile( run.getFastaUploadId() );
		String uid = upload.getUniqueId();

		File reportDirectory = new File( Constants.DATA_DOWNLOAD_DIRECTORY, uid );
		if( !reportDirectory.exists() ) {
			reportDirectory.mkdir();
		}
		
		// connection we're going to use to perform taxonomy lookups
		Connection conn = null;
		FileWriter fw = null;

		try {
		
			conn = DBConnectionManager.getInstance().getConnection( DBConnectionManager.TAXONOMY_DB );
			
			File reportFile = new File( reportDirectory, "taxonomy_report_" + run.getId() + ".txt" );
				
			fw = new FileWriter( reportFile );
				
			fw.write( "GO acc\tGO aspect\tGO name\ttaxon name\tNCBI taxonomy id\ttaxonomy rank\tPSM count\tratio of GO\tratio of run\n" );
				
				
			for( GONode node : GO_TAXON_COUNTS.keySet() ) {
				int go_count = GO_NODE_COUNT_MAP.get( node );
					
					
				for( NCBITaxonomyNode taxonNode : GO_TAXON_COUNTS.get( node ).keySet() ) {
						
					fw.write( node.getAcc() + "\t" );
					fw.write( node.getTermType() + "\t" );
					fw.write( node.getName() + "\t" );
						
					fw.write( NCBITaxonomyUtils.getScientificNameForNCBITaxonomyId( taxonNode.getId(), conn ) + "\t" );
					fw.write( taxonNode.getId() + "\t" );
					fw.write( taxonNode.getRank() + "\t" );
					
					int go_taxon_count = GO_TAXON_COUNTS.get( node ).get( taxonNode );
					
					fw.write( go_taxon_count + "\t" );
					fw.write( ( (double)go_taxon_count / (double)go_count ) + "\t" );
					fw.write( ( (double)go_taxon_count / (double)totalSpectra ) + "\n" );						

				}
					
			}
							
		} finally {
			try { conn.close(); }
			catch( Exception e ) { ; }
			
			if( fw != null ) {
				try { fw.close(); fw = null; }
				catch( Exception e ) { ; }
			}
		}
		
		
		
	}
}
