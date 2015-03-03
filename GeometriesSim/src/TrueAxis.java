import java.awt.Graphics2D;
import java.util.ArrayList;



public class TrueAxis extends PointCloud{
	double dooble = 0;
	double time = 0;
	Vector random = new Vector();
	public TrueAxis(int pointsPerSide){
		super(pointsPerSide*3);
//		canDrawPolies=false;
//		canDrawSegments=false;
//		canDrawPoints=false;
		override=true;
		setSides(pointsPerSide);
		setColor();
	}
	
	public void setColor(){

	}
	
	public void setSides(int sideLength){
		
		
		
		wireframe.removeAll(wireframe);
		polies.removeAll(polies);
		for(int i = 0; i<sideLength; i++){
			int dist = 10000*i-5000*sideLength;
			points[i]=new CircularPoint();
			points[i].teleport(dist,0,0);
			points[i].setBaseColor(255, 0, 0);
			
			points[i+sideLength]=new CircularPoint();
			points[i+sideLength].teleport(0,dist,0);
			points[i+sideLength].setBaseColor(0, 255, 0);
			
			points[i+2*sideLength]=new CircularPoint();
			points[i+2*sideLength].teleport(0,0,dist);
			points[i+2*sideLength].setBaseColor(0, 0, 255);
			
			wireframe.add(new Line(points[i],points[Math.abs(i-1)]));
			wireframe.add(new Line(points[i+sideLength],points[i-1+sideLength]));
			wireframe.add(new Line(points[i+sideLength*2],points[i-1+sideLength*2]));
		}
		
	}
	public void draw(Graphics2D g2){
		for(int i=0; i<points.length; i++){
			points[i].mixColors();
		}
		super.draw(g2);
		time++;
	}
}
