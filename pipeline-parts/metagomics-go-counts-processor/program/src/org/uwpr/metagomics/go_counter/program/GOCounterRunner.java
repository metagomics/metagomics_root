package org.uwpr.metagomics.go_counter.program;

import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

import javax.imageio.ImageIO;

import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.uwpr.metagomics.dto.RunDTO;
import org.uwpr.metagomics.dto.UploadedFastaFileDTO;
import org.uwpr.metagomics.go_counter.database.FastaSearcher;
import org.uwpr.metagomics.go_counter.database.GOAssociationSaver;
import org.uwpr.metagomics.go_counter.database.GOSearcher;
import org.uwpr.metagomics.go_counter.database.PeptideDAO;
import org.uwpr.metagomics.go_counter.database.RunDAO;
import org.uwpr.metagomics.go_counter.database.UploadedFastaFileDAO;
import org.uwpr.metagomics.utils.VersionUtils;
import org.uwpr.metaproteomics.emma.go.GONode;

public class GOCounterRunner {

	public static GOCounterRunner getInstance() { return new GOCounterRunner(); }
	
	/**
	 * Run entire GO counting procedure for a given run. It is assumed that all peptides have
	 * already been mapped to proteins, and that those proteins all have blast hits in the
	 * database. (I.e., that all previous steps of the pipeline have been run.)
	 * 
	 * @param runId
	 * @throws Exception
	 */
	public void runGOCounter( int runId ) throws Exception {

		// a map of peptide sequence to PSM counts (or some other quantitation)
		Map<String, Integer> peptideCounts = getPeptideCountsForRun( runId );

		// this map keyed on id instead, populated below
		Map<Integer, Integer> peptideIdCounts = new HashMap<>();
		
		
		// get total PSM count for run
		int RUN_PSM_COUNT = 0;
		
		int fastaUploadId = FastaSearcher.getInstance().getFastaUploadIdForRun( runId );
		
		// process each peptide, build a count for all GO terms and save each
		// peptide's association to a GO term for this run's fasta upload id (if not already in the db)
		
		Map<GONode, Integer> GO_NODE_COUNT_MAP = new HashMap<>();
		Map<GONode, Collection<Integer>> GO_NODE_PEPTIDE__ID_MAP = new HashMap<>();
		
		for( String peptideSequence : peptideCounts.keySet() ) {
			
			RUN_PSM_COUNT+= peptideCounts.get( peptideSequence );
			
			int peptideId = PeptideDAO.getInstance().getIdForPeptideSequence( peptideSequence );
			int count = peptideCounts.get( peptideSequence );
			
			peptideIdCounts.put( peptideId,  count );
			
			Collection<GONode> peptideGONodes = GOSearcher.getInstance().getAllGONodesForPeptide( peptideId, runId );
			
			for( GONode goNode : peptideGONodes ) {
				
				// increment the count for this GO term by the number associated with this peptide
				if( !GO_NODE_COUNT_MAP.containsKey( goNode ) )
					GO_NODE_COUNT_MAP.put( goNode, 0 );
				
				GO_NODE_COUNT_MAP.put( goNode, GO_NODE_COUNT_MAP.get( goNode ) + count );

				// save the association of this GO term w/ this peptide in this fasta upload id to the database
				GOAssociationSaver.getInstance().saveGOToPeptideAssocation( fastaUploadId, peptideId, goNode.getAcc() );
				
				if( !GO_NODE_PEPTIDE__ID_MAP.containsKey( goNode ) )
					GO_NODE_PEPTIDE__ID_MAP.put( goNode, new HashSet<>() );
				
				GO_NODE_PEPTIDE__ID_MAP.get( goNode ).add( peptideId );
				
			}
		}
		
		
		// save each GO term's count and ratio to the database for this run
		for( GONode node : GO_NODE_COUNT_MAP.keySet() ) {
			int count = GO_NODE_COUNT_MAP.get( node );
			double ratio = (double)count / (double)RUN_PSM_COUNT;
			
			GOAssociationSaver.getInstance().saveGOToRunAssocation( runId, node.getAcc(), count, ratio);
		}
		
		
		RunDTO run = RunDAO.getInstance().getRun( runId );
		run.setTotalPSMCount( RUN_PSM_COUNT );
		
		UploadedFastaFileDTO upload = UploadedFastaFileDAO.getInstance().getUploadedFastaFile( run.getFastaUploadId() );
		String uid = upload.getUniqueId();

		File reportDirectory = new File( Constants.DATA_DOWNLOAD_DIRECTORY, uid );
		if( !reportDirectory.exists() ) {
			reportDirectory.mkdir();
		}
		
		// save go text report to disk
		{			
			FileWriter fw = null;
			try {
				
				File reportFile = new File( reportDirectory, "go_report_" + run.getId() + ".txt" );
				
				fw = new FileWriter( reportFile );
				
				fw.write( "# MetaGOmics GO report" );
				fw.write( "# MetaGOmics version: " + VersionUtils.getVersion() + "\n" );
				fw.write( "# Run date: " + new java.util.Date() + "\n" );
				
				fw.write( "GO acc\tGO aspect\tGO name\tcount\ttotal count\tratio\n" );
				
				
				for( GONode node : GO_NODE_COUNT_MAP.keySet() ) {
					int count = GO_NODE_COUNT_MAP.get( node );
					double ratio = (double)count / (double)RUN_PSM_COUNT;
					
					fw.write( node.getAcc() + "\t" );
					fw.write( node.getTermType() + "\t" );
					fw.write( node.getName() + "\t" );
					fw.write( count + "\t" );
					fw.write( RUN_PSM_COUNT + "\t" );
					fw.write( ratio + "\n" );
				}
				
			} finally {
				if( fw != null ) {
					try { fw.close(); fw = null; }
					catch( Exception e ) { ; }
				}
			}			
		}//end saving report
		
		
		// save the taxonomy report
		TaxonomyReportGenerator.getInstance().generateTaxonomyReport( runId, RUN_PSM_COUNT, peptideIdCounts, GO_NODE_COUNT_MAP, GO_NODE_PEPTIDE__ID_MAP);
		
		
		// save GO images to disk
		Map<String, Map<String, SingleRunGraphOb>> THE_DATA = new HashMap<>();
		{
			
			boolean createImages = false;
			try {
				if( Class.forName( "org.uwpr.local.graph_image.SingleRunGOGraphGenerator", false, null ) != null )
					createImages = true;
				
			} catch( Exception e ) {
				
			}
			
			if( createImages ) {
				for( GONode node : GO_NODE_COUNT_MAP.keySet() ) {
					
					if( !THE_DATA.containsKey( node.getTermType() ) ) {
						THE_DATA.put( node.getTermType(), new HashMap<>() );
					}
					
					Map<String, SingleRunGraphOb> data = THE_DATA.get( node.getTermType() );
					
					int count = GO_NODE_COUNT_MAP.get( node );
					double ratio = (double)count / (double)RUN_PSM_COUNT;
					
					SingleRunGraphOb ob = new SingleRunGraphOb();
					ob.setCount( count );
					ob.setRatio( ratio );
					ob.setNode( node );
					ob.setRunPsmTotal( RUN_PSM_COUNT );
					
					data.put( node.getAcc(), ob );
				}			
				
				for( String aspect : THE_DATA.keySet() ) {
					
					Map<String, SingleRunGraphOb> data = THE_DATA.get( aspect );
	
					if( data.keySet().size() > 0 ) {
						
						File imageFile = new File( reportDirectory, "go_image_" + aspect + "_" + run.getId() + ".png" );
		
						try {
							// save off an image of the remaining GO tree
							
							Class<?> cls = Class.forName( "org.uwpr.local.graph_image.SingleRunGOGraphGenerator" );														
							Object obj = cls.newInstance();

							Method method = cls.getDeclaredMethod( "getGOGraphImage", data.getClass() );

							BufferedImage image = (BufferedImage)method.invoke( obj, data );
							
						    ImageIO.write(image, "png", imageFile);
						    
						    
						    imageFile = new File( reportDirectory, "go_image_" + aspect + "_" + run.getId() + ".svg" );
						    
							Method method2 = cls.getDeclaredMethod( "saveGOGraphSVGImage", data.getClass(), imageFile.getClass() );
							method2.invoke( obj, data, imageFile );
						    	
						} catch ( Exception e ) {
							System.out.println( "Error creating images: " + e.getMessage() );
							System.err.println( "Error creating images: " + e.getMessage() );
						}
					}
				}
			}
		}
		
		
		
		
		// generate and save GO image and report for all comparisons for this fasta upload to disk
		RunComparisonProcessor.generateRunComparisons( THE_DATA, run, upload);
		
		
		
		// contact web service on the metagomics server to mark run complete and notify submittors
		HttpClient client = null;
		HttpPost post = null;
		List<NameValuePair> nameValuePairs = null;

		try {

			client = new DefaultHttpClient();
			post = new HttpPost( Constants.MARK_RUN_COMPLETE_URL );
			nameValuePairs = new ArrayList<NameValuePair>(1);

			nameValuePairs.add(new BasicNameValuePair("runId", String.valueOf( runId ) ) );

			post.setEntity(new UrlEncodedFormEntity(nameValuePairs));
			client.execute(post);
			
		} catch ( Exception e ) {
			throw e;
		}
		
		
	}
	
