package org.uwpr.metagomics.fasta_importer.program;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.yeastrc.fasta.FASTAEntry;
import org.yeastrc.fasta.FASTAHeader;
import org.yeastrc.fasta.FASTAReader;

/**
 * The code that runs the entire FASTA parsing pipeline, given a unique ID
 * associated with an uploaded FASTA file.
 * 
 * @author michaelriffle
 *
 */
public class FastaParserRunner {

	public static FastaParserRunner getInstance() { return new FastaParserRunner(); }
	private FastaParserRunner() { }
	
	/**
	 * Run the import pipeline of a FASTA file, given a unique Id
	 * @param uniqueId
	 */
	public void runFastaImportPipeline( int fastaFileId ) throws Exception {
		
		System.out.println( "Processing fasta file id: " + fastaFileId );
		
		/*
		 * Read through FASTA file and insert necessary rows into
		 * database.
		 */
		
		File fastaFile = new File( Constants.UPLOAD_TEMP_DIRECTORY, String.valueOf( fastaFileId) + ".fasta" );
		
		if( !fastaFile.exists() ) {
			throw new Exception( "Can not find FASTA file for " + fastaFileId );
		}
		
		FASTAReader faReader = FASTAReader.getInstance( fastaFile );
		try {
			
			FASTAEntry entry = faReader.readNext();
			
			if( entry == null ) {
				throw new Exception( "Could not find a FASTA entry in the file. Is it a FASTA file?" );
			}
			
			while ( entry != null ) {
				
				Set<FASTAHeader> headers = entry.getHeaders();
				
				String sequence = entry.getSequence();
				int sequenceId = DatabaseUtils.getInstance().getIdForProteinSequence( sequence );
				
				for( FASTAHeader header : headers ) {
					
					DatabaseUtils.getInstance().insertFastaProtein(fastaFileId, sequenceId, header.getName(), header.getDescription() );
					
					
				}
				
				
				entry = faReader.readNext();
			}
			
			
		} finally {			
			try { faReader.close(); }
			catch( Exception e ) { ; }
		}
		
		
		// contact web service on the metagomics server to mark fasta complete and notify submittors
		HttpClient client = null;
		HttpPost post = null;
		List<NameValuePair> nameValuePairs = null;


		try {

			client = new DefaultHttpClient();
			post = new HttpPost( Constants.MARK_FASTA_COMPLETE_URL );
			nameValuePairs = new ArrayList<NameValuePair>(1);
			nameValuePairs.add(new BasicNameValuePair("fastaId", String.valueOf( fastaFileId) ) );
			post.setEntity(new UrlEncodedFormEntity(nameValuePairs));
			client.execute(post);
			
		} catch ( Exception e ) {
			throw e;
		}
		
		
	}
	
	
}
