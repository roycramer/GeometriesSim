import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Polygon;
import java.awt.geom.Line2D;
import java.awt.image.BufferedImage;


public class Poly implements Comparable<Poly>{
	CircularPoint[] cp;
	boolean calcIndividuals = false;
	public Polygon shape = new Polygon();
	boolean drawSafely=false;
	boolean front=false;
	
	public Poly(CircularPoint[] cpIn){
		cp = cpIn;
	}
	
	public void setCalcIndividuals(boolean in){
		calcIndividuals = in;
	}
	
	public Poly(CircularPoint aIn, CircularPoint bIn, CircularPoint cIn){
		cp = new CircularPoint[3];
		cp[0]=aIn;
		cp[1]=bIn;
		cp[2]=cIn;
	}
	
	public Poly(CircularPoint aIn, CircularPoint bIn, CircularPoint cIn, CircularPoint dIn){
		cp = new CircularPoint[4];
		cp[0]=aIn;
		cp[1]=bIn;
		cp[2]=cIn;
		cp[3]=dIn;
	}
	
	public Color colorbound(double rr, double gg, double bb){
		rr=Math.max(Math.min(255, rr),0);
		gg=Math.max(Math.min(255, gg),0);
		bb=Math.max(Math.min(255, bb),0);
		return new Color((int)rr,(int)gg,(int)bb);
	}
	
	public Color getColor(){
		Color cur = cp[0].sphereColor;
		if(cur==null)cur = cp[0].baseColor;
		return cur;
	}
	
	public Color getColor(double xIn, double yIn, BufferedImage bf){
		boolean useImage = true;
		boolean generated = !true;
		double rr,gg,bb,xImage,yImage;
//		xIn-=cp[0].d.getX();
//		yIn-=cp[0].d.getY();
		
		Color cur = cp[0].sphereColor;
		if(cur==null)cur = cp[0].baseColor;
		if(cur==null)cur = new Color(0,0,0);
		
		if(useImage){
			if(generated){
				double yCrazy = cp[1].d.getY()-cp[0].d.getY();
				double xCrazy = cp[2].d.getX()-cp[1].d.getX();
				double angle = Math.atan2(yCrazy, xCrazy);
				double magnitude = Math.sqrt(yCrazy*yCrazy+xCrazy*xCrazy);
				double xto1 = (xIn-cp[1].d.getX())/(xCrazy);
				double yto1 = (yIn-cp[0].d.getY())/(yCrazy);
				Point testPoint = new Point((int)xIn, (int)yIn);
				Line2D.Double xEdge = new Line2D.Double(cp[0].d.getX(), cp[0].d.getY(), cp[1].d.getX(), cp[1].d.getY());
				Line2D.Double yEdge = new Line2D.Double(cp[1].d.getX(), cp[1].d.getY(), cp[2].d.getX(), cp[2].d.getY());
				double xEdgeApproach = xEdge.ptLineDist(testPoint)/xEdge.getP1().distance(xEdge.getP2());
				double yEdgeApproach = yEdge.ptLineDist(testPoint)/yEdge.getP1().distance(xEdge.getP2());
				xImage = Math.abs(1*xCrazy/magnitude)*bf.getWidth();
				yImage = Math.abs(1*yCrazy/magnitude)*bf.getHeight();
				int scale = 1;
				
			}
			else{
				int scale = 1;
				xImage = Math.abs(xIn*scale)/SceneGraphic.ww*bf.getWidth();
				yImage = Math.abs(yIn*scale)/SceneGraphic.hh*bf.getHeight();
			}
			//bound the coordinates.
			xImage = Math.min(xImage, bf.getWidth()-1);
			yImage = Math.min(yImage, bf.getHeight()-1);
			Color cc = new Color(bf.getRGB((int)xImage, (int)yImage), true);
			int active = 1;
			rr = (cur.getRed()*active+cc.getRed());
			gg = (cur.getGreen()*active+cc.getGreen());
			bb = (cur.getBlue()*active+cc.getBlue());
//			double val1 = 5*(Math.abs(0.10*Math.abs(xIn*yIn)%100-50));
//			double val2 = 5*(Math.abs(0.11*Math.abs(xIn*yIn)%100-50));
//			double val3 = 5*(Math.abs(0.12*Math.abs(xIn*yIn)%100-50));
//			rr-=val1;
//			gg-=val2;
//			bb-=val3;
			return colorbound(rr,gg,bb);
		}
		else{
			double val1 = 5*(Math.abs(0.10*Math.abs(xIn*yIn)%100-50));
			double val2 = 5*(Math.abs(0.11*Math.abs(xIn*yIn)%100-50));
			double val3 = 5*(Math.abs(0.12*Math.abs(xIn*yIn)%100-50));
			int active = 1;
			rr = (cur.getRed()*active*val1/255);
			gg = (cur.getGreen()*active*val2/255);
			bb = (cur.getBlue()*active*val3/255);
			return colorbound(rr,gg,bb);
		}
	}
	
