var _UPLOADED_FASTA_FILE;

function loadUploadedFastaFile( uniqueId ) {
	
	console.log( "Loading uploaded fasta file." );
			
	var url = contextPathJSVar + "/services/fastaFile/getData?unique_id=" + uniqueId;
			
	// var request = 
	$.ajax({
		type: "GET",
		url: url,
		dataType: "json",
		success: function(data)	{
			//try {
			    
				if( !data ) {
					console.log( "Got no data..." );
					handleAJAXFailure( "Got no data..." );
				}
				
				_UPLOADED_FASTA_FILE = data;
				console.log( _UPLOADED_FASTA_FILE );
				
				updatePageContent();
			        		
			/*} catch( e ) {
				reportWebErrorToServer.reportErrorObjectToServer( { errorException : e } );
				throw e;
			}*/
		},
		
		failure: function(errMsg) {
			handleAJAXFailure( errMsg );
		},
		error: function(jqXHR, textStatus, errorThrown) {	
			handleAJAXError( jqXHR, textStatus, errorThrown );							
		}
	});	
}


function loadRunData() {
	
	console.log( "Loading run data." );
			
	var url = contextPathJSVar + "/services/fastaFile/getRuns?unique_id=" + _UPLOADED_FASTA_FILE.uniqueId;
			
	// var request = 
	$.ajax({
		type: "GET",
		url: url,
		dataType: "json",
		success: function(data)	{
			//try {
			    
				if( !data ) {
					console.log( "Got no data..." );
					handleAJAXFailure( "Got no data..." );
				}
				
				_UPLOADED_FASTA_FILE.runs = data;
				console.log( _UPLOADED_FASTA_FILE );
				
				showRuns();
				
			/*} catch( e ) {
				reportWebErrorToServer.reportErrorObjectToServer( { errorException : e } );
				throw e;
			}*/
		},
		
		failure: function(errMsg) {
			handleAJAXFailure( errMsg );
		},
		error: function(jqXHR, textStatus, errorThrown) {	
			handleAJAXError( jqXHR, textStatus, errorThrown );							
		}
	});	
}


function checkFilesExistSingleRun( runId ) {

	console.log( "Checking files exist single run." );
	
	var url = contextPathJSVar + "/services/files/existSingleRun?uniqueId=" + _UPLOADED_FASTA_FILE.uniqueId;
	url += "&runId=" + runId;
			
	// var request = 
	$.ajax({
		type: "GET",
		url: url,
		dataType: "json",
		success: function(data)	{
			//try {
			    
				if( !data ) {
					console.log( "Got no data..." );
					handleAJAXFailure( "Got no data..." );
				}
				
				console.log( data );
				
				showDownloadSingleRunOverlay( data, runId );
				
			/*} catch( e ) {
				reportWebErrorToServer.reportErrorObjectToServer( { errorException : e } );
				throw e;
			}*/
		},
		
		failure: function(errMsg) {
			handleAJAXFailure( errMsg );
		},
		error: function(jqXHR, textStatus, errorThrown) {	
			handleAJAXError( jqXHR, textStatus, errorThrown );							
		}
	});	
}



function checkFilesExistTwoRuns( run1Id, run2Id ) {

	console.log( "Checking files exist single run." );
	
	var url = contextPathJSVar + "/services/files/existTwoRuns?uniqueId=" + _UPLOADED_FASTA_FILE.uniqueId;
	url += "&run1Id=" + run1Id;
	url += "&run2Id=" + run2Id;
			
	// var request = 
	$.ajax({
		type: "GET",
		url: url,
		dataType: "json",
		success: function(data)	{
			//try {
			    
				if( !data ) {
					console.log( "Got no data..." );
					handleAJAXFailure( "Got no data..." );
				}
				
				console.log( data );
				
				showDownloadTwoRunsOverlay( data, run1Id, run2Id );
				
			/*} catch( e ) {
				reportWebErrorToServer.reportErrorObjectToServer( { errorException : e } );
				throw e;
			}*/
		},
		
		failure: function(errMsg) {
			handleAJAXFailure( errMsg );
		},
		error: function(jqXHR, textStatus, errorThrown) {	
			handleAJAXError( jqXHR, textStatus, errorThrown );							
		}
	});	
}




