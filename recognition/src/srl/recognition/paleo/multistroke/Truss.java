/**
 * Date Created: Mar 15, 2013
 */
package srl.recognition.paleo.multistroke;

import java.util.ArrayList;
import java.util.List;

import srl.core.sketch.IBeautifiable;
import srl.core.sketch.IShape;
import srl.core.sketch.Interpretation;
import srl.core.sketch.Point;
import srl.core.sketch.Shape;

/**
 * This class manages the entire Truss object.
 * Every truss contains an ArrayList of beams, nodes, drawnNodes, and forces and contains a variable for error_margin.
 * The truss itself is composed of beams and nodes. The forces array may eventually be included as part of a truss object
 * or may be placed outside in a wrapper class like FBD. drawnNodes is an ArrayList that may be used to specify if the
 * user explicitly draws a node on the canvas. error_margin is a decimal value that is used to indicate how much tolerance
 * the user is allowed when not actually connecting their drawn line endpoints together.
 * @author danieltan
 * Date Created: Mar 15, 2013
 */
public class Truss extends Shape{
	/**
	 * 
	 */
	private static final long serialVersionUID = -1683177586062715394L;
	private ArrayList<IShape> beams;
	private ArrayList<NewNode> nodes;
	private double error_margin = 0.1; // .001 ==> You have NOT drawn a valid truss..
	private double conf = 1.0;

	//********************************
	//
	//
	//		   Constructors:
	//
	//
	//********************************

	/**
	 * Default Constructor, creates empty ArrayLists for beams and forces
	 *
	 */
	public Truss(){
		beams = new ArrayList<IShape>();
		nodes= new ArrayList<NewNode>();
	}

	//********************************
	//
	//
	//		  Parsing Functions:
	//
	//
	//********************************

