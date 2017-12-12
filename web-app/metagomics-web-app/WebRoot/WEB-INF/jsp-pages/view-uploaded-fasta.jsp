<%@ include file="/WEB-INF/jsp-includes/strutsTaglibImport.jsp" %>
<%@ include file="/WEB-INF/jsp-includes/jstlTaglibImport.jsp" %>

 <c:set var="pageTitle">Upload MS/MS results</c:set>
 
 <c:set var="headerAdditions">
 
		<script type="text/javascript" src="${ contextPath }/js/uploaded-fasta-file-page.js"></script>

</c:set>










<%@ include file="/WEB-INF/jsp-includes/header_main.jsp" %>

<div class="overall-enclosing-block view-uploaded-fasta-page">

	<div id="fasta-upload-overview" style="display:none;">
		<div id="upload-title"><span id="upload-nickname"></span> <span id="upload-fasta-filename"></span></div>
		<div id="db-name">GO annotation DB: <span id="annotation-database-name"></span></div>
		<div id="blast-filters">BLAST filters: <span id="blast-cutoff"></span>, <span id="use-top-hit"></span></div>
	</div>
	
	
	<div id="fasta-not-processed" style="display:none;">
		<div class="not-processed-header">FASTA file queued for processing</div>
		<div class="not-processed-text">This FASTA file has been submitted to the server for processing. Once complete
		you may return to this URL to upload peptide counts using this FASTA file. You may check back at this URL later, and
		you will receive an email at #EMAIL# when it is ready.
		</div>
	</div>
	
	<div id="fasta-is-processed" style="display:none;">
		<div class="is-processed-header">Uploaded Peptide Count Lists</div>
		<div class="is-processed-ms-run-list"></div>

		<div id="have-run-data" style="display:none;">
			<table id="run-data-table" style="margin-top:20px;border:none;">
			
			
			</table>
			
			<input onClick="javascript:performTwoRunDownload()" type="button" id="view-merged-run-button" value="Compare Checked Runs">
			
		</div>
		
		<div id="no-run-data" style="display:none;">
			No peptide count lists have been uploaded.
		</div>

		<logic:equal name="is_demo" value="true">
			<input type="button" id="upload-ms-results-button" value="Upload Peptide Count List"/>
		</logic:equal>
		
		<logic:notEqual name="is_demo" value="true">
			<input type="button" onClick="showUploadOverlay()" id="upload-ms-results-button" value="Upload Peptide Count List"/>	
		</logic:notEqual>
		
	</div>
	
</div>





<div class="upload-overlay-background"></div>
<div class="upload-overlay-content">

	<form enctype="multipart/form-data">

	<div class="header-item">Choose file containing peptide counts:</div>
	<div class="header-item-explain">Text file containing tab-delimited list of peptide sequences and counts (one per line).</div>
	
	<div class="form-item">
		<input id="run-file-field" type="file" style="font-size:19pt;" />
	</div>

	<div class="form-item">
		Nickname: <input id="run-file-nickname" type="text" maxlength="30" style="font-size:19pt;background-color:#E8E8E8;border-style:solid;border-color:black;border-width:2px;" />
	</div>
	<div class="header-item-explain">
		A short description of this file, e.g. &quotCondition 1, replicate 2&quot;
	</div>

	<input onClick="uploadRunFile()" value="Upload Data" type="button" style="font-size:33pt;background-color:#E8E8E8;border-style:solid;border-color:black;border-width:2px;margin-left:50px;margin-top:20px;">
	<input onClick="hideUploadOverlay()" value="Cancel" type="button" style="font-size:33pt;background-color:#E8E8E8;border-style:solid;border-color:black;border-width:2px;margin-left:50px;margin-top:20px;">



	</form>

</div>


<div class="download-single-run-overlay-content">

	<div class="header-item">Download data:</div>
	<div class="header-item-explain">Click the link of the data to download. If it is greyed out, there may be no significant terms of that GO aspect.</div>

	<div class="download-item"><a id="report-link" href="">Download GO Report</a></div>
	<div class="download-item"><a id="taxonomy-report-link" href="">Download Taxonomy Report</a></div>
	
	<div class="download-item">Download GO Image:</div>
	<div class="download-item image-option">
		Biological Process
		[<a id="biological_process-image-png" href="">PNG</a>]
		[<a id="biological_process-image-svg" href="">SVG</a>]
	</div>
	<div class="download-item image-option">
		Molecular Function
		[<a id="molecular_function-image-png" href="">PNG</a>]
		[<a id="molecular_function-image-svg" href="">SVG</a>]
	</div>
	<div class="download-item image-option">
		Cellular Component
		[<a id="cellular_component-image-png" href="">PNG</a>]
		[<a id="cellular_component-image-svg" href="">SVG</a>]
	</div>

	<input onClick="hideDownloadSingleRunOverlay()" value="Close" type="button" style="font-size:24pt;background-color:#E8E8E8;border-style:solid;border-color:black;border-width:2px;margin-top:30px;">

</div>



<div id="hidden-unique-id-div" style="display:none;"><bean:write name="uniqueId"/></div>

<%@ include file="/WEB-INF/jsp-includes/footer_main.jsp" %>
