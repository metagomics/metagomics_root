package org.uwpr.metagomics.webservices;

import java.util.Collection;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.FormParam;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;

import org.uwpr.metagomics.dao.FastaFileDAO;
import org.uwpr.metagomics.dao.UploadedFastaFileDAO;
import org.uwpr.metagomics.dto.UploadedFastaFileDTO;
import org.uwpr.metagomics.webutils.EmailUtils;

@Path("/fasta")
public class FastaParsingNotificationService {
	
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	@Path("/markComplete")
	public Message markFastaCompleteGet ( 
			@QueryParam("fastaId") int fastaId,
			@Context HttpServletRequest request ) throws Exception {

			// mark this fasta as completed in the database
			FastaFileDAO.getInstance().markComplete( fastaId );
		
			
			// notify email addressed of uploaded fastas that used this fasta that they
			// can now upload MS/MS data
			Collection<UploadedFastaFileDTO> uploads = UploadedFastaFileDAO.getInstance().getUploadedFastaFilesForFastaFileId( fastaId );
			
			for( UploadedFastaFileDTO upload : uploads ) {
				EmailUtils.getInstance().notifyOfReadyFastaUpload( upload );
			}
			
			Message message = new Message();
			message.setMessage( "success" );
			
			return message;
			
	}
	
	@POST
	@Produces(MediaType.APPLICATION_JSON)
	@Path("/markComplete")
	public Message markFastaCompletePost ( 
			@FormParam("fastaId") int fastaId,
			@Context HttpServletRequest request ) throws Exception {

			// mark this fasta as completed in the database
			FastaFileDAO.getInstance().markComplete( fastaId );
		
			
			// notify email addressed of uploaded fastas that used this fasta that they
			// can now upload MS/MS data
			Collection<UploadedFastaFileDTO> uploads = UploadedFastaFileDAO.getInstance().getUploadedFastaFilesForFastaFileId( fastaId );
			
			for( UploadedFastaFileDTO upload : uploads ) {
				EmailUtils.getInstance().notifyOfReadyFastaUpload( upload );
			}
			
			Message message = new Message();
			message.setMessage( "success" );
			
			return message;
			
	}
	
	public class Message {
		
		private String message;

		public String getMessage() {
			return message;
		}

		public void setMessage(String message) {
			this.message = message;
		}
		
	}
}