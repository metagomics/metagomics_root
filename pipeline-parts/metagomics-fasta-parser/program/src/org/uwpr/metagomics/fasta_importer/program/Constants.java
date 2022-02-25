package org.uwpr.metagomics.fasta_importer.program;

import org.uwpr.metagomics.utils.OperatingSystemDetection;

public class Constants {
	
	//  Need trailing "/" or "\"
	public static final String UPLOAD_TEMP_DIRECTORY
			= OperatingSystemDetection.isWindows()
			? "F:\\tmp\\metagomics\\FASTA_processing"   // use this value if Windows
					: "/data/metagomics/FASTA_processing/";             // use this value if not Windows


	public static final String MARK_FASTA_COMPLETE_URL
	= OperatingSystemDetection.isWindows()
	? "http://localhost:8080/metagomics/services/fasta/markComplete"   // use this value if Windows
			: "http://meta.yeastrc.org/metagomics/services/fasta/markComplete";             // use this value if not Windows
	
}
