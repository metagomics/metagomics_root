package org.uwpr.metagomics.webconstants;

import java.util.Collection;
import java.util.HashSet;

public class GOConstants {

	public static final Collection<String> GO_ASPECTS;
	
	static {
		GO_ASPECTS = new HashSet<>();

		GO_ASPECTS.add( "biological_process" );
		GO_ASPECTS.add( "cellular_component" );
		GO_ASPECTS.add( "molecular_function" );
	}
	
}
