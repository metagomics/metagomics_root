package org.uwpr.metagomics.blast_parser.program;

import org.jobcenter.util.OperatingSystemDetection;

public class Constants {
	
	//  Need trailing "/" or "\"
	public static final String BLAST_RESULTS_FOLDER
			= OperatingSystemDetection.isWindows()
			? "F:\\tmp\\metagomics\\blast_results\\"   // use this value if Windows
					: "/data/metagomics/blast_results/";             // use this value if not Windows


}