function hideDownloadSingleRunOverlay() {
	$('.upload-overlay-background').hide();
	$('.download-single-run-overlay-content').hide();
}

function performTwoRunDownload() {
	var runs = getCheckedRuns();
	console.log( runs );
	
	if( runs.length != 2 ) {
		alert( "You must select exactly two runs to compare." );
		return;
	}
	
	checkFilesExistTwoRuns( runs[ 0 ], runs[ 1 ] );
	
}

function showDownloadTwoRunsOverlay( data, run1Id, run2Id ) {
	
	/*
	 * Two run report download:
	 * ${ contextPath }/downloadTwoRunData?type=report&uniqueId=#UNIQUE_ID#&run1Id=#RUN_ID&run2Id=#RUN_ID
	 */
	
	/*
	 * Two run image download:
	 * ${ contextPath }/downloadTwoRunData?type=image&format=png&aspect=biological_process&uniqueId=#UNIQUE_ID#&run1Id=#RUN_ID&run2Id=#RUN_ID
	 */
	
	
	
	
	var $reportLink = $('#report-link');
	var $taxonomyReportLink = $('#taxonomy-report-link' );

	disableLink( $taxonomyReportLink );	
	
	
	if( data.reportExists ) {
		enableLink( $reportLink );
		
		$reportLink.attr("href", contextPathJSVar + "/downloadTwoRunData.do?type=report&uniqueId=" + _UPLOADED_FASTA_FILE.uniqueId + "&run1Id=" + run1Id + "&run2Id=" + run2Id );
	} else {
		disableLink( $reportLink );	
	}

	
	var aspects = [ "biological_process", "cellular_component", "molecular_function" ];
	var formats = [ "png", "svg" ];
	
	for( var i = 0; i < aspects.length; i++ ) {
		var aspect = aspects[ i ];
		
		for( var k = 0; k < formats.length; k++ ) {
			var format = formats[ k ];
			
			var $imageLink = $( "#" + aspect + "-image-" + format );
			console.log( "$imageLink:" );
			console.log( $imageLink );
			
			if( !data.imageExists || !data.imageExists[ aspect ] || !data.imageExists[ aspect ][ format ] ) {
				disableLink( $imageLink );
			} else {
				enableLink( $imageLink );
				$imageLink.attr("href", contextPathJSVar + "/downloadTwoRunData.do?type=image&format=" + format + "&aspect=" + aspect + "&uniqueId=" + _UPLOADED_FASTA_FILE.uniqueId + "&run1Id=" + run1Id + "&run2Id=" + run2Id );
			}
			
		}
	}
	
	
	$('.upload-overlay-background').show();
	$('.download-single-run-overlay-content').show();
	
	
}


