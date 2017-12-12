package org.uwpr.metagomics.run_blast.webserver_get_send;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.Arrays;

import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.InputStreamEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.log4j.Logger;
import org.uwpr.metagomics.run_blast.exceptions.MetagomicsServerSendReceiveException;


/**
 * 
 *
 */
public class SendFileToWebserver {

	private static Logger log = Logger.getLogger(SendFileToWebserver.class);

	private static final int SUCCESS_HTTP_RETURN_CODE = 200;


	//  private constructor
	private SendFileToWebserver() { }
	/**
	 * @return newly created instance
	 */
	public static SendFileToWebserver getInstance() { 
		return new SendFileToWebserver(); 
	}
	
	
	public void sendFileToWebserver( 
			
			File fileToSend,
			String webserviceURL ) throws Exception {
		

		CloseableHttpClient httpclient = null;

		HttpPost post = null;
		HttpResponse response = null;

		InputStream fileToSendInputStream = null;
		
		InputStream responseInputStream = null;
		
		try {
			fileToSendInputStream = new FileInputStream(fileToSend);
			long fileToSendLength = fileToSend.length();
			
			httpclient = HttpClients.createDefault();

			post = new HttpPost( webserviceURL );
			
			InputStreamEntity inputStreamEntity = 
					new InputStreamEntity( 
							fileToSendInputStream, 
							fileToSendLength, 
							ContentType.APPLICATION_OCTET_STREAM );

			post.setEntity( inputStreamEntity );

			response = httpclient.execute(post);
			
			int httpStatusCode = response.getStatusLine().getStatusCode();

			if ( log.isDebugEnabled() ) {

				log.debug("Send Email: Http Response Status code: " + httpStatusCode );
			}
			

			responseInputStream = response.getEntity().getContent();
			
			//  optional code for viewing response as string
			
			//  responseBytes must be large enough for the whole response, or code something to create larger array and copy to the larger array
			
			byte[] responseBytes = new byte[10000000];
			
			int responseBytesOffset = 0;
			int responseBytesLength = responseBytes.length;
			
			int totalBytesRead = 0;
			
			while (true) {

				int bytesRead = responseInputStream.read(responseBytes, responseBytesOffset, responseBytesLength );
			
				if ( bytesRead == -1 ) {
					
					break;
				}
				
				totalBytesRead += bytesRead;
				responseBytesOffset += bytesRead;
				responseBytesLength -= bytesRead;
			}
			
			byte[] responseBytesJustData = Arrays.copyOf(responseBytes, totalBytesRead);
			
			String responseAsString = new String(responseBytesJustData );
			
			if ( httpStatusCode != SUCCESS_HTTP_RETURN_CODE ) {
				
				String msg = "Send of file failed with HTTP Status code: " 
						+ httpStatusCode 
						+ ", for file: " + fileToSend.getCanonicalPath();
				System.err.println( msg );
				
				System.err.println( "Send file response: " );
				System.err.println( responseAsString );
				
				throw new MetagomicsServerSendReceiveException( msg );
			}	

		} catch (MetagomicsServerSendReceiveException e) {
			throw e;
			
		} catch (Exception e) {

			log.error("Failed Send File: " + fileToSend.getCanonicalPath(), e );
			throw e;

		} finally { 

			if ( responseInputStream != null ) {
				responseInputStream.close();
			}
			
			if ( httpclient != null ) {
				// When HttpClient instance is no longer needed,
				// shut down the connection manager to ensure
				// immediate deallocation of all system resources
				httpclient.close();
			}
			if ( fileToSendInputStream != null ) {
				fileToSendInputStream.close();
			}

		}
		
	}
}
