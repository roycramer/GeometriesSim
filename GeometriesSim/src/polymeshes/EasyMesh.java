import java.awt.Graphics2D;
import java.util.Random;

public class EasyMesh extends PointCloud{
	double time = 0;
	
	public EasyMesh(int pointsPerSide){
		super(pointsPerSide);
//		canDrawPolies=false;
		canDrawSegments=false;
		canDrawPoints=false;
		override=true;
		setSides();
		setColor();
	}
	
	public void setColor(){
		for(int i = 1; i<points.length-31; i++){
			double rr = 22.0*Math.random();
			double gg = 22.0*Math.random();
			double bb = 22.0*Math.random();
			points[i].setBaseColor(rr,gg,bb);
		}
	}
	
	public int bound(int in){
		return Math.min(Math.max(in, 0),4000);
	}
	
	public void setSides(){
		wireframe.removeAll(wireframe);
		polies.removeAll(polies);
		for(int i = 0; i<points.length; i++){
			points[i]=new CircularPoint();
		}

		
	}
	public void draw(Graphics2D g2){
		super.draw(g2);
	}
}
