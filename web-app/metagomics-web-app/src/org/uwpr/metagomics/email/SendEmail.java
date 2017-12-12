package org.uwpr.metagomics.email;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.log4j.Logger;


/**
 * 
 *
 */
public class SendEmail {

	
	private static final SendEmail instance = new SendEmail();

	private SendEmail() { }
	public static SendEmail getInstance() { return instance; }

	
	private static final Logger log = Logger.getLogger(SendEmail.class);


	private static final String TO = "to";
	private static final String FROM = "from";
	private static final String SUBJECT = "subject";
	private static final String BODY = "body";


	public void sendEmail( SendEmailDTO sendEmailDTO ) throws Exception  {		
		
		String email_webservice_url = EmailConfig.EMAIL_WEBSERVICE_URL;

		HttpClient client = null;
		HttpPost post = null;
		BufferedReader rd = null;
		List<NameValuePair> nameValuePairs = null;
		HttpResponse response = null;


		try {

			client = new DefaultHttpClient();

			post = new HttpPost( email_webservice_url );

			nameValuePairs = new ArrayList<NameValuePair>(1);

			nameValuePairs.add(new BasicNameValuePair(TO, sendEmailDTO.getToEmailAddress() ));
			nameValuePairs.add(new BasicNameValuePair(FROM, sendEmailDTO.getFromEmailAddress() ));
			nameValuePairs.add(new BasicNameValuePair(SUBJECT, sendEmailDTO.getEmailSubject() ));
			nameValuePairs.add(new BasicNameValuePair(BODY, sendEmailDTO.getEmailBody() ));

			post.setEntity(new UrlEncodedFormEntity(nameValuePairs));

			response = client.execute(post);
			
			int httpStatusCode = response.getStatusLine().getStatusCode();

			if ( log.isDebugEnabled() ) {

				log.debug("Send Email: Http Response Status code: " + httpStatusCode 
						+ ", email_webservice_url: " + email_webservice_url );
			}

			rd = new BufferedReader(new InputStreamReader(response.getEntity().getContent()));
			
			String line = "";
			while ((line = rd.readLine()) != null) {

				if ( log.isDebugEnabled() ) {

					log.debug("Send Email: Http Response Line: " + line);
				}

			}

			
			if ( httpStatusCode != HttpStatus.SC_OK ) {
				
				String msg = "Failed to send Email.  Http Response Status code: " + httpStatusCode 
						+ ", email_webservice_url: " + email_webservice_url ;
				
				log.error( msg );
				
				throw new Exception(msg);
			}


		} catch (Exception e) {

			log.error("Failed to send email request.  from address = |" + sendEmailDTO.getFromEmailAddress() 
					+ "|, to address = |" + sendEmailDTO.getToEmailAddress() + "|."
					+ ", email_webservice_url: " + email_webservice_url , e );
			throw e;

		} finally { 

			if ( rd != null ) {
				rd.close();
			}
		}
	}


}
