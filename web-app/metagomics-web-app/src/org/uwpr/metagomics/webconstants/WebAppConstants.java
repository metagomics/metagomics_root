package org.uwpr.metagomics.webconstants;

import java.util.HashMap;
import java.util.Map;

import org.uwpr.metagomics.webutils.OperatingSystemDetection;

public class WebAppConstants {

	public static final String DEMO_PROJECT_HASH = "BfAhNPhtBETdXVkB";
	
	//  Need trailing "/" or "\"
	public static final String UPLOAD_FASTA_TEMP_DIRECTORY
			= OperatingSystemDetection.isWindows()
			? "F:\\tmp\\metagomics\\FASTA_processing"   // use this value if Windows
					: "/data/metagomics/FASTA_processing/";             // use this value if not Windows


	//  Need trailing "/" or "\"
	public static final String UPLOAD_RUN_TEMP_DIRECTORY
			= OperatingSystemDetection.isWindows()
			? "F:\\tmp\\metagomics\\run_processing"   // use this value if Windows
					: "/data/metagomics/run_processing/";             // use this value if not Windows
	
	//  Need trailing "/" or "\"
	public static final String DOWNLOAD_FASTA_TO_RUN_BLAST_DIRECTORY
			= OperatingSystemDetection.isWindows()
			? "F:\\tmp\\metagomics\\blast_fasta"   // use this value if Windows
					: "/data/metagomics/blast_fasta/";             // use this value if not Windows


	//  Need trailing "/" or "\"
	public static final String UPLOAD_BLAST_RESULTS_DIRECTORY
			= OperatingSystemDetection.isWindows()
			? "F:\\tmp\\metagomics\\blast_results\\"   // use this value if Windows
					: "/data/metagomics/blast_results/";             // use this value if not Windows
	
	
	//  Need trailing "/" or "\"
	public static final String DATA_DOWNLOAD_DIRECTORY
			= OperatingSystemDetection.isWindows()
			? "F:\\tmp\\metagomics\\data_download\\"   // use this value if Windows
					: "/data/metagomics/data_download/";             // use this value if not Windows
	
	public static final String VIEW_UPLOADED_FASTA_FILE_URL = "https://www.yeastrc.org/metagomics/viewUploadedFasta.do";
	
	
	public static final Map<Integer, String> BLAST_DATABASE_DISPLAY_NAMES;
	public static final Map<Integer, String> BLAST_DATABASE_BLAST_NAMES;
	
	static {
		
		BLAST_DATABASE_DISPLAY_NAMES = new HashMap<>();
		BLAST_DATABASE_DISPLAY_NAMES.put( 1, "Uniprot sprot" );
		BLAST_DATABASE_DISPLAY_NAMES.put( 2, "Uniprot trembl" );
		BLAST_DATABASE_DISPLAY_NAMES.put( 3, "Uniprot sprot+trembl (bacteria only)" );
		
		
		BLAST_DATABASE_BLAST_NAMES = new HashMap<>();
		BLAST_DATABASE_BLAST_NAMES.put( 1, "uniprot_sprot.fasta" );
		BLAST_DATABASE_BLAST_NAMES.put( 2, "uniprot_trembl.fasta" );
		BLAST_DATABASE_BLAST_NAMES.put( 3, "uniprot-bacteria.fasta" );
	}
}