	/**
	 * Takes ArrayLists of SRL_Lines and SRL_Points and adds the lines and
	 * points to the private ArrayLists beams and drawnNodes
	 * provided those lines and nodes don't already exist in beams and drawnNodes
	 * AND that those lines and nodes fulfill the rules for a Truss
	 * @author danieltan
	 * @param lineList ArrayList<SRL_Line> pointList ArrayList<SRL_Point>
	 */
	public void belongsToTruss(List<Shape> lineList) {
		
		//ensure each line has both its end points connected to another line
		for (int i=0; i<lineList.size(); i++) {
			IBeautifiable s = (IBeautifiable)(lineList.get(i));
			org.openawt.svg.SVGLine shape = (org.openawt.svg.SVGLine)s.getBeautifiedShape();
			 org.openawt.geom.Line2D line2d = (org.openawt.geom.Line2D)shape.getShape();
			//System.out.println("lineList["+i+"]: ("+lineList.get(i).getP1().getX()+","+lineList.get(i).getP1().getY()+")");
			//System.out.println("lineList["+i+"]: ("+lineList.get(i).getP2().getX()+","+lineList.get(i).getP2().getY()+")");
			boolean found = false;
			if (found == false) {
				//System.out.println("found == false");
				int testP1 = -1;
				int testP2 = -1;
				NewNode temp1 = null;
				NewNode temp2 = null;
				//System.out.println("**********************");
				//System.out.println("start beam for loop");
				NewNode beamNodeP1 = null;
				NewNode beamNodeP2 = null;
				for (int j=0; j<beams.size(); j++) { //compare with existing beams
					//System.out.println("beams["+j+"]: P1 ("+((Beam)beams.get(j)).getP1().getX()+","+((Beam)beams.get(j)).getP1().getY()+")");
					//System.out.println("beams["+j+"]: P2 ("+((Beam)beams.get(j)).getP2().getX()+","+((Beam)beams.get(j)).getP2().getY()+")");
					
					temp1 = evaluatePointBeamConnection(j, new Point(line2d.getX1(), line2d.getY1()));
					temp2 = evaluatePointBeamConnection(j, new Point(line2d.getX2(), line2d.getY2()));
					if (temp1 != null){
						testP1 = 0;
						beamNodeP1 = temp1;
					}
					if (temp2 != null){
						testP2 = 0;
						beamNodeP2 = temp2;
					}
				}

				//System.out.println("Finished checking beam list");
				for (int j=i+1; j<lineList.size(); j++) { //compare with remaining SRL_Lines
					//System.out.println("comparing:");
					if (evaluatePointLineConnection(lineList.get(j), new Point(line2d.getX1(), line2d.getY1())) == 0 && testP1 != 0){
						testP1 = 0;
					}
					if (evaluatePointLineConnection(lineList.get(j), new Point(line2d.getX2(), line2d.getY2())) == 0 && testP2 != 0){
						testP2 = 0;
					}
					if (testP1 == 0 && testP2 == 0) { // the 2 lines are the same line2d, skip this line2d
						break;
					}
				}
				//System.out.println("Finished checking line2d list");
				if (testP1 == 0 && testP2 == 0) { // line2d i shares both end points with another line2d
					//System.out.println("Adding a line2d to beam list");

					//System.out.println("P1: ("+line2d.getX1()+","+line2d.getY1()+")");
					//System.out.println("cast P1: ("+(int)line2d.getX1()+","+(int)line2d.getY1()+")");
					//System.out.println("P2: ("+line2d.getX2()+","+line2d.getY2()+")");
					//System.out.println("cast P2: ("+(int)line2d.getX2()+","+(int)line2d.getY2()+")");

					ArrayList<NewNode> line = new ArrayList<NewNode>();
					NewNode newNode1 = new NewNode();
					NewNode newNode2 = new NewNode();
					//newNode1.setId((UUID)(new Date()).getTime());
					newNode1.setX((int)line2d.getX1());
					newNode1.setY((int)line2d.getY1());

					newNode2.setX((int)line2d.getX2());
					newNode2.setY((int)line2d.getY2());
					//newNode2.setId((int)(new Date()).getTime());

					if (beamNodeP1 != null && beamNodeP2 != null){
						//System.out.println("Shares two nodes");
						line.add(beamNodeP1);
						line.add(beamNodeP2);
					} else if (beamNodeP1 != null){
						//System.out.println("Shares with one beam");
						line.add(beamNodeP1);
						line.add(newNode2);

						nodes.add(newNode2);
					} else if (beamNodeP2 != null){
						//System.out.println("Shares with one beam");
						line.add(newNode1);
						line.add(beamNodeP2);

						nodes.add(newNode1);
					} else {
						//System.out.println("Shares with no beams");
						line.add(newNode1);
						line.add(newNode2);

						nodes.add(newNode1);
						nodes.add(newNode2);
					}

					Beam newBeam = new Beam(line);

					//System.out.println("Adding P1 = ("+newNode1.getX()+", "+newNode1.getY()+")");
					//System.out.println("Adding P2 = ("+newNode2.getX()+", "+newNode2.getY()+")");
					addBeam(newBeam);
				}
			}
		}
	}

	/**
	 * Evaluate if a point belongs to a line
	 * a point belongs to a line IF the euclidean distance
	 * between the point and a line's endpoint are within the error_margin
	 * @author danieltan
	 * @param SRL_Line line, SRL_Point point
	 * @return 0 for success; -1 if the point does not belong to the line
	 */
	private int evaluatePointLineConnection(Shape line, Point point){
		

		IBeautifiable s = (IBeautifiable)(line);
		org.openawt.svg.SVGLine shape = (org.openawt.svg.SVGLine)s.getBeautifiedShape();
		org.openawt.geom.Line2D line2d = (org.openawt.geom.Line2D)shape.getShape();
		//System.out.println("evaluatePointLineConnection");
		//System.out.println("P1 ("+line2d.getX1()+","+line2d.getY1()+")");
		//System.out.println("P2 ("+line2d.getX2()+","+line2d.getY2()+")");
		//System.out.println("point ("+point.getX()+","+point.getY()+")");

		//double error = line.getLength()*error_margin;
		double error = Math.sqrt((line2d.getP1().getX()-line2d.getP2().getX())*(line2d.getP1().getX()-line2d.getP2().getX())
				            + (line2d.getP1().getY()-line2d.getP2().getY())*(line2d.getP1().getY()-line2d.getP2().getY()))*error_margin;
		
		//System.out.println("error = "+error);
		
		//IPoint point1 = (IPoint) point;
		//double distanceToLine = line.distance(point);
		double distanceToLine = line2d.ptSegDist(point.getX(), point.getY());
		
		//System.out.println("distanceToLine = "+distanceToLine);

		if (distanceToLine <= error) {
			//System.out.println("Success! Point is connected to Line");
			conf = Math.min(conf, 0.79 * Math.exp(distanceToLine/error));
			return 0;
		}
		return -1;
	}

