package org.uwpr.metagomics.webservices;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.FormParam;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;

import org.uwpr.metagomics.webconstants.GOConstants;
import org.uwpr.metagomics.webutils.DownloadDataUtils;

@Path("/files")
public class CheckFilesExistService {

	
	
	
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	@Path("/existTwoRuns")
	public Message checkFilesExistTwoRunsGet ( 
			@QueryParam("uniqueId") String uid,
			@QueryParam("run1Id") int run1Id,
			@QueryParam("run2Id") int run2Id,
			@Context HttpServletRequest request ) throws Exception {

		Message message = new Message();
		
		{
			File reportFile = DownloadDataUtils.getReportFileTwoRuns( uid, run1Id, run2Id );
			if( reportFile != null && reportFile.exists() ) {
				message.setReportExists( true );
			} else {
				message.setReportExists( false );
			}
		}
		
		List<String> imageTypes = new ArrayList<>( 2 );
		imageTypes.add( "png" );
		imageTypes.add( "svg" );
		
		Map<String, Map<String, Boolean>> imageExists = new HashMap<>();
		
		for( String aspect : GOConstants.GO_ASPECTS ) {
			imageExists.put( aspect,  new HashMap<>() );
			
			for( String format : imageTypes ) {
				
				File imageFile = DownloadDataUtils.getImageFileTwoRuns( uid, run1Id, run2Id, aspect, format);
				if( imageFile != null && imageFile.exists() ) {
					imageExists.get( aspect ).put( format, true );
				} else {
					imageExists.get( aspect ).put( format, false );
				}				
			}
		}

		message.setImageExists( imageExists );
		return message;

	}

	@POST
	@Produces(MediaType.APPLICATION_JSON)
	@Path("/existTwoRuns")
	public Message checkFilesExistTwoRunsPost ( 
			@FormParam("uniqueId") String uid,
			@FormParam("run1Id") int run1Id,
			@FormParam("run2Id") int run2Id,
			@Context HttpServletRequest request ) throws Exception {

		Message message = new Message();
		
		{
			File reportFile = DownloadDataUtils.getReportFileTwoRuns( uid, run1Id, run2Id );
			if( reportFile != null && reportFile.exists() ) {
				message.setReportExists( true );
			} else {
				message.setReportExists( false );
			}
		}
		
		List<String> imageTypes = new ArrayList<>( 2 );
		imageTypes.add( "png" );
		imageTypes.add( "svg" );
		
		Map<String, Map<String, Boolean>> imageExists = new HashMap<>();
		
		for( String aspect : GOConstants.GO_ASPECTS ) {
			imageExists.put( aspect,  new HashMap<>() );
			
			for( String format : imageTypes ) {
				
				File imageFile = DownloadDataUtils.getImageFileTwoRuns( uid, run1Id, run2Id, aspect, format);
				if( imageFile != null && imageFile.exists() ) {
					imageExists.get( aspect ).put( format, true );
				} else {
					imageExists.get( aspect ).put( format, false );
				}				
			}
		}

		message.setImageExists( imageExists );
		return message;

	}
	
	
	
	
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	@Path("/existSingleRun")
	public Message checkFilesExistOneRunGet ( 
			@QueryParam("uniqueId") String uid,
			@QueryParam("runId") int runId,
			@Context HttpServletRequest request ) throws Exception {

		Message message = new Message();
		
		{
			File reportFile = DownloadDataUtils.getReportFileSingleRun( uid, runId );
			if( reportFile != null && reportFile.exists() ) {
				message.setReportExists( true );
			} else {
				message.setReportExists( false );
			}
		}
		
		{
			File reportFile = DownloadDataUtils.getTaxonomyReportFileSingleRun( uid, runId );
			if( reportFile != null && reportFile.exists() ) {
				message.setTaxonomyReportExists( true );
			} else {
				message.setTaxonomyReportExists( false );
			}
		}
		
		List<String> imageTypes = new ArrayList<>( 2 );
		imageTypes.add( "png" );
		imageTypes.add( "svg" );
		
		Map<String, Map<String, Boolean>> imageExists = new HashMap<>();
		
		for( String aspect : GOConstants.GO_ASPECTS ) {
			imageExists.put( aspect,  new HashMap<>() );
			
			for( String format : imageTypes ) {
				
				File imageFile = DownloadDataUtils.getImageFileSingleRun( uid, runId, aspect, format);
				if( imageFile != null && imageFile.exists() ) {
					imageExists.get( aspect ).put( format, true );
				} else {
					imageExists.get( aspect ).put( format, false );
				}				
			}
		}

		message.setImageExists( imageExists );
		return message;

	}

	@POST
	@Produces(MediaType.APPLICATION_JSON)
	@Path("/existSingleRun")
	public Message checkFilesExistOneRunPost ( 
			@FormParam("uniqueId") String uid,
			@FormParam("runId") int runId,
			@Context HttpServletRequest request ) throws Exception {

		Message message = new Message();
		
		{
			File reportFile = DownloadDataUtils.getReportFileSingleRun( uid, runId );
			if( reportFile != null && reportFile.exists() ) {
				message.setReportExists( true );
			} else {
				message.setReportExists( false );
			}
		}
		
		{
			File reportFile = DownloadDataUtils.getTaxonomyReportFileSingleRun( uid, runId );
			if( reportFile != null && reportFile.exists() ) {
				message.setTaxonomyReportExists( true );
			} else {
				message.setTaxonomyReportExists( false );
			}
		}
		
		List<String> imageTypes = new ArrayList<>( 2 );
		imageTypes.add( "png" );
		imageTypes.add( "svg" );
		
		Map<String, Map<String, Boolean>> imageExists = new HashMap<>();
		
		for( String aspect : GOConstants.GO_ASPECTS ) {
			imageExists.put( aspect,  new HashMap<>() );
			
			for( String format : imageTypes ) {
				
				File imageFile = DownloadDataUtils.getImageFileSingleRun( uid, runId, aspect, format);
				if( imageFile != null && imageFile.exists() ) {
					imageExists.get( aspect ).put( format, true );
				} else {
					imageExists.get( aspect ).put( format, false );
				}				
			}
		}

		message.setImageExists( imageExists );
		return message;

	}

	public class Message {

		private boolean reportExists;
		
		private boolean taxonomyReportExists;
		
		// the key is aspect, inner key is format
		private Map<String, Map<String, Boolean>> imageExists;

		public boolean isReportExists() {
			return reportExists;
		}

		public void setReportExists(boolean reportExists) {
			this.reportExists = reportExists;
		}

		public Map<String, Map<String, Boolean>> getImageExists() {
			return imageExists;
		}

		public void setImageExists(Map<String, Map<String, Boolean>> imageExists) {
			this.imageExists = imageExists;
		}

		public boolean isTaxonomyReportExists() {
			return taxonomyReportExists;
		}

		public void setTaxonomyReportExists(boolean taxonomyReportExists) {
			this.taxonomyReportExists = taxonomyReportExists;
		}
		
		
		
	}
}