<%@ include file="/WEB-INF/jsp-includes/header_main.jsp" %>

<div class="overall-enclosing-block home-page">

	<form id="fasta-upload-form" action="uploadFASTA.do" method="POST" enctype="multipart/form-data">


	<div class="header-item">1. Upload a meta-proteome FASTA file</div>
	<div class="header-item-explain">The unannotated meta-proteome FASTA file against which you performed your MS/MS searches.</div>
	
	<div class="form-item">
		<input id="fasta-file" type="file" name="fastaFile" style="font-size:19pt;" />
	</div>

	<div class="form-item">
		Nickname: <input id="nickname" type="text" name="nickname" style="font-size:19pt;background-color:#E8E8E8;border-style:solid;border-color:black;border-width:2px;" />
	</div>
	<div class="header-item-explain">
		A short description of this file, e.g. &quotAntarctic run-off proteome 2017&quot;
	</div>
	
	

	<div class="header-item non-top">2. Select GO annotation BLAST database</div>
	<div class="header-item-explain">
		This is the database against which protein sequences from your meta-proteome will be BLASTed to obtain Gene Ontology annotations.
	</div>


	<div class="form-item">
	Uniprot database: <input type="radio" name="annotationDatabase" style="font-size:19pt;" value="1" checked></input> Uniprot sprot
					<input type="radio" name="annotationDatabase" style="font-size:19pt;" value="2"></input> Uniprot trembl
					<input type="radio" name="annotationDatabase" style="font-size:19pt;" value="3"></input> Uniprot sprot+trembl (bacterial only)
	</div>


	<div class="header-item non-top">3. Choose how to use BLAST hits</div>
	<div class="header-item-explain">
		Choose the BLAST e-value cutoff, and whether to use GO annotations for all matches meeting the cutoff or only the top hit.
	</div>

	<div style="margin-top:20px">
		<div class="form-item" style="display:inline;">
			Blast e-value cutoff: <input id="blast-cutoff" value="1E-10" type="text" name="cutoff" style="font-size:19pt;background-color:#E8E8E8;border-style:solid;border-color:black;border-width:2px;" />
		</div>
	
		<div class="form-item" style="display:inline;">
			<input type="checkbox" name="useTopHit" style="font-size:19pt;" checked></input>Use only top hit? 
		</div>
	</div>
	
	
	<div class="header-item non-top">4. Your email address</div>
	<div class="header-item-explain">
		Used only to send your links to your data when it's ready to view
	</div>
	
	<div class="form-item">
		Email address: <input id="email-address" type="text" name="emailAddress" style="font-size:19pt;background-color:#E8E8E8;border-style:solid;border-color:black;border-width:2px;" />
	</div>
	
	<input value="Upload FASTA File" type="button" onClick="javascript:validateFastaFileUploadForm()" style="font-size:33pt;background-color:#E8E8E8;border-style:solid;border-color:black;border-width:2px;margin-left:50px;margin-top:20px;"></input>
	
	</form>

</div>

<%@ include file="/WEB-INF/jsp-includes/footer_main.jsp" %>
