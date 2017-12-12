
var validateFastaFileUploadForm = function() {
	
	var fastaFile = document.getElementById( "fasta-file" ).value;
	if( !fastaFile || fastaFile.length === 0 ) {
		alert( "Please select a FASTA file to upload." );
		return;
	}
	
	var nickname = document.getElementById( "nickname" ).value;
	if( !nickname || nickname.length === 0 ) {
		alert( "Please provide a nickname/brief description of your FASTA file." );
		return;
	}
	
	var blastCutoff = document.getElementById( "blast-cutoff" ).value;
	if( !blastCutoff || blastCutoff.length === 0 ) {
		alert( "Please enter a cutoff to use for Blast matches." );
		return;
	}
	
	if( !$.isNumeric( blastCutoff ) ) {
		alert( "Please enter a number for the blast cutoff." );
		return;
	}
	
	if( blastCutoff <= 0 ) {
		alert( "Please enter a number greater than 0 for the blast cutoff." );
		return;
	}
	
	var emailAddress = document.getElementById( "email-address" ).value;
	if( !emailAddress || emailAddress.length === 0 ) {
		alert( "Please enter an email address." );
		return;
	}
	
	if( !validateEmail( emailAddress ) ) {
		alert( "Please enter a valid email address." );
		return;
	}
	
	
	var form = document.getElementById( "fasta-upload-form" );
	form.submit();
	
}

// taken from stackoverflow: https://stackoverflow.com/questions/46155/how-to-validate-email-address-in-javascript
function validateEmail(email) {
    var re = /^(([^<>()\[\]\\.,;:\s@"]+(\.[^<>()\[\]\\.,;:\s@"]+)*)|(".+"))@((\[[0-9]{1,3}\.[0-9]{1,3}\.[0-9]{1,3}\.[0-9]{1,3}\])|(([a-zA-Z\-0-9]+\.)+[a-zA-Z]{2,}))$/;
    return re.test(email);
}
