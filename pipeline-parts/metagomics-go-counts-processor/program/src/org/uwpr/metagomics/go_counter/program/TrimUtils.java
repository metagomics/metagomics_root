package org.uwpr.metagomics.go_counter.program;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import org.uwpr.metaproteomics.emma.go.GONode;

public class TrimUtils {

	public static boolean meetsCutoffs( TwoRunGraphOb ob ) {
		
		if( ob.getLaplaceQvalue() > 0.01 ) { return false; }
		
		return true;		
	}
	
	/**
	 * Iteratively remove leaves from the data until all leaves meet the 
	 * log2 fold change and minimum psm count cutoffs.
	 * 
	 * @param data
	 * @return
	 */
	public static int trimTree( Map< String, TwoRunGraphOb > data ) throws Exception {

		System.out.println( "\t\t\tCalling trimTree..." );
		
		Set<String> leaves = getAllLeaves( data );
		int itemsRemoved = 0;
		
		for (Iterator<String> i = data.keySet().iterator(); i.hasNext();) {
			TwoRunGraphOb ob = data.get( i.next() );

		    if( leaves.contains( ob.getNode().getAcc() ) && !meetsCutoffs( ob ) ) {
		    	i.remove();
		    	itemsRemoved++;
		    }
		    
		}
		
		System.out.println( "\t\t\t\tRemoved " + itemsRemoved + " items..." );
		
		return itemsRemoved;
	}
	
	
	
	/**
	 * Given the supplied data, find all leaf nodes (nodes w/ no children)
	 * 
	 * @param data
	 * @return
	 */
	private static Set<String> getAllLeaves( Map< String, TwoRunGraphOb > data ) throws Exception {
		Set<String> leaves = new HashSet<>();

		Set<String> allParents = getAllParents( data );
		
		/*
		 * If an accession string isn't listed as a parent of any other
		 * node in the data, it must be a leaf node.
		 */
		for( String acc : data.keySet() ) {
			if( !allParents.contains( acc ) ) {
				leaves.add( acc );
			}			
		}		
		
		return leaves;
	}
	
	/**
	 * For the given data, find all nodes that are listed as parents of any node
	 * 
	 * @param data
	 * @return
	 */
	public static Set<String> getAllParents( Map< String, TwoRunGraphOb > data ) throws Exception {
		Set<String> allParents = new HashSet<>();

		for( String acc : data.keySet() ) {
			TwoRunGraphOb ob = data.get( acc );
			
			for( GONode parentNode : ob.getParents() ) {
				allParents.add( parentNode.getAcc() );
			}
			
		}
		
		
		return allParents;
	}
	
}