	public Vector getNormal(){ //use normal for backface culling, collision reflection, etc.getnormal
		Vector result = new Vector();
		Vector v1 = cp[0].stored;
		Vector v2 = cp[1].stored;
		Vector v3 = cp[2].stored;
		double xNormal = (v2.get('y') - v1.get('y'))*(v3.get('z') - v1.get('z')) - (v2.get('z') - v1.get('z'))*(v3.get('y') - v1.get('y'));
		double yNormal = (v2.get('z') - v1.get('z'))*(v3.get('x') - v1.get('x')) - (v2.get('x') - v1.get('x'))*(v3.get('z') - v1.get('z'));
		double zNormal = (v2.get('x') - v1.get('x'))*(v3.get('y') - v1.get('y')) - (v2.get('y') - v1.get('y'))*(v3.get('x') - v1.get('x'));
		result.setAll(xNormal,yNormal,zNormal);
		double temp = 1/Math.sqrt(xNormal*xNormal+yNormal*yNormal+zNormal*zNormal);
		result.multiplyAll(temp,temp,temp);
		return result;
	}
	
	public void drawCalc(){
		drawSafely=true;
		front=false;
		boolean temp2 = (cp[0].d.getZ()>0);
		if(cp.length==4){ //this optimization doesn't seem to help much.
			int[] ypoints = {(int)cp[0].d.getY(),(int)cp[1].d.getY(),(int)cp[2].d.getY(),(int)cp[3].d.getY()};
			int[] xpoints = {(int)cp[0].d.getX(),(int)cp[1].d.getX(),(int)cp[2].d.getX(),(int)cp[3].d.getX()};
			shape = new Polygon(xpoints, ypoints, 4);
		}
		else{
			shape = new Polygon();
			for(int i=0; i<cp.length; i++){
				shape.addPoint((int)cp[i].d.getX(), (int)cp[i].d.getY());
				if(calcIndividuals){
					boolean temp1 = (cp[i].d.getZ()>0);
					if(temp1!=temp2)drawSafely=false;
					if(temp1||temp2)front = true;
				}
			}
		}
		if(calcIndividuals){
			for(int i=0; i<cp.length; i++){
				boolean temp1 = (cp[i].d.getZ()>0);
				if(temp1!=temp2)drawSafely=false;
				if(temp1||temp2)front = true;
			}
		}
		
	}
	
	public void draw(Graphics2D g2){
		if((!calcIndividuals||drawSafely&&front)&&getNormal().getZ()<0){
			g2.setColor(cp[0].sphereColor);
			g2.fillPolygon(shape);
//			g2.setColor(Color.BLACK);
//			g2.drawPolygon(shape);
		}
	}

	public int compareTo(Poly o) {
		double az = 0;
		double bz = 0;
		for(int i=0; i<o.cp.length; i++){
			az+=o.cp[i].stored.getZ();
		}
		az/=1.0*o.cp.length;
		for(int i=0; i<cp.length; i++){
			bz+=cp[i].stored.getZ();
		}
		bz/=1.0*cp.length;
		return (int)(Math.signum(az-bz));
	}
}