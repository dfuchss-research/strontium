package srl.recognition.paleo.multistroke;

import java.util.UUID;

import srl.core.sketch.IPoint;

public class NewNode implements IPoint{
	public UUID id = UUID.randomUUID();
	private double x;
	private double y;
	long time;
	private boolean hasAtLeast2Beams;
	
	public NewNode(NewNode newNode) {
		x = newNode.getX();
		y = newNode.getY();
		time = Integer.MAX_VALUE;
	}

	public NewNode() {
		// TODO Auto-generated constructor stub
	}

	public NewNode(double i, double j, long t) {
		x=i;
		y=j;
		time = t;
		
	}

	@Override
	public double getX() {
		return x;
	}

	@Override
	public double getY() {
		return y;
	}

	@Override
	public long getTime() {
		return time;
	}

	@Override
	public UUID getID() {
		return id;
	}
	
	public void setState(boolean bool) {
		hasAtLeast2Beams = bool;
	}
	
	/**
	 * Sets a Node x coordinate
	 * @param takes an int x_coord
	 */
	public void setX(int x_coord){
		x = x_coord;
	}

	/**
	 * Sets a Node y coordinate
	 * @param takes an int y_coord
	 */
	public void setY(int y_coord){
		y = y_coord;
	}
	
	@Override
	public boolean equalsXYTime(IPoint p) {
		return  (p.getX() == x && p.getY() == y && p.getTime() == time);
	}

	@Override
	public void translate(double x, double y) {
		
	}

	@Override
	public void scale(double x, double y) {
		
	}
	
	@Override
	public NewNode clone() {
		return new NewNode(this);
		
	}

	@Override
	public int compareTo(IPoint p) {
		int timeDiff = (int) (this.time - p.getTime());
		if (timeDiff != 0)
			return timeDiff;
		
		int xDiff = (int) (this.getX() - p.getX());
		if (xDiff != 0)
			return xDiff;
		
		int yDiff = (int) (this.getY() - p.getY());
		if (yDiff != 0)
			return yDiff;
		
		int idDiff = this.getID().compareTo(p.getID());
		return idDiff;
	}

	@Override
	public double distance(IPoint p) {
		return Math.sqrt(p.getX()*p.getX() - getX() * getX() 
				+ p.getY() * p.getY() - getY()*getY());
	}

	@Override
	public double distance(double x, double y) {
		return Math.sqrt(x*x - getX() * getX() 
				+ y * y - getY()*getY());
	}

	public boolean getState() {
		return hasAtLeast2Beams;
	}

}
