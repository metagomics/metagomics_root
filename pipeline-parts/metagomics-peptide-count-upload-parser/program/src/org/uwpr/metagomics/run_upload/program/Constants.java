package org.uwpr.metagomics.run_upload.program;

import java.util.HashMap;
import java.util.Map;

import org.uwpr.metagomics.utils.OperatingSystemDetection;

public class Constants {
	
	// the number of protein sequences to get from the database per iteration when
	// finding proteins that contain peptides for a fasta file
	public static final int NUMBER_PROTEIN_SEQUENCES_PER_ITERATION = 2500;
	
	//  Need trailing "/" or "\"
	public static final String UPLOAD_RUN_TEMP_DIRECTORY
			= OperatingSystemDetection.isWindows()
			? "F:\\tmp\\metagomics\\run_processing"   // use this value if Windows
					: "/data/metagomics/run_processing/";             // use this value if not Windows

	//  Need trailing "/" or "\"
	public static final String UPLOAD_BLAST_FASTA_TEMP_DIRECTORY
			= OperatingSystemDetection.isWindows()
			? "F:\\tmp\\metagomics\\blast_fasta"   // use this value if Windows
					: "/data/metagomics/blast_fasta/";             // use this value if not Windows

	
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
