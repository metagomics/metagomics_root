package org.uwpr.metaproteomics.emma.go;

import java.awt.Color;
import java.awt.image.BufferedImage;
import java.io.File;
import java.text.DecimalFormat;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;

import org.uwpr.metagomics.go_counter.program.SingleRunGraphOb;

import y.base.Node;
import y.io.JPGIOHandler;
import y.layout.OrientationLayouter;
import y.layout.hierarchic.HierarchicLayouter;
import y.view.Arrow;
import y.view.EdgeRealizer;
import y.view.Graph2D;
import y.view.Graph2DView;
import y.view.NodeLabel;
import y.view.PolyLineEdgeRealizer;
import y.view.ShapeNodeRealizer;
import yext.svg.io.SVGIOHandler;

public class SingleRunGOGraphGenerator {

	private String getLabel( SingleRunGraphOb ob ) {
		
		DecimalFormat df = new DecimalFormat("####0.##");
		
		String label = ob.getNode().getName() + " (" + ob.getNode().getAcc() + ")";
		
		label += "\n" + df.format( ob.getRatio() );
		label += "\n(" + ob.getCount() + ")";
		
		return label;
	}
	
	
	public void saveGOGraphSVGImage( Map<String, SingleRunGraphOb> data, File file ) throws Exception {
		Graph2D graph = new Graph2D();
		Map<String, Node> addedNodes = new HashMap<String, Node>();
		
		ShapeNodeRealizer realizer = new ShapeNodeRealizer();

		realizer.setShapeType(ShapeNodeRealizer.ROUND_RECT);
		
		NodeLabel nl = new NodeLabel();
		nl.setFontSize(12);
		realizer.setLabel(nl);		
		
		graph.setDefaultNodeRealizer(realizer);

		EdgeRealizer er = new PolyLineEdgeRealizer();
		er.setArrow(Arrow.SHORT);

		graph.setDefaultNodeRealizer(realizer);
		graph.setDefaultEdgeRealizer(er);
		
		Collection< String > gonodes = new HashSet<String>();
		gonodes.addAll( data.keySet() );

		
		Iterator<String> iter = gonodes.iterator();
		while (iter.hasNext()) {
			String acc = iter.next();
			SingleRunGraphOb ob = data.get( acc );
			

			this.modifyRealize( realizer, ob );
			
			
			Node node = null;
			if (addedNodes.containsKey( acc ) )
				node = (Node)(addedNodes.get( acc ));
			else {
				node = graph.createNode();

				String label = getLabel( ob );
				label = label.replaceAll(" ", "\n");
				
				graph.setLabelText(node, label);
				
				graph.setSize(node, graph.getLabelLayout(node)[0].getBox().width + 10, graph.getLabelLayout(node)[0].getBox().height + 5);
				
				addedNodes.put(acc, node);
			}
			
			if( ob.getParents() != null && ob.getParents().size() >= 1 ) {

				Collection< GONode > parents = ob.getParents();
				
				if (parents != null) {
					Iterator<GONode> piter = parents.iterator();
					while (piter.hasNext()) {
						
						GONode parentNode = piter.next();
						
						SingleRunGraphOb gparent = data.get( parentNode.getAcc() );
						Node parent = null;
	
						this.modifyRealize( realizer, gparent );
						
						if (addedNodes.containsKey( gparent.getNode().getAcc() ))
							parent = (Node)(addedNodes.get(gparent.getNode().getAcc()));
						else {
							parent = graph.createNode();
	
							String label = getLabel( gparent );
							label = label.replaceAll(" ", "\n");
						
							graph.setLabelText(parent, label);
						
							graph.setSize(parent, graph.getLabelLayout(parent)[0].getBox().width + 10, graph.getLabelLayout(parent)[0].getBox().height + 5);
						
							addedNodes.put(gparent.getNode().getAcc(), parent);
						}
					
						// Create the directed edge in the graph
						graph.createEdge(parent, node);
					}
				}
			}
		}
		
		y.layout.hierarchic.
		
		HierarchicLayouter layouter = new HierarchicLayouter();
		layouter.setOrientationLayouter(new OrientationLayouter(OrientationLayouter.TOP_TO_BOTTOM));
		layouter.setLayoutStyle(HierarchicLayouter.TREE);
		layouter.setRoutingStyle( HierarchicLayouter.ROUTE_POLYLINE );

		
		
	    layouter.doLayout(graph);
	    
	    
	    SVGIOHandler svg = new SVGIOHandler();
	    svg.write( graph, file.getAbsolutePath() );
	    
	}
	
	
	
