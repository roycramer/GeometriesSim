import java.awt.Color;
import java.awt.Graphics2D;
import java.util.Scanner;

public class Cube extends PointCloud{

	public Cube(int pointsPerSide, int distIn){
		super(pointsPerSide*pointsPerSide*pointsPerSide);
		diameter = distIn;
//		canDrawPolies=false;
		canDrawSegments=false;
		canDrawPoints=false;
		setSides(pointsPerSide);
		setColor();
	}
	
	public void setColor(){
		double rr = 255.0*Math.random();
		double gg = 255.0*Math.random();
		double bb = 255.0*Math.random();
		for(int i = 0; i<points.length; i++){
			points[i].setColor(rr,gg,bb);
			rr-=155.0/points.length;
			gg-=155.0/points.length;
			bb-=055.0/points.length;
		}
	}
	
	public void setSides(int pointsPerSide){
		wireframe.removeAll(wireframe);
		polies.removeAll(polies);
		int centr = (int)(diameter/2);
		for(int i = 0; i<points.length; i++){
			rows = pointsPerSide;
			dist = (int)(diameter/pointsPerSide);
			points[i]=new CircularPoint();
			points[i].teleport((int)(i%rows)*dist-centr,(int)(i/rows%rows)*dist-centr,(int)(i/rows/rows)*dist-centr);
		}
		polies.add(new Poly(points[1],points[3],points[7],points[5]));
		polies.add(new Poly(points[2],points[3],points[7],points[6]));
		polies.add(new Poly(points[3],points[2],points[6],points[7]));
		polies.add(new Poly(points[5],points[4],points[0],points[1]));
		polies.add(new Poly(points[0],points[2],points[3],points[1]));
		polies.add(new Poly(points[5],points[4],points[6],points[7]));
	
	}
	
	public void draw(Graphics2D g2){
		super.draw(g2);
		override = false;
		CircularPoint cp;
		cp = new CircularPoint();
		if(camera!=null&&rLocal!=null&&loc!=null&&rAxial!=null&&camera.loc!=null&&rCamera!=null)
		cp.performTransformations(rLocal, loc, rAxial, camera.loc, rCamera);
		
		cp.xyzToXYS();
		g2.setColor(Color.RED);
		cp.draw(g2);
		cp.cen.setAll(points[0].cen);
	}
}
