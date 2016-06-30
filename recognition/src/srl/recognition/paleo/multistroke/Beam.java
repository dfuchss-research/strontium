package srl.recognition.paleo.multistroke;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.openawt.Color;
import org.openawt.Polygon;

import srl.core.sketch.Alias;
import srl.core.sketch.BoundingBox;
import srl.core.sketch.IShape;
import srl.core.sketch.IStroke;
import srl.core.sketch.Point;


/**
 * Every Truss is composed of beams and nodes: This class manages the beam member of a Truss.
 * This may be used by other Free Body Diagram physics problems, but currently is only used by class, Truss.
 * Every Beam contains an beam id, an ArrayList of nodes, 2 nodes for each end point P1 and P2 and a length.
 * Every Beam logically should be composed of only 2 end points; however, in the event a beam definition
 * includes curved beams, the class contains a node list to handle all the intermediate points between P1 and P2
 * @author blake
 * @author danieltan
 * 
 */
public class Beam implements IShape{
	private ArrayList<NewNode> nodes;
	private NewNode P1;
	private NewNode P2;
	private double length;
	
	private List<Alias> aliases;
	private List<IStroke> strokes;
	
	private Long m_maxStrokeTime = null;
	private BoundingBox m_boundingBox;
	private UUID ID = UUID.randomUUID();
	private Double m_confidence;
	private Map<String, String> m_attributes = null;
	private String m_description = null;
	
	
	//********************************
	//
	//
	//		   Constructors:
	//
	//
	//********************************

	/**
	 * Default constructor
	 * Creates an empty ArrayList<NewNode>
	 */
	public Beam(){
		nodes = new ArrayList<NewNode>();
	}

	/**
	 * Constructor which takes an ArrayList<NewNode>
	 * and sets P1 and P2 to be the first and last elements of the ArrayList node
	 * @param ArrayList<NewNode> node
	 */
	public Beam(ArrayList<NewNode> node){
		nodes = node;
		setP1(nodes.get(0));
		setP2(nodes.get(nodes.size()-1));
	}


	//********************************
	//
	//
	//		   	  Setters:
	//
	//
	//********************************

	/**
	 * Sets the beam end point P1
	 * @param NewNode node
	 */
	public void setP1(NewNode node) {
		P1 = node;
	}

	/**
	 * Sets the beam end point P2
	 * @param NewNode node
	 */
	public void setP2(NewNode node) {
		P2 = node;
	}

	//********************************
	//
	//
	//		      Getters:
	//
	//
	//********************************

	/**
	 * Gets the node P1 and returns it
	 * @return NewNode P1
	 */
	public NewNode getP1(){
		return P1;
	}

	/**
	 * Gets the node P2 and returns it
	 * @return NewNode P2
	 */
	public NewNode getP2(){
		return P2;
	}

	/**
	 * Gets the node list ArrayList<NewNode>nodes of the beam and returns it
	 * @return ArrayList<NewNode> nodes
	 */
	public ArrayList<NewNode> getNodes(){
		return nodes;
	}

	/**
	 * Gets a specific node at a given index location and returns it
	 * @param int nodeIndex
	 * @return NewNode at index nodeIndex
	 */
	public NewNode getNode(int nodeIndex){
		return nodes.get(nodeIndex);
	}

	/**
	 * Calculates the distance between P1 and P2 and returns the value as a double
	 * @return length of beam (type double)
	 */
	public double getLength(){
		double xdiff = P1.getX() - P2.getX();
		double ydiff = P1.getY() - P2.getY();
		return Math.sqrt(xdiff*xdiff + ydiff*ydiff);
	}

	//********************************
	//
	//
	//		   	 Functions:
	//
	//
	//********************************

	/**
	 * Adds a node to ArrayList<NewNode> nodes
	 * @param NewNode node
	 */
	public void addNode(NewNode node){
		nodes.add(node);
	}

	/**
	 * Removes a specific node from ArrayList<NewNode> nodes at a given index location
	 * @param int nodeIndex
	 */
	public void removeNode(int nodeIndex){
		nodes.remove(nodeIndex);
	}


	/**
	 * @author bpavel
	 * @see srl.core.sketch.IShape#clone()
	 * @return
	 */
	@Override
	public Beam clone() {
		return null;
	}

	@Override
	public int compareTo(IShape arg0) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public void addAlias(Alias alias) {
		
		aliases.add(alias);
		
	}

	@Override
	public void addStroke(IStroke stroke) {
		
		if(strokes == null)strokes = new ArrayList<IStroke>();
		BoundingBox strokeBox = stroke.getBoundingBox();
		BoundingBox curBox = getBoundingBox();
		double minx = Math.min(curBox.getMinX(), strokeBox.getMinX());
		double miny = Math.min(curBox.getMinY(), strokeBox.getMinY());
		double maxx = Math.max(curBox.getMaxX(), strokeBox.getMaxX());
		double maxy = Math.max(curBox.getMaxY(), strokeBox.getMaxY());
		m_boundingBox = new BoundingBox(minx, miny, maxx, maxy);
		
		// max stroke time
		long curTime = getTime();
		m_maxStrokeTime = new Long(Math.max(curTime, stroke.getTime()));
		strokes.add(stroke);
		
	}

	@Override
	public boolean containsStroke(IStroke stroke) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean containsStrokeRecursive(IStroke stroke) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void flagExternalUpdate() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public Alias getAlias(String name) {
		for(Alias alias:aliases) {
			if (alias.getName() == name)return alias;
		}
		return null;
	}

	@Override
	public Collection<Alias> getAliases() {
		return aliases;
	}

	@Override
	public String getAttribute(String key) {
		if (m_attributes == null)
			return null;
		return m_attributes.get(key);
	}

	@Override
	public Map<String, String> getAttributes() {
	
		return null;
	}

	@Override
	public BoundingBox getBoundingBox() {
		
		return null;
	}

	@Override
	public Polygon getConvexHull() {
		
		return null;
	}

	@Override
	public Double getConfidence() {
		return 1D;//TODO assign conf  o
	}

	@Override
	public String getDescription() {
		return m_description;
	}

	@Override
	public IStroke getFirstStroke() {
		return strokes.get(0);
	}

	@Override
	public UUID getID() {
		return ID ;
	}

	@Override
	public String getLabel() {
		return "Beam";
	}

	@Override
	public String getShapeLabelText() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Point getShapeLabelLocation() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public IStroke getLastStroke() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Long getRecognitionTime() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<IStroke> getRecursiveStrokes() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<IShape> getRecursiveSubShapes() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<IShape> getAllRecursiveSubShapes() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<IStroke> getRecursiveParentStrokes() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public IStroke getStroke(int index) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public IStroke getStroke(UUID strokeId) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<IStroke> getStrokes() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public IShape getSubShape(int index) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public IShape getSubShape(UUID shapeId) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<IShape> getSubShapes() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public long getTime() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public boolean hasAttribute(String key) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public String removeAttribute(String name) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void setAttribute(String key, String value) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void setColor(Color c) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void setConfidence(Double confidence) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void setDescription(String description) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void setLabel(String label) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void setShapeLabelText(String text) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void setShapeLabelLocation(Point newPoint) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void setRecognitionTime(Long recTime) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void setStrokes(List<IStroke> strokes) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void setSubShapes(List<IShape> subShapes) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public boolean equalsByContent(IShape os) {
		// TODO Auto-generated method stub
		return false;
	}

	//********************************
	//
	//
	//		   Google Protobuf:
	//
	//
	//********************************

}
