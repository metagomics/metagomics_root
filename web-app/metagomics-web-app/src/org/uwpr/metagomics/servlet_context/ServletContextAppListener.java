package org.uwpr.metagomics.servlet_context;


import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.http.HttpServlet;

import org.apache.log4j.Logger;


/**
 * This class is loaded and the method "contextInitialized" is called when the web application is first loaded by the container
 *
 */
public class ServletContextAppListener extends HttpServlet implements ServletContextListener {

	private static Logger log = Logger.getLogger( ServletContextAppListener.class );
	private static final long serialVersionUID = 1L;

	/* (non-Javadoc)
	 * @see javax.servlet.ServletContextListener#contextInitialized(javax.servlet.ServletContextEvent)
	 */
	public void contextInitialized(ServletContextEvent event) {
		
		
		log.warn( "INFO:  !!!!!!!!!!!!!!!   Start up of web app  'metagomics' beginning  !!!!!!!!!!!!!!!!!!!! " );


		ServletContext context = event.getServletContext();

		String contextPath = context.getContextPath();

		context.setAttribute( "contextPath", contextPath );
		CurrentContext.setCurrentWebAppContext( contextPath );
		
		String jsCssCacheBustString = getJsCssCacheBustString();
		context.setAttribute( "cacheBustValue", jsCssCacheBustString );
		
		log.warn( "INFO:  !!!!!!!!!!!!!!!   Start up of web app  'Proxl' complete  !!!!!!!!!!!!!!!!!!!! " );

		log.warn( "INFO: Application context values set.  Key = contextPath: value = " + contextPath
				+ "" );


	}
	
	private String getJsCssCacheBustString() {
		
		long currentTime = System.currentTimeMillis();
		
		String currentTimeHexString = Long.toHexString( currentTime );
		
		return currentTimeHexString;
	}

	/* (non-Javadoc)
	 * @see javax.servlet.ServletContextListener#contextDestroyed(javax.servlet.ServletContextEvent)
	 */
	public void contextDestroyed(ServletContextEvent event) {


	}


}