	/**
	 * Evaluate if a point belongs to an existing beam
	 * a point belongs to a beam IF the euclidean distance
	 * between the point and a beam are within the error_margin
	 * @author danieltan
	 * @param Beam beam, SRL_Point point
	 * @return beam.getP1 || beam.getP2 for success; null otherwise
	 */
	private NewNode evaluatePointBeamConnection(int beamIndex, Point point){
		Beam beam = (Beam)beams.get(beamIndex);
		double error = beam.getLength()*error_margin;		
		org.openawt.geom.Line2D tempBeam = new org.openawt.geom.Line2D.Double (beam.getP1().getX(), beam.getP1().getY(),
				beam.getP2().getX(), beam.getP2().getY());
		
		double distanceToP1 = point.distance(beam.getP1().getX(), beam.getP1().getY());
		double distanceToP2 = point.distance(beam.getP2().getX(), beam.getP2().getY());
		double distanceToLine = tempBeam.ptSegDist(point.getX(), point.getY());//(point);
		NewNode oldP2 = new NewNode();
		oldP2 = beam.getP2();

		if (distanceToLine <= error) {
			//check if the closest point on "beam" with "point" is an endpoint of "beam"
			if (distanceToP1 <= error)
				//closest point on "beam" with "point" is beam.P1
				return beam.getP1();
			else if (distanceToP2 <= error)
				//closest point on "beam" with "point" is beam.P2
				return beam.getP2();
			else {
				//closest point on "beam" with "point" is in the middle of "beam"
				//split beam
				//set P2 of first half to middlePoint
				NewNode middlePoint = new NewNode();
				//middlePoint.setId((int)(new Date()).getTime());
				middlePoint.setX((int)point.getX());
				middlePoint.setY((int)point.getY());
				beam.setP2(middlePoint);

				//set P1 of otherHalf to middlePoint and P2 to original beam.P2
				ArrayList<NewNode> line = new ArrayList<NewNode>();
				line.add(middlePoint);
				line.add(oldP2);
				Beam otherHalf = new Beam(line);
				addBeamAt(beamIndex, otherHalf);
				//System.out.println("Splitting Line Into:");
				//System.out.println("First Half Beam: P1 ("+beam.getP1().getX()+","+beam.getP1().getY()+")");
				//System.out.println("First Half Beam: P2 ("+beam.getP2().getX()+","+beam.getP2().getY()+")");
				//System.out.println("Other Half Beam: P1 ("+otherHalf.getP1().getX()+","+otherHalf.getP1().getY()+")");
				//System.out.println("Other Half Beam: P2 ("+otherHalf.getP2().getX()+","+otherHalf.getP2().getY()+")");
				conf = Math.min(conf, 0.79*Math.exp(distanceToLine/error));
				return beam.getP2();
			}
		}
		return null;
	}

