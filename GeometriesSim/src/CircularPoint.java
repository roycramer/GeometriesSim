import java.awt.Color;
import java.awt.Graphics2D;


public class CircularPoint implements Comparable<CircularPoint>{
	
	String identity = "No Identity";
	Vector loc, cen, d, stored;
	Vector cam;
	int mouseStrength = 5, alpha = 255;
	double depthScale = 0.007, segmentThickness = 1, frictionAir = 0.99;
	double sclMax = 40, sclPow = 0.3;
	boolean isVisible = true;
	Color sphereColor, baseColor, lightingColor;
	
	public void setVisible (boolean in){
		isVisible = in;
	}
	
	public CircularPoint() {
		loc = new Vector();
		cen   = new Vector();
		d = new Vector();
		stored = new Vector();
	}
	
	public void setThickness(double in){
		segmentThickness = in;
	}
	
	public void teleport(double xIn, double yIn, double zIn){
		loc.setAll(xIn, yIn, zIn);
	}
	
	public void teleport(Vector v){
		loc.setAll(v);
	}
	
	public void move(double xIn, double yIn, double zIn){
		loc.addAll(xIn, yIn, zIn);
	}
	
	public void move(Vector v){
		loc.addAll(v);
	}
	
	public void setPerspectiveCenter(double xIn, double yIn, double zIn){
		cen.setAll(xIn, yIn, zIn);
	}
	
	public void xyzToXYS(){
		double sx = stored.getX();
		double sy = stored.getY();
		double sz = stored.getZ();
		double cx = cen.getX();
		double cy = cen.getY();
		
		d.setZ(Math.min(sz, sclMax));
//		double intoPerspective = sz*depthScale;
//		double averager = (intoPerspective+mouseStrength);
//		d.setX((cx*intoPerspective+(cx+sx)*mouseStrength)/averager);
//		d.setY((cy*intoPerspective+(cy+sy)*mouseStrength)/averager);
		
		double ix = Math.cos(Math.atan2(sz, sx));
		double iy = Math.cos(Math.atan2(sz, sy));
//		double ix = Math.signum(sx)/Math.sqrt(sz*sz/sx/sx+1);
//		double iy = Math.signum(sy)/Math.sqrt(sz*sz/sy/sy+1);
		d.setX(cx*(1+2*ix));
		d.setY(cx*(1+2*iy));
	}
	
	public void draw(Graphics2D g2){
		if(isVisible && withinScreen()){
			g2.setColor(sphereColor);
			g2.fillRect((int)d.getX()-(int)d.getZ()/2,(int)d.getY()-(int)d.getZ()/2,(int)d.getZ(),(int)d.getZ());
		}
	}
	
	public void drawString(Graphics2D g2, String s){
		if(isVisible){
			g2.setColor(sphereColor);
			g2.drawString(s,(int)d.getX(),(int)d.getY());
		}
	}
	
	public void performTransformations(Vector rLocal, Vector locAxial, Vector rAxial, Vector locCamera, Vector rCamera){
		// not using rLocal and rAxial increases framerate from 44 to 54.
		stored.setAll(loc);
		stored.rotate(rLocal);
		stored.addAll(locAxial);
		stored.rotate(rAxial);
		stored.addAll(locCamera.getNegative());
		stored.rotate(rCamera);
		cam = locCamera;
	}
	
	public void rotateGlobal(Vector rLocal){
		loc.rotate(rLocal);
	}
	
	public boolean withinScreen(){
		double zTemp = stored.getZ();
		if(zTemp<0)return false;
		double xTemp = d.getX();
		double yTemp = d.getY();
		double border = 5;
		if(xTemp<border||yTemp<border)return false;
		double ww = cen.getX()*2;
		double hh = cen.getY()*2;
		if(xTemp>ww-border || yTemp>hh-border)return false;
		return true;
	}
	
	public void setColor(double rr, double gg, double bb){
		rr=Math.max(Math.min(255, rr),0);
		gg=Math.max(Math.min(255, gg),0);
		bb=Math.max(Math.min(255, bb),0);
		sphereColor = new Color((int)rr,(int)gg,(int)bb,alpha);
	}
	
	public void setBaseColor(double rr, double gg, double bb){
		rr=Math.max(Math.min(255, rr),0);
		gg=Math.max(Math.min(255, gg),0);
		bb=Math.max(Math.min(255, bb),0);
		baseColor = new Color((int)rr,(int)gg,(int)bb,alpha);
	}
	
	public void setLightingColor(double rr, double gg, double bb){
		rr=Math.max(Math.min(255, rr),0);
		gg=Math.max(Math.min(255, gg),0);
		bb=Math.max(Math.min(255, bb),0);
		lightingColor = new Color((int)rr,(int)gg,(int)bb,alpha);
	}
	
	public void mixColors(){
		if(lightingColor==null){
			sphereColor = baseColor;
		}
		else{
			double rr = baseColor.getRed()+lightingColor.getRed();
			double gg = baseColor.getGreen()+lightingColor.getGreen();
			double bb = baseColor.getBlue()+lightingColor.getBlue();
			rr=Math.max(Math.min(255, rr),0);
			gg=Math.max(Math.min(255, gg),0);
			bb=Math.max(Math.min(255, bb),0);
			sphereColor = new Color((int)rr, (int)gg,(int)bb,alpha);
		}
	}
	
	public double camDist(Vector cp){
		double result = 0;
		if(cam!=null)result = cam.distSphereUnscaled(cp);
		return result;
	}
	
	public int compareTo(CircularPoint cp) {
//		int result = (int)(Math.signum(camDist(cp.loc)-camDist(loc)));
		if(d!=null&&cp.d!=null){
			double d1 = d.getZ()-cp.d.getZ();
			int result = (int)(Math.signum(d1));
			return result;
		}
		else return 0;
	}
	
}
