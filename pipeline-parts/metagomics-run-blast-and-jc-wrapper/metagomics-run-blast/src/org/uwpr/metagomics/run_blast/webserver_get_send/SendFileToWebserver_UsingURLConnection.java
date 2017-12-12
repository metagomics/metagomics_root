package org.uwpr.metagomics.run_blast.webserver_get_send;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;

import org.apache.log4j.Logger;
import org.uwpr.metagomics.run_blast.exceptions.MetagomicsServerSendReceiveException;


/**
 * UNUSED
 * 
 * WARNING:  This has a 2GB limit since the data is first written to a ByteArrayOutputStream.
 * If file is larger than 2GB, a java.lang.OutOfMemoryError is thrown.
 */
public class SendFileToWebserver_UsingURLConnection {

//	private static Logger log = Logger.getLogger(SendFileToWebserver_UsingURLConnection.class);
//
//	private static final String CONTENT_TYPE_SEND_RECEIVE = "application/octet-stream";
//	
//	private static final int SUCCESS_HTTP_RETURN_CODE = 200;
//	
//	private static final int BUFFER_BYTE_ARRAY_SIZE = 32 * 1024;
//
//
//	//  private constructor
//	private SendFileToWebserver_UsingURLConnection() { }
//	/**
//	 * @return newly created instance
//	 */
//	public static SendFileToWebserver_UsingURLConnection getInstance() { 
//		return new SendFileToWebserver_UsingURLConnection(); 
//	}
//	
//	
//	public void sendFileToWebserver( 
//			
//			File fileToSend,
//			String webserviceURL ) throws Exception {
//		
//		//   Create object for connecting to server
//		URL urlObject;
//		try {
//			urlObject = new URL( webserviceURL );
//
//		} catch (MalformedURLException e) {
//			String msg = "Exception creating URL object to connect to server.  URL: " + webserviceURL;
//			log.error(msg, e);
//			throw new MetagomicsServerSendReceiveException( msg, e );
//		}
//
//		//   Open connection to server
//		URLConnection urlConnection;
//		try {
//			urlConnection = urlObject.openConnection();
//
//		} catch (IOException e) {
//			String msg =  "Exception calling openConnection() on URL object to connect to server.  URL: " + webserviceURL;
//			log.error(msg, e);
//			throw new MetagomicsServerSendReceiveException( msg, e );
//		}
//
//		// Downcast URLConnection to HttpURLConnection to allow setting of HTTP parameters 
//		
//		if ( ! ( urlConnection instanceof HttpURLConnection ) ) {
//			String msg = "Processing Error: Cannot cast URLConnection to HttpURLConnection";
//			log.error(msg);
//			throw new MetagomicsServerSendReceiveException( msg );
//		}
//
//		HttpURLConnection httpURLConnection = null;
//		try {
//			httpURLConnection = (HttpURLConnection) urlConnection;
//		} catch (Exception e) {
//			String msg =  "Processing Error: Cannot cast URLConnection to HttpURLConnection";
//			log.error(msg, e);
//			throw new MetagomicsServerSendReceiveException( msg, e );
//		}
//
//		//  Set HttpURLConnection properties
//		
//		httpURLConnection.setRequestProperty( "Accept", CONTENT_TYPE_SEND_RECEIVE );
//		httpURLConnection.setRequestProperty( "Content-Type", CONTENT_TYPE_SEND_RECEIVE );
//
//		httpURLConnection.setDoOutput(true);
//
//		// Send post request to server
//		
//		System.out.println( "File to send Size: " + fileToSend.length() );
//		
//		long bytesReadTotal = 0;
//		long bytesWrittenTotal = 0;
//		
//		try {  //  Overall try/catch block to put "httpURLConnection.disconnect();" in the finally block
//
//			try {
//				httpURLConnection.connect();
//
//			} catch ( IOException e ) {
//				String msg = "Exception connecting to server at URL: " + webserviceURL;
//				log.error(msg, e);
//				throw new MetagomicsServerSendReceiveException( msg, e );
//			}
//
//			//  Send  to server
//			FileInputStream fileInputStream = null; 
//			OutputStream outputStream = null;
//			try {
//				fileInputStream = new FileInputStream(fileToSend);
//				outputStream = httpURLConnection.getOutputStream();
//				
//				//  outputStream is actually instance of sun.net.www.http.PosterOutputStream;
//				// public class sun.net.www.http.PosterOutputStream extends java.io.ByteArrayOutputStream {
//				//  import sun.net.www.http.PosterOutputStream;
////				PosterOutputStream posterOutputStream = (PosterOutputStream)outputStream;
//				
//	            byte[] byteBuffer = new byte[ BUFFER_BYTE_ARRAY_SIZE ];
//	            int bytesRead = -1;
//	            while ((bytesRead = fileInputStream.read(byteBuffer)) > 0) {
//	            	bytesReadTotal += bytesRead;
//	            	outputStream.write(byteBuffer, 0, bytesRead);
////	            	posterOutputStream.write(byteBuffer, 0, bytesRead);
//	            	bytesWrittenTotal += bytesRead;
//	            }
//
//			} catch ( IOException e ) {
//
//				byte[] errorStreamContents = null;
//				try {
//					errorStreamContents= getErrorStreamContents( httpURLConnection, fileToSend, webserviceURL );
//				} catch ( Exception ex ) {
//
//				}
//				String msg = "IOException sending File to server at URL: " + webserviceURL;
//				log.error(msg, e);
//				throw new MetagomicsServerSendReceiveException( msg, e );
//			} finally {
//
//				if ( outputStream != null ) {
//
//					try {
//						outputStream.flush();
//						outputStream.close();
//					} catch ( IOException e ) {
//
//						byte[] errorStreamContents = null;
//
//						try {
//							errorStreamContents= getErrorStreamContents( httpURLConnection, fileToSend, webserviceURL );
//						} catch ( Exception ex ) {
//
//						}
//						String msg = "IOException closing output Stream to server at URL: " + webserviceURL;
//						log.error(msg, e);
//						throw new MetagomicsServerSendReceiveException( msg, e );
//					}
//				}
//				if ( fileInputStream != null ) {
//					try {
//						fileInputStream.close();
//					} catch ( IOException e ) {
//
//						byte[] errorStreamContents = null;
//
//						try {
//							errorStreamContents= getErrorStreamContents( httpURLConnection, fileToSend, webserviceURL );
//						} catch ( Exception ex ) {
//
//						}
//						String msg = "IOException closing input stream from file";
//						log.error(msg, e);
//						throw new MetagomicsServerSendReceiveException( msg, e );
//					}
//				}
//			}
//
//
//			try {
//				int httpResponseCode = httpURLConnection.getResponseCode();
//
//				if ( httpResponseCode != SUCCESS_HTTP_RETURN_CODE ) {
//
//					byte[] errorStreamContents = null;
//
//					try {
//						errorStreamContents= getErrorStreamContents( httpURLConnection, fileToSend, webserviceURL );
//					} catch ( Exception ex ) {
//
//					}
//					String msg = "Unsuccessful HTTP response code of " + httpResponseCode
//									+ " connecting to server at URL: " + webserviceURL;
//					log.error(msg);
//					throw new MetagomicsServerSendReceiveException( msg );
//				}
//
//			} catch ( IOException e ) {
//
//				byte[] errorStreamContents = null;
//
//				try {
//					errorStreamContents= getErrorStreamContents( httpURLConnection, fileToSend, webserviceURL );
//				} catch ( Exception ex ) {
//
//				}
//				String msg = "IOException getting HTTP response code from server at URL: " + webserviceURL;
//				log.error(msg, e);
//				throw new MetagomicsServerSendReceiveException( msg, e );
//			}
//
//			//  Get response from server
//			
//			ByteArrayOutputStream outputStreamBufferOfServerResponse = new ByteArrayOutputStream( 1000000 );
//			InputStream inputStream = null;
//
//			try {
//				inputStream = httpURLConnection.getInputStream();
//				int nRead;
//				byte[] data = new byte[16384];
//
//				while ((nRead = inputStream.read(data, 0, data.length)) != -1) {
//					outputStreamBufferOfServerResponse.write(data, 0, nRead);
//				}
//			} catch ( IOException e ) {
//				byte[] errorStreamContents = null;
//				try {
//					errorStreamContents= getErrorStreamContents( httpURLConnection, fileToSend, webserviceURL );
//				} catch ( Exception ex ) {
//
//				}
//				String msg = "IOException receiving contents/bytes from server at URL: " + webserviceURL;
//				log.error(msg, e);
//				throw new MetagomicsServerSendReceiveException( msg, e );
//			} finally {
//				if ( inputStream != null ) {
//					try {
//						inputStream.close();
//					} catch ( IOException e ) {
//						byte[] errorStreamContents = null;
//						try {
//							errorStreamContents= getErrorStreamContents( httpURLConnection, fileToSend, webserviceURL );
//						} catch ( Exception ex ) {
//
//						}
//						String msg = "IOException closing input Stream from server at URL: " + webserviceURL;
//						log.error(msg, e);
//						throw new MetagomicsServerSendReceiveException( msg, e );
//					}
//
//				}
//			}
//			
//
//			System.out.println( "Output Stream from Server for Send file: " + fileToSend.getCanonicalPath()
//					+ ", URL: " + webserviceURL );
//			System.out.println( "Output Stream from Server contents (For Send File, expect empty):\n" 
//					+ new String( outputStreamBufferOfServerResponse.toByteArray() ) );
//
//		} catch ( Throwable e ) {
//			
//			System.out.println( "Exception Thrown:  bytesReadTotal: " + bytesReadTotal 
//					+ ", bytesWrittenTotal: " + bytesWrittenTotal );
//
//			throw e;
//		} finally {
//
////			httpURLConnection.disconnect();
//		}
//		
//		System.out.println( "Successful Send:  bytesReadTotal: " + bytesReadTotal 
//				+ ", bytesWrittenTotal: " + bytesWrittenTotal );
//
//	}
//	
//
//	/**
//	 * @param httpURLConnection
//	 * @param fileToSend TODO
//	 * @param webserviceURL TODO
//	 * @return
//	 * @throws IOException
//	 */
//	private byte[] getErrorStreamContents(HttpURLConnection httpURLConnection, File fileToSend, String webserviceURL) throws IOException {
//
//		InputStream inputStream = httpURLConnection.getErrorStream();
//
//		ByteArrayOutputStream baos = new ByteArrayOutputStream();
//
//		int byteArraySize = 5000;
//
//		byte[] data = new byte[ byteArraySize ];
//
//		while (true) {
//
//			int bytesRead = inputStream.read( data );
//
//			if ( bytesRead == -1 ) {  // end of input
//
//				break;
//			}
//
//			if ( bytesRead > 0 ) {
//
//				baos.write( data, 0, bytesRead );
//			}
//
//		}
//		
//		System.out.println( "Error Stream from Server for file: " + fileToSend.getCanonicalPath()
//				+ ", URL: " + webserviceURL );
//		System.out.println( "Error Stream from Server contents:\n" + baos.toByteArray().toString() );
//		
//		return baos.toByteArray();
//	}
//

}
