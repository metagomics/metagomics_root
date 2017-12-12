package org.uwpr.metagomics.webutils;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.security.MessageDigest;


public class Sha1SumCalculator {
	
	private Sha1SumCalculator() { }
	public static Sha1SumCalculator getInstance() { 
		return new Sha1SumCalculator(); 
	}
	
	
	public String getSHA1Sum( InputStream is ) throws Exception {
		
		String result = null;
				
		try {

			MessageDigest md = MessageDigest.getInstance("SHA1");
			byte[] dataBytes = new byte[1024];

			int nread = 0; 

			while ((nread = is.read(dataBytes)) != -1) {
				md.update(dataBytes, 0, nread);
			};

			byte[] mdbytes = md.digest();

			//convert the byte to hex format
			StringBuffer sb = new StringBuffer("");
			for (int i = 0; i < mdbytes.length; i++) {
				sb.append(Integer.toString((mdbytes[i] & 0xff) + 0x100, 16).substring(1));
			}
			
			result = sb.toString();
			
		} finally {
			
			if ( is != null ) {
				
				is.close();
			}
		}
		
		
	    return result;
	}
	
	public String getSHA1Sum( File f ) throws Exception {		
		FileInputStream fis = new FileInputStream( f );
		return getSHA1Sum( fis );
	}
	
}