function showDownloadSingleRunOverlay( data, runId ) {
	
	/*
	 * Single run report download:
	 * ${ contextPath }/downloadSingleRunData?type=report&uniqueId=#UNIQUE_ID#&runId=#RUN_ID
	 */
	
	/*
	 * Single run image download:
	 * ${ contextPath }/downloadSingleRunData?type=image&format=png&aspect=biological_process&uniqueId=#UNIQUE_ID#&runId=#RUN_ID
	 */
	
	
	
	
	var $reportLink = $('#report-link');
	var $taxonomyReportLink = $('#taxonomy-report-link' );
	
	if( data.reportExists ) {
		enableLink( $reportLink );
		
		$reportLink.attr("href", contextPathJSVar + "/downloadSingleRunData.do?type=report&uniqueId=" + _UPLOADED_FASTA_FILE.uniqueId + "&runId=" + runId );
	} else {
		disableLink( $reportLink );	
	}
	
	if( data.taxonomyReportExists ) {
		enableLink( $taxonomyReportLink );
		
		$taxonomyReportLink.attr("href", contextPathJSVar + "/downloadSingleRunData.do?type=taxonomy_report&uniqueId=" + _UPLOADED_FASTA_FILE.uniqueId + "&runId=" + runId );
	} else {
		disableLink( $taxonomyReportLink );	
	}

	
	var aspects = [ "biological_process", "cellular_component", "molecular_function" ];
	var formats = [ "png", "svg" ];
	
	for( var i = 0; i < aspects.length; i++ ) {
		var aspect = aspects[ i ];
		
		for( var k = 0; k < formats.length; k++ ) {
			var format = formats[ k ];
			
			var $imageLink = $( "#" + aspect + "-image-" + format );
			console.log( "$imageLink:" );
			console.log( $imageLink );
			
			if( !data.imageExists || !data.imageExists[ aspect ] || !data.imageExists[ aspect ][ format ] ) {
				disableLink( $imageLink );
			} else {
				enableLink( $imageLink );
				$imageLink.attr("href", contextPathJSVar + "/downloadSingleRunData.do?type=image&format=" + format + "&aspect=" + aspect + "&uniqueId=" + _UPLOADED_FASTA_FILE.uniqueId + "&runId=" + runId );
			}
			
		}
	}
	
	
	$('.upload-overlay-background').show();
	$('.download-single-run-overlay-content').show();
	
	
}


function disableLink( $link ) {
	$link.css('textDecoration','none');
	$link.css('opacity','0.3');
	
	$link.click(function () {return false;});

	$link.attr("href", "")	
}

function enableLink( $link ) {
	$link.css('textDecoration','underline');
	$link.css('opacity','1.0');
	
	$link.unbind('click');
}


function showUploadOverlay() {

	$overlayBg = $('div.upload-overlay-background');
	$overlayContent = $('div.upload-overlay-content');
	
	$overlayBg.show();
	$overlayContent.show();
	
}

function hideUploadOverlay() {
	
	$overlayBg = $('div.upload-overlay-background');
	$overlayContent = $('div.upload-overlay-content');
	
	$overlayBg.hide();
	$overlayContent.hide();
}






function uploadRunFile() {
	
	// get our file
	var file = document.getElementById("run-file-field").files[ 0 ];
	var nickname = $("#run-file-nickname").val();
	
	if( !file ) {
		alert( "Must choose a file." );
		return;
	}

	if ( file.size < 1 ) {
		alert( "File is empty." );
		return;
	}
	
	if( !nickname ) {
		alert( "Must enter a nickname." );
		return;
	}

	
	
	var formData = new FormData();
	formData.append( 'dataFile', file, file.name );
	formData.append( 'nickname', nickname );
	formData.append( 'uniqueId', _UPLOADED_FASTA_FILE.uniqueId );
	
	var xhr = new XMLHttpRequest();
	xhr.open('POST', contextPathJSVar + '/uploadRun.do', true);

    xhr.onload = function() {

    	try {

    		if (xhr.status === 200) {

    			var xhrResponse = xhr.response;


    			var resp = null;

    			try {
    				resp = JSON.parse(xhrResponse);

    				if ( resp.message === "success" ) {


    				} else {

    					alert("File Upload failed. Failed to determine error reason.");

    					throw 'Unknown error occurred: [' + xhr.responseText + ']';
    				}

    			} catch (e) {

    				alert("File Upload failed. Failed to get information from server response.");

    				throw 'Unknown error occurred: [' + xhr.responseText + ']';
    			}



    			
    			hideUploadOverlay();
    			loadRunData();

    		} else {

    			console.log( xhr );
    			
    			handleAJAXError( xhr );


    		}
    		
    	} catch( e ) {
    		console.log( e );
    		//reportWebErrorToServer.reportErrorObjectToServer( { errorException : e } );
    		throw e;
    	}
    };
    
    xhr.send(formData);

}






