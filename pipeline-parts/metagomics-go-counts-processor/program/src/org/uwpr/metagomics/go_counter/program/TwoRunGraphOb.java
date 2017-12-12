package org.uwpr.metagomics.go_counter.program;

import java.util.Collection;

import org.uwpr.metagomics.dto.RunDTO;
import org.uwpr.metaproteomics.emma.go.GONode;
import org.uwpr.metaproteomics.emma.go.GOSearchUtils;

public class TwoRunGraphOb {


	private SingleRunGraphOb ob1;
	private SingleRunGraphOb ob2;
	private RunDTO run1;
	private RunDTO run2;
	private Collection<GONode> parents;
	private double pvalue;
	private double pvalue_corr;
	private double laplaceRatio1;
	private double laplaceRatio2;
	private double laplaceLogChange;
	private double laplacePvalue;
	private double laplacePvalue_corr;
	private double laplaceQvalue;
	private GONode node;
	private Double logChange;
	
	public SingleRunGraphOb getOb1() {
		return ob1;
	}
	public void setOb1(SingleRunGraphOb ob1) {
		this.ob1 = ob1;
	}
	public SingleRunGraphOb getOb2() {
		return ob2;
	}
	public void setOb2(SingleRunGraphOb ob2) {
		this.ob2 = ob2;
	}
	public RunDTO getRun1() {
		return run1;
	}
	public void setRun1(RunDTO run1) {
		this.run1 = run1;
	}
	public RunDTO getRun2() {
		return run2;
	}
	public void setRun2(RunDTO run2) {
		this.run2 = run2;
	}
	public Collection<GONode> getParents() throws Exception {
		
		if( this.parents == null )
			this.parents = GOSearchUtils.getDirectParentNodes( this.getNode() );
		
		return parents;
	}
	public double getPvalue() {
		return pvalue;
	}
	public void setPvalue(double pvalue) {
		this.pvalue = pvalue;
	}
	public double getPvalue_corr() {
		return pvalue_corr;
	}
	public void setPvalue_corr(double pvalue_corr) {
		this.pvalue_corr = pvalue_corr;
	}
	public double getLaplaceRatio1() {
		return laplaceRatio1;
	}
	public void setLaplaceRatio1(double laplaceRatio1) {
		this.laplaceRatio1 = laplaceRatio1;
	}
	public double getLaplaceRatio2() {
		return laplaceRatio2;
	}
	public void setLaplaceRatio2(double laplaceRatio2) {
		this.laplaceRatio2 = laplaceRatio2;
	}
	public double getLaplaceLogChange() {
		return laplaceLogChange;
	}
	public void setLaplaceLogChange(double laplaceLogChange) {
		this.laplaceLogChange = laplaceLogChange;
	}
	public double getLaplacePvalue() {
		return laplacePvalue;
	}
	public void setLaplacePvalue(double laplacePvalue) {
		this.laplacePvalue = laplacePvalue;
	}
	public double getLaplacePvalue_corr() {
		return laplacePvalue_corr;
	}
	public void setLaplacePvalue_corr(double laplacePvalue_corr) {
		this.laplacePvalue_corr = laplacePvalue_corr;
	}
	public GONode getNode() {
		return node;
	}
	public void setNode(GONode node) {
		this.node = node;
	}
	public Double getLogChange() {
		return logChange;
	}
	public void setLogChange(Double logChange) {
		this.logChange = logChange;
	}
	public double getLaplaceQvalue() {
		return laplaceQvalue;
	}
	public void setLaplaceQvalue(double laplaceQvalue) {
		this.laplaceQvalue = laplaceQvalue;
	}
	
	
	
	
	
}
