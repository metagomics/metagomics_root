package org.uwpr.metagomics.go_counter.program;

import java.util.Collection;

import org.uwpr.metaproteomics.emma.go.GONode;
import org.uwpr.metaproteomics.emma.go.GOSearchUtils;

public class SingleRunGraphOb {

	private double ratio;
	private int count;
	private GONode node;
	private Collection<GONode> parents;
	private double runPsmTotal;
	
	public double getRatio() {
		return ratio;
	}
	public void setRatio(double ratio) {
		this.ratio = ratio;
	}
	public int getCount() {
		return count;
	}
	public void setCount(int count) {
		this.count = count;
	}
	public GONode getNode() {
		return node;
	}
	public void setNode(GONode node) {
		this.node = node;
	}
	public Collection<GONode> getParents() throws Exception {
		
		if( this.parents == null )
			this.parents = GOSearchUtils.getDirectParentNodes( this.getNode() );
		
		return parents;
	}
	public double getRunPsmTotal() {
		return runPsmTotal;
	}
	public void setRunPsmTotal(double runPsmTotal) {
		this.runPsmTotal = runPsmTotal;
	}

	
	
	
	
	
}