	public BufferedImage getGOGraphImage( Map<String, SingleRunGraphOb> data ) throws Exception {
		Graph2D graph = new Graph2D();
		Map<String, Node> addedNodes = new HashMap<String, Node>();
		
		ShapeNodeRealizer realizer = new ShapeNodeRealizer();

		realizer.setShapeType(ShapeNodeRealizer.ROUND_RECT);
		
		NodeLabel nl = new NodeLabel();
		nl.setFontSize(12);
		realizer.setLabel(nl);		
		
		graph.setDefaultNodeRealizer(realizer);

		EdgeRealizer er = new PolyLineEdgeRealizer();
		er.setArrow(Arrow.SHORT);

		graph.setDefaultNodeRealizer(realizer);
		graph.setDefaultEdgeRealizer(er);
		
		Collection< String > gonodes = new HashSet<String>();
		gonodes.addAll( data.keySet() );

		
		Iterator<String> iter = gonodes.iterator();
		while (iter.hasNext()) {
			String acc = iter.next();
			SingleRunGraphOb ob = data.get( acc );
			

			this.modifyRealize( realizer, ob );
			
			
			Node node = null;
			if (addedNodes.containsKey( acc ) )
				node = (Node)(addedNodes.get( acc ));
			else {
				node = graph.createNode();

				String label = getLabel( ob );
				label = label.replaceAll(" ", "\n");
				
				graph.setLabelText(node, label);
				
				graph.setSize(node, graph.getLabelLayout(node)[0].getBox().width + 10, graph.getLabelLayout(node)[0].getBox().height + 5);
				
				addedNodes.put(acc, node);
			}
			
			if( ob.getParents() != null && ob.getParents().size() >= 1 ) {

				Collection< GONode > parents = ob.getParents();
				
				if (parents != null) {
					Iterator<GONode> piter = parents.iterator();
					while (piter.hasNext()) {
						
						GONode parentNode = piter.next();
						
						SingleRunGraphOb gparent = data.get( parentNode.getAcc() );
						Node parent = null;
	
						this.modifyRealize( realizer, gparent );
						
						if (addedNodes.containsKey( gparent.getNode().getAcc() ))
							parent = (Node)(addedNodes.get(gparent.getNode().getAcc()));
						else {
							parent = graph.createNode();
	
							String label = getLabel( gparent );
							label = label.replaceAll(" ", "\n");
						
							graph.setLabelText(parent, label);
						
							graph.setSize(parent, graph.getLabelLayout(parent)[0].getBox().width + 10, graph.getLabelLayout(parent)[0].getBox().height + 5);
						
							addedNodes.put(gparent.getNode().getAcc(), parent);
						}
					
						// Create the directed edge in the graph
						graph.createEdge(parent, node);
					}
				}
			}
		}
		
		//SmartOrganicLayouter layouter = new SmartOrganicLayouter();
		//layouter.setCompactness(0.25);
		//layouter.setQualityTimeRatio(1.0);
		//layouter.setMinimalNodeDistance(20);
		
		//BalloonLayouter layouter = new BalloonLayouter();
		
		y.layout.hierarchic.
		
		HierarchicLayouter layouter = new HierarchicLayouter();
		layouter.setOrientationLayouter(new OrientationLayouter(OrientationLayouter.TOP_TO_BOTTOM));
		layouter.setLayoutStyle(HierarchicLayouter.TREE);
		layouter.setRoutingStyle( HierarchicLayouter.ROUTE_POLYLINE );

		
		
		
		//OrganicLayouter layouter = new OrganicLayouter();
		//layouter.setOrientationLayouter(new OrientationLayouter(OrientationLayouter.LEFT_TO_RIGHT));
		
		
	    layouter.doLayout(graph);	    
	    
	    JPGIOHandler jpg = new JPGIOHandler();
	    jpg.setQuality((float)(7.0));
	    Graph2DView view = jpg.createDefaultGraph2DView(graph);
	    BufferedImage bi = (BufferedImage)(view.getImage());
		
		return bi;
	}
	
	private Color getColor( SingleRunGraphOb ob ) {
		int base = 210;

		
		int green = base;
		int blue = base;
		int red = base;	

		base = 255;
		int min = 50;
		
		green = base;
		blue = base;
		red = base;
				
		blue -= ( 255.0 * ob.getRatio() );
		green -= ( 255.0 * ob.getRatio() );
		red -= ( 255.0 * ob.getRatio() );
		
		if( blue < min ) { blue = min; }
		if( green < min ) { green = min; }
		if( red < min ) { red = min; }
		
		return new Color(red,green,blue);
		
	}

	
	private void modifyRealize( ShapeNodeRealizer realizer, SingleRunGraphOb ob ) {
		
		realizer.setFillColor( this.getColor( ob ) );
		realizer.setShapeType( ShapeNodeRealizer.ROUND_RECT );
		
		//realizer.setShapeType( ShapeNodeRealizer.ROUND_RECT );
			
		NodeLabel nl = new NodeLabel();
		nl.setFontSize(11);
		realizer.setLabel(nl);
			
	}
	
}
