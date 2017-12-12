package org.uwpr.metagomics.webutils;

import java.security.SecureRandom;

public class UniqueIDGenerator {
	
	private static final int ID_LENGTH = 16;
	
	private static final UniqueIDGenerator _INSTANCE = new UniqueIDGenerator();
	public static UniqueIDGenerator getInstance() { return _INSTANCE; }
	private UniqueIDGenerator() { }
	
	
	private static final String AB = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz";
	private static SecureRandom rnd = new SecureRandom();

	private static String randomString( int len ){
	   StringBuilder sb = new StringBuilder( len );
	   for( int i = 0; i < len; i++ ) 
	      sb.append( AB.charAt( rnd.nextInt(AB.length()) ) );
	   return sb.toString();
	}

	
	/**
	 * Get a unique id to use for an uploaded metaproteomic FASTA file
	 * Guaranteed to be unique.
	 * 
	 * @return
	 */
	public String generateNewUniqueID() throws Exception {
		
		String uniqueId = randomString( ID_LENGTH );
		while( !UniqueIdTester.getInstance().isUnique( uniqueId ) ) {
			uniqueId = randomString( ID_LENGTH );
		}
		
		return uniqueId;
	}
	
	
	
}
