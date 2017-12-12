package org.uwpr.metagomics.run_blast.webserver_get_send;


import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;

import org.apache.log4j.Logger;
import org.uwpr.metagomics.run_blast.exceptions.MetagomicsServerSendReceiveException;

/**
 * 
 *
 */
public class GetFileFromWebserver {

	private static Logger log = Logger.getLogger(GetFileFromWebserver.class);

	private static final int SUCCESS_HTTP_RETURN_CODE = 200;
	
	private static final int BUFFER_BYTE_ARRAY_SIZE = 32 * 1024;


	//  private constructor
	private GetFileFromWebserver() { }
	/**
	 * @return newly created instance
	 */
	public static GetFileFromWebserver getInstance() { 
		return new GetFileFromWebserver(); 
	}
	
	
	public void getFileFromWebserver( 
			
			File fileToWriteTo,
			String webserviceURL ) throws Exception {
		
		//   Create object for connecting to server
		URL urlObject;
		try {
			urlObject = new URL( webserviceURL );

		} catch (MalformedURLException e) {
			String msg = "Exception creating URL object to connect to server.  URL: " + webserviceURL;
			log.error(msg, e);
			throw new MetagomicsServerSendReceiveException( msg, e );
		}

		//   Open connection to server
		URLConnection urlConnection;
		try {
			urlConnection = urlObject.openConnection();

		} catch (IOException e) {
			String msg =  "Exception calling openConnection() on URL object to connect to server.  URL: " + webserviceURL;
			log.error(msg, e);
			throw new MetagomicsServerSendReceiveException( msg, e );
		}

		// Downcast URLConnection to HttpURLConnection to allow setting of HTTP parameters 
		
		if ( ! ( urlConnection instanceof HttpURLConnection ) ) {
			String msg = "Processing Error: Cannot cast URLConnection to HttpURLConnection";
			log.error(msg);
			throw new MetagomicsServerSendReceiveException( msg );
		}

		HttpURLConnection httpURLConnection = null;
		try {
			httpURLConnection = (HttpURLConnection) urlConnection;
		} catch (Exception e) {
			String msg =  "Processing Error: Cannot cast URLConnection to HttpURLConnection";
			log.error(msg, e);
			throw new MetagomicsServerSendReceiveException( msg, e );
		}

		//  Set HttpURLConnection properties
		
//		httpURLConnection.setRequestProperty( "Accept", CONTENT_TYPE_SEND_RECEIVE );
//		httpURLConnection.setRequestProperty( "Content-Type", CONTENT_TYPE_SEND_RECEIVE );

		//  Only for POST
//		httpURLConnection.setDoOutput(true);

		// Send post request to server
		
		try {  //  Overall try/catch block to put "httpURLConnection.disconnect();" in the finally block

			try {
				httpURLConnection.connect();

			} catch ( IOException e ) {
				String msg = "Exception connecting to server at URL: " + webserviceURL;
				log.error(msg, e);
				throw new MetagomicsServerSendReceiveException( msg, e );
			}

			try {
				int httpResponseCode = httpURLConnection.getResponseCode();

				if ( httpResponseCode != SUCCESS_HTTP_RETURN_CODE ) {

					byte[] errorStreamContents = null;

					try {
						errorStreamContents= getErrorStreamContents( httpURLConnection, fileToWriteTo, webserviceURL );
					} catch ( Exception ex ) {

					}
					String msg = "Unsuccessful HTTP response code of " + httpResponseCode
									+ " connecting to server at URL: " + webserviceURL;
					log.error(msg);
					throw new MetagomicsServerSendReceiveException( msg );
				}

			} catch ( IOException e ) {

				byte[] errorStreamContents = null;

				try {
					errorStreamContents= getErrorStreamContents( httpURLConnection, fileToWriteTo, webserviceURL );
				} catch ( Exception ex ) {

				}
				String msg = "IOException getting HTTP response code from server at URL: " + webserviceURL;
				log.error(msg, e);
				throw new MetagomicsServerSendReceiveException( msg, e );
			}

			//  Get response from server
			
			BufferedOutputStream bufferedOutputStream = null;
			InputStream inputStream = null;

			try {
				inputStream = httpURLConnection.getInputStream();
				bufferedOutputStream = new BufferedOutputStream( new FileOutputStream(fileToWriteTo) );
				int nRead;
				byte[] data = new byte[ BUFFER_BYTE_ARRAY_SIZE ];

				while ((nRead = inputStream.read(data, 0, data.length)) != -1) {
					bufferedOutputStream.write(data, 0, nRead);
				}
			} catch ( IOException e ) {
				byte[] errorStreamContents = null;
				try {
					errorStreamContents= getErrorStreamContents( httpURLConnection, fileToWriteTo, webserviceURL );
				} catch ( Exception ex ) {

				}
				String msg = "IOException receiving contents/bytes from server at URL: " + webserviceURL;
				log.error(msg, e);
				throw new MetagomicsServerSendReceiveException( msg, e );
			} finally {
				Exception exceptionClosingOutput = null;
				String errorMsgClosingOutput = null;
				if ( bufferedOutputStream != null ) {
					try {
						bufferedOutputStream.close();
					} catch (Exception e ) {
						exceptionClosingOutput = e;
						errorMsgClosingOutput = "Error closing output stream to file: " + fileToWriteTo.getCanonicalPath();
						log.error( errorMsgClosingOutput, e);
					}
				}
				
				if ( inputStream != null ) {
					try {
						inputStream.close();
					} catch ( Exception e ) {
						byte[] errorStreamContents = null;
						try {
							errorStreamContents= getErrorStreamContents( httpURLConnection, fileToWriteTo, webserviceURL );
						} catch ( Exception ex ) {

						}
						String msg = "Exception closing input Stream from server at URL: " + webserviceURL;
						log.error(msg, e);
						if ( exceptionClosingOutput == null ) {
							throw new MetagomicsServerSendReceiveException( msg, e );
						}
					}
				}
				if ( exceptionClosingOutput != null ) {
					throw new MetagomicsServerSendReceiveException( errorMsgClosingOutput, exceptionClosingOutput );
				}
			}
			

			System.out.println( "Got file from Server (wrote to local file system): " + fileToWriteTo.getCanonicalPath()
					+ ", URL: " + webserviceURL );
			

		} finally {

//			httpURLConnection.disconnect();
		}
		
	}
	

	/**
	 * @param httpURLConnection
	 * @param fileToSend TODO
	 * @param webserviceURL TODO
	 * @return
	 * @throws IOException
	 */
	private byte[] getErrorStreamContents(HttpURLConnection httpURLConnection, File fileToWriteTo, String webserviceURL) throws IOException {

		InputStream inputStream = httpURLConnection.getErrorStream();

		ByteArrayOutputStream baos = new ByteArrayOutputStream();

		int byteArraySize = 5000;

		byte[] data = new byte[ byteArraySize ];

		while (true) {

			int bytesRead = inputStream.read( data );

			if ( bytesRead == -1 ) {  // end of input

				break;
			}

			if ( bytesRead > 0 ) {

				baos.write( data, 0, bytesRead );
			}

		}
		
		System.out.println( "Error Stream from Server for file: " + fileToWriteTo.getCanonicalPath()
				+ ", URL: " + webserviceURL );
		System.out.println( "Error Stream from Server contents:\n" + baos.toByteArray().toString() );
		
		return baos.toByteArray();
	}


}
