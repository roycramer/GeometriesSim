 import java.awt.Color;
import java.awt.Graphics2D;
import java.util.ArrayList;
import java.util.Collections;


public class PointCloud implements Comparable<PointCloud>{
	CircularPoint[] points;
	Vector loc = new Vector();
	Vector rLocal = new Vector();
	Vector rAxial = new Vector();
	Vector rCamera = new Vector();
	Vector vel = new Vector();
	double rows, columns;
	double diameter;
	double forceScalar = 10;
	double friction = 0.98;
	boolean canDrawPolies=true;
	boolean canDrawSegments=true;
	boolean canDrawPoints=true;
	boolean isVisible=true;
	boolean override = false;
	boolean withinScreen = false;
	boolean canMove = true;
	PointCloud camera;
	int dist;
	
	ArrayList<Line> wireframe =new ArrayList<Line>();
	ArrayList<Poly> polies =new ArrayList<Poly>();
	int time;
	
	public PointCloud(int pointCount){
		points = new CircularPoint[pointCount];
		for(int i = 0; i< pointCount; i++)points[i] = new CircularPoint();
	}
	
	public void stayAbove(HeightMap h){
//		for(int i=0; i<h.points.length; i++){
//			CircularPoint cp = h.points[i];
//			double distx = Math.abs(cp.loc.getX()-loc.getX());
//			double disty = Math.abs(cp.loc.getY()-loc.getY());
//			if(distx<1000&&disty<1000&&loc.getZ()>cp.loc.getZ()){
//				loc.set('z', cp.loc.getZ());;
//				vel.set('z', 0);
//			}
//		}
	}
	
	public void boundTo(PointCloud pc){
		int chainRestrict = 200;
		double dist = pc.loc.distSphere(loc);
		
		if(dist>chainRestrict){
			double inverseDistance = chainRestrict/dist; //after multiplying by this, it should be a scale of 1.
			Vector special = loc;
			special.addAll(pc.loc.getNegative());
			special.multiplyAll(inverseDistance);
			special.addAll(pc.loc);
			pc.loc.setAll(special);
		}
	}
	
	public void setCam(PointCloud cam){
		this.camera = cam;
	}
	
	public void setLoc(double xIn, double yIn, double zIn){
		loc.setAll(xIn, yIn, zIn);
	}
	
	public void move(double xIn, double yIn, double zIn){
		loc.addAll(xIn, yIn, zIn);
	}
	
	public void boundToWalls(){
		int wall1 = 120000;
		int wall2 = 40000;
		if(loc.getX()>wall1 && loc.getX()<-wall1 && loc.getY()>wall1 && loc.getY()<-wall1)vel.multiplyAll(-1, -1, 1);
		if(loc.getX()>wall1){
			loc.setX(wall1);
			vel.add('x',-10);
		}
		if(loc.getX()<-wall1){
			loc.setX(-wall1);
			vel.add('x',10);
		}
		if(loc.getY()>wall1){
			loc.setY(wall1);
			vel.add('y',-10);
		}
		if(loc.getY()<-wall1){
			loc.setY(-wall1);
			vel.add('y',10);
		}
		if(loc.getZ()>wall2)loc.setZ(wall2);
		if(loc.getZ()<-wall2)loc.setZ(-wall2);
	}
	
	public void customProperties(boolean keyR, boolean keyF, PointCloud cameraIn){
		double speed1 = -10*forceScalar*Math.random();
		if(keyF)speed1*=-1;
		if(keyR||keyF){
			for(char cc = 'x'; cc<='z'; cc++){
//				loc.set(cc, cameraIn.loc.get(cc));
				vel.add(cc, -0.01*loc.get(cc));
			}
		}
		buzzingMovement(1);
		update();
		setCam(cameraIn);
		floorBoundary(0);
	}
	
	private void floorBoundary(int floor){
		if(loc.getZ()>floor)loc.set('z',floor);
	}
	