function getCheckedRuns() {
	
	var $runCheckBoxes = $( '.run-check-box' );
	var runs = [ ];
	
	$runCheckBoxes.each( function( index ) {
		if( $( this ).is( ":checked" ) ) {
			runs.push( $( this ).val() );
		}
	});
	
	return runs;
}







function updatePageContent() {
	
	$('#fasta-upload-overview').show();
	
	$('#upload-nickname').text( _UPLOADED_FASTA_FILE.nickname );
	$('#upload-fasta-filename').text( "(" + _UPLOADED_FASTA_FILE.fastaFile.filename + ")" );
	
	$('#annotation-database-name').text( _UPLOADED_FASTA_FILE.annotationDatabaseName );
	$('#blast-cutoff').text( _UPLOADED_FASTA_FILE.blastCutoff );

	if( _UPLOADED_FASTA_FILE.useTopHit ) {
		$('#use-top-hit').text( "Use top hit only" );
	} else {
		$('#use-top-hit').text( "Use all hits" );
	}
	
	if( _UPLOADED_FASTA_FILE.fastaFile.processed ) {
		console.log( "Load the \"is processed\" page." );
		loadIsProcessedPage();
	} else {
		console.log( "Load the \"not processed\" page." );
		loadNotProcessedPage();
	}
}

function showRuns () {
	
	// if there are no runs, show that
	if( !_UPLOADED_FASTA_FILE.runs || _UPLOADED_FASTA_FILE.runs.length < 1 ) {
		console.log( _UPLOADED_FASTA_FILE.runs.length );
		$('#no-run-data').show();
		return;
	}
	
	$dataDiv = $('#have-run-data');
	$table = $('#run-data-table');
	
	$table.empty();
		
	for( var i = 0; i < _UPLOADED_FASTA_FILE.runs.length; i++ ) {
		
		var run = _UPLOADED_FASTA_FILE.runs[ i ];
		
		var html = "<tr>";
		
		html += "<td class=\"checkbox-cell\">";
		
		if( run.processed ) {
			html += "<input class=\"run-check-box\" type=\"checkbox\" name=\"checked-run\" value=\"" + run.id + "\">";
		} else {
			html += "&nbsp;"
		}		
		html += "</td>";
		
		html += "<td class=\"nickname-cell\">";
		html += run.nickname;		
		html += "</td>";
		
		
		
		html += "<td class=\"status-cell\">";
		
		if( run.processed ) {
			html += "<input onClick=\"javascript:checkFilesExistSingleRun(" + run.id + ")\" class=\"view-go-button\" type=\"button\" value=\"Download GO Analysis\">";
		} else {
			html += "Queued for processing"
		}		
		html += "</td>";
		
		html += "<td class=\"date-cell\">";
		html += "Updated: " + run.uploadDate;		
		html += "</td>";
		
		
		html += "</tr>";
				
		var $newRow = $.parseHTML( html );
				
		
		$table.append( $newRow );
		
		
	}
	
	$dataDiv.show();
	
}


function loadIsProcessedPage() {
	
	$('#fasta-is-processed').show();
	
	loadRunData();
	
}


function loadNotProcessedPage() {
	
	var $el = $('.not-processed-text');
	
	var txt = $el.text();
	
	txt = txt.replace( "#EMAIL#", _UPLOADED_FASTA_FILE.email );
	$el.text( txt );
	
	
	$('#fasta-not-processed').show();
}


function initPage() {
	
	var uniqueId = $('#hidden-unique-id-div').html();
	if( !uniqueId ) {
		throw new Error( "Could not find unique id." );
	}
	
	
	loadUploadedFastaFile( uniqueId );
	
}


$(document).ready(function()  { 
	
	try {
		initPage();

	} catch( e ) {
		reportWebErrorToServer.reportErrorObjectToServer( { errorException : e } );
		throw e;
	}
});