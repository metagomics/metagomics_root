package org.uwpr.metaproteomics.emma.go;

public class GOUtils {

	public static String BIOLOGICAL_PROCESS = "P";
	public static String CELLULAR_COMPONENT = "C";
	public static String MOLECULAR_FUNCTION = "F";
	
	public static String UNKNOWN_PROCESS_ACC = "unknownprc";
	public static String UNKNOWN_FUNCTION_ACC = "unknownfun";
	public static String UNKNOWN_COMPONENT_ACC = "unknowncmp";
	
	public static int UNKNOWN_PROCESS_ID = -1;
	public static int UNKNOWN_FUNCTION_ID = -2;
	public static int UNKNOWN_COMPONENT_ID = -3;

	
	public static String getAspect( String aspect_string ) {
		if( aspect_string.equals( "biological_process" ) ) return "P";
		if( aspect_string.equals( "molecular_function" ) ) return "F";
		if( aspect_string.equals( "cellular_component" ) ) return "C";
		
		return "unknown";
	}
	
	public static GONode getAllNode() throws Exception {
		if( allNode == null )
			allNode = GONodeFactory.getInstance().getGONode( "all" );
		
		return allNode;
	}
	
	public static GONode getAspectRootNode( String aspect ) throws Exception {

		GONode allNode = getAllNode();
		
		for( GONode child : GOSearchUtils.getDirectChildNodes( allNode ) ) {
			//System.err.println( child.getAcc() + " : " + child.getName() + " : " + child.getTermType() );
			
			if( child.getName().equals( aspect ) ) {
				return child;
			}
		}
		
		throw new Exception( "Could not find aspect root node for aspect: " + aspect );
		
	}
	
	public static GONode getUnknownNodeForAspect( String aspect ) throws Exception {
		
		if( aspect.equals( "biological_process" ) ) return GONodeFactory.NODE_UNKNOWN_BIOLOGICAL_PROCESS;
		if( aspect.equals( "cellular_component" ) ) return GONodeFactory.NODE_UNKNOWN_CELLULAR_COMPONENT;
		if( aspect.equals( "molecular_function" ) ) return GONodeFactory.NODE_UNKNOWN_MOLECULAR_FUNCTION;
		
		else throw new Exception( "Unknown aspect: " + aspect );
		
	}
	
	private static GONode allNode;
}