	/**
	 * Evaluate Truss nodes and sets all node booleans
	 * @author danieltan
	 * @return 0 for success; -1 otherwise
	 */
	private int evaluateNodes(){
		int count = 0;
		for (int i=0; i<nodes.size(); i++) {
			for (int j=0; j<beams.size(); j++) {
				System.out.println("nodes["+i+"].ID = "+nodes.get(i).getID());
				System.out.println("beams["+j+"].P1.ID = "+((Beam)beams.get(j)).getP1().getID());
				System.out.println("beams["+j+"].P2.ID = "+((Beam)beams.get(j)).getP2().getID());

				if ( nodes.get(i).getID() == ((Beam)beams.get(j)).getP1().getID() ||
						nodes.get(i).getID() == ((Beam)beams.get(j)).getP2().getID() ){
					count++;
				}
			}
			if (count >= 2) {
				System.out.println("This node has 2 beams emanating from it");
				nodes.get(i).setState(true);
				count = 0;
			}
		}
		return 0;
	}

	/**
	 * Evaluates if the current beams in ArrayList beams form a complete Truss
	 * by ensuring all nodes have at least 2 beams connected to it
	 * @author danieltan
	 * @return 0 for success; -1 if not yet a Truss
	 */
	public int evaluateTruss(){
		if (beams.size() < 3)
			return -1;
		else if (evaluateNodes() == 0) {
			int count = 0;
			for (int i=0; i<nodes.size(); i++) {
				if (nodes.get(i).getState() == false){
					count++;
				}
			}
			if (count > 0)
				return -1;
			else
				return 0;
		}
		else
			return -1;
	}

	/**
	 * Evaluates if the current Truss is a stable one
	 * @author danieltan
	 * @return 0 for success; -1 if not yet stable
	 */
	public int evaluateTrussStability(){
		if (2*nodes.size()-3 <= beams.size())
			return 0;
		else
			return -1;
	}

	//********************************
	//
	//
	//		   	  Setters:
	//
	//
	//********************************

	/**
	 * Sets the error_margin which if multiplied by a line's length indicates what the system's tolerance is to drawing another connected line
	 * @author danieltan
	 * @param takes in a double value which error_margin is set to
	 */
	public void setErrorMargin(double error) {
		error_margin = error;
	}

	//********************************
	//
	//
	//		      Getters:
	//
	//
	//********************************





	/**
	 * Retrieves ArrayList of the included Nodes
	 * @return ArrayList<Node>
	 */
	public ArrayList<NewNode> getNodes(){
		return nodes;
	}

	/**
	 * Retrieves a specific Beam
	 * @param beamIndex
	 * @return Beam
	 */
	public Beam getBeam(int beamIndex){
		return (Beam)beams.get(beamIndex);
	}

	/**
	 * Retrieves a specific Node
	 * @param nodeIndex
	 * @return Node
	 */
	public NewNode getNode(int nodeIndex){
		return nodes.get(nodeIndex);
	}

	/**
	 * Retrieves the error_margin
	 * @return error_margin
	 */
	public double getErrorMargin(){
		return error_margin;
	}
	
	public Interpretation getInterpretation() {
		return interpretations.get(interpretations.size() - 1);
	}

	//********************************
	//
	//
	//		   	 Functions:
	//
	//
	//********************************

	/**
	 * Adds a Beam
	 * @param beam Beam
	 */
	public void addBeam(Beam beam){
		beams.add(beam);
	}

	/**
	 * Adds a Beam
	 * @param beam Beam
	 */
	public void addBeamAt(int index, Beam beam){
		beams.add(index, beam);
	}

	/**
	 * Removes a Beam from index i
	 * @param beamIndex Index of beam in list of beams
	 */
	public void removeBeam(int beamIndex){
		beams.remove(beamIndex);
	}

	/**
	 * Clears all the Truss members
	 * @author danieltan
	 */
	public void clear(){
		beams.clear();
		nodes.clear();
	}

	/**
	 * @see srl.core.sketch.IShape#clone()
	 * @return
	 */
	@Override
	public Truss clone() {
		Truss t = new Truss();
		t.beams.addAll(beams);
		t.aliases.addAll(aliases);
		t.error_margin = error_margin;
		t.nodes.addAll(nodes);
		return t;
	}

	public double getConfidence() {
		return conf;
	}
}