	/**
	 * Get all peptides and PSM counts for those peptides from the file on disk.
	 * @param runId
	 * @return
	 * @throws Exception
	 */
	private Map<String, Integer> getPeptideCountsForRun( int runId ) throws Exception {
		
		Map<String, Integer> peptideCounts = new HashMap<>();
		
		File peptideCountsFile = new File( Constants.UPLOAD_RUN_TEMP_DIRECTORY, runId + ".txt" );
		
		if( !peptideCountsFile.exists() ) {
			throw new Exception( peptideCountsFile.getAbsolutePath() + " does not exist." );
		}
		
		BufferedReader br = null;
		
		try {
			
			br = new BufferedReader( new FileReader( peptideCountsFile ) );
			
			String line = br.readLine();
			
			while ( line != null ) {
				
				String[] fields = line.split( "\\t" );
				
				String peptide = null;
				Integer count = null;
				
				try {
					peptide = fields[ 0 ];
					count = Integer.parseInt( fields[ 1 ] );
				} catch (Exception e) {
					// just skip lines that can't be parsed
					
				}
				
				if( count != null && peptide != null) {
					
					peptideCounts.put( peptide, count );
					
				}
				
				line = br.readLine();
			}
			
		} finally {
			
			if( br != null ) {
				try { br.close(); br = null; }
				catch( Throwable t ) { ; }
			}
		}
		
		// TODO: erase file?
		
		return peptideCounts;		
	}
	
}
