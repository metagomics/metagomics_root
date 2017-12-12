package org.uwpr.metagomics.webservices;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.FormParam;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;

import org.uwpr.metagomics.dao.RunDAO;
import org.uwpr.metagomics.webutils.EmailUtils;

@Path("/run")
public class RunUploadNotificationService {

	@GET
	@Produces(MediaType.APPLICATION_JSON)
	@Path("/markComplete")
	public Message markFastaCompleteGet ( 
			@QueryParam("runId") int runId,
			@Context HttpServletRequest request ) throws Exception {

		// mark run as complete
		RunDAO.getInstance().markRunComplete( runId );


		// notify user it's ready
		EmailUtils.getInstance().notifyOfReadyRun( runId );

		Message message = new Message();
		message.setMessage( "success" );

		return message;

	}

	@POST
	@Produces(MediaType.APPLICATION_JSON)
	@Path("/markComplete")
	public Message markFastaCompletePost ( 
			@FormParam("runId") int runId,
			@Context HttpServletRequest request ) throws Exception {

		// mark run as complete
		RunDAO.getInstance().markRunComplete( runId );


		// notify user it's ready
		EmailUtils.getInstance().notifyOfReadyRun( runId );

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