	private void buzzingMovement(double in){
		vel.add('x', in*(Math.random()-0.5));
		vel.add('y', in*(Math.random()-0.5));
		vel.add('z', in*(Math.random()-0.5));
	}
	
	public void update(){
		applyFriction(friction);
		vel.add('z',9.81*forceScalar);
		loc.addAll(vel);
	}
	
	public void applyFriction(double frictionIn){
		vel.multiplyAll(frictionIn, frictionIn, frictionIn);
	}
	
	public void bounce(PointCloud pc){
		if(loc.distBox(pc.loc)<diameter/2&&loc.distBox(pc.loc)>diameter/3){ //isColliding
			for(char cc = 'x'; cc<='z'; cc++){
				vel.set(cc,pc.vel.get(cc)*0.5);
			}
		}
	}
	
	public void repel(PointCloud pc, double radius, double bounceMagnitude){
		if(loc.distBox(pc.loc)<radius){ //isColliding
			for(char cc = 'x'; cc<='z'; cc++){
				double dist = loc.get(cc)-pc.loc.get(cc);
				if(dist>radius)dist-=radius;
				if(dist<-radius)dist+=radius;
				vel.add(cc,(dist)*bounceMagnitude); //add velocity by difference in position
//				if(loc.get(cc)>pc.loc.get(cc))loc.set(cc,pc.loc.get(cc)+radius);
			}
		}
	}
	
	public void repelCube(PointCloud pc){
		repel(pc, diameter/2, .04);
	}
	
	public void setLineThickness(double lIn){
		for(int i = 0; i<points.length; i++){
			points[i].setThickness(lIn);
		}
	}
	
	public boolean withinTheseBounds(double x1, double y1, double x2, double y2){
		for(int i=0; i<points.length; i++){
		boolean b1 = points[i].d.getX()>x1;
		boolean b2 = points[i].d.getX()<x2;
		boolean b3 = points[i].d.getY()>y1;
		boolean b4 = points[i].d.getY()<y2;
			if(b1&&b2&&b3&&b4&&points[i].d.getZ()>0)return true;
		}
		return false;
//		
//		for(int i=0; i<polies.size(); i++){
//			if(polies.get(i).shape.contains(x1, y1))return true;
//		}
//		return false;
	}
	
	public void setPerspectiveCenter(double xIn, double yIn, double zIn){
		for(int i = 0; i<points.length; i++){
			points[i].setPerspectiveCenter(xIn, yIn, zIn);
		}
	}
	
	public double camDist(Vector cp){
		double result = 0;
		if(camera!=null)result = camera.loc.distSphereUnscaled(cp);
		return result;
	}
	
	public void drawCalc(){
		points[0].performTransformations(rLocal, loc, rAxial, camera.loc, rCamera);
		points[0].xyzToXYS();
		withinScreen = (override || isVisible && points[0].withinScreen());
		if(withinScreen){
			performTransformations();
//			if(time%1==0)Collections.sort(polies);
			if(canDrawPolies)  for(int i=0; i<polies.size(); i++){
				polies.get(i).drawCalc();
			}
		}
	}
	
	public void draw(Graphics2D g2){
		time++;
		if(withinScreen){
			if(canDrawPoints)  for(int i=0; i<points.length; i++)   points[i].draw(g2);
			if(canDrawSegments)for(int i=0; i<wireframe.size(); i++)wireframe.get(i).draw(g2);
			if(canDrawPolies)  for(int i=0; i<polies.size(); i++){
				if(true || points[0].stored.getZ()<40000)
					polies.get(i).draw(g2);
				else
					points[0].draw(g2);
			}
		}
		
	}
	
	public void performTransformations(){
		for(int i = 0; i<points.length; i++){
			points[i].performTransformations(rLocal, loc, rAxial, camera.loc, rCamera);
			points[i].xyzToXYS();
		}
	}

	public int compareTo(PointCloud pc) {
		if(pc.getClass()==HeightMap.class)return 1;
		if(getClass()==HeightMap.class)return -1;
		int result = (int)(-Math.signum(camDist(loc)-camDist(pc.loc)));
		return result;
	}
}
