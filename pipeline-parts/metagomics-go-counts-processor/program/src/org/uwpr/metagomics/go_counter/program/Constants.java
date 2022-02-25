package org.uwpr.metagomics.go_counter.program;

public class Constants {

	//  Need trailing "/" or "\"
	public static final String UPLOAD_RUN_TEMP_DIRECTORY
			= OperatingSystemDetection.isWindows()
			? "F:\\tmp\\metagomics\\run_processing"   // use this value if Windows
					: "/data/metagomics/run_processing/";             // use this value if not Windows
	
	public static final String MARK_RUN_COMPLETE_URL
	= OperatingSystemDetection.isWindows()
	? "http://localhost:8080/metagomics/services/run/markComplete"   // use this value if Windows
			: "http://meta.yeastrc.org/metagomics/services/run/markComplete";             // use this value if not Windows
	
	
	public static final String DATA_DOWNLOAD_DIRECTORY
	= OperatingSystemDetection.isWindows()
	? "F:\\tmp\\metagomics\\data_download"   // use this value if Windows
			: "/data/metagomics/data_download/";             // use this value if not Windows
	
}
