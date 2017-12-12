package org.uwpr.metagomics.actions;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.Action;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.uwpr.metagomics.dao.UploadedFastaFileDAO;
import org.uwpr.metagomics.dto.UploadedFastaFileDTO;
import org.uwpr.metagomics.webconstants.WebAppConstants;

public class ViewUploadedFastaFileAction extends Action {

	public ActionForward execute( ActionMapping mapping,
			  ActionForm form,
			  HttpServletRequest request,
			  HttpServletResponse response )
					  throws Exception {
		
		
		String uid = request.getParameter( "uid" );
		if( uid == null ) {
			request.setAttribute( "error_message", "No uid supplied." );
			return mapping.findForward( "Failure" );
		}
		

		UploadedFastaFileDTO upload = null;
		
		try {
			upload = UploadedFastaFileDAO.getInstance().getUploadedFastaFile( uid );
		} catch( Exception e ) {
			request.setAttribute( "error_message", "Error loading the uploaded fasta file object." );
			return mapping.findForward( "Failure" );
		}
		
		if( upload == null ) {
			request.setAttribute( "error_message", "Invalid unique id." );
			return mapping.findForward( "Failure" );
		}
		
		request.setAttribute( "uniqueId", uid );
		
		if( uid.equals( WebAppConstants.DEMO_PROJECT_HASH ) ) {
			request.setAttribute( "is_demo",  true );
		}
		
		return mapping.findForward( "Success" );
	}

}
