import java.awt.Graphics2D;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.Arrays;
import java.util.Collections;
import java.util.Scanner;

public class CustomizableMesh extends PointCloud{
	double time = 0;
	
	public CustomizableMesh(int pointsPerSide){
		super(pointsPerSide);
//		canDrawPolies=false;
		canDrawSegments=false;
		canDrawPoints=false;
		override=true;
		setSides();
		setColor();
		this.rLocal.setAll(0, -90, 0);
	}
	
	public void setColor(){
		for(int i = 1; i<points.length-31; i++){
			points[i].setBaseColor(0,0,100);
		}
	}
	
	public void drawCalc(){
		super.drawCalc();
//		rLocal.add('z', 10);
		for(CircularPoint cp: points){
			Vector rot = new Vector();
			rot.setAll(10,1,1);
			cp.rotateGlobal(rot);
		}
		int flashlightRadius = 100;
		for(int i=0; i<polies.size(); i++){
			Poly activePoly = polies.get(i);
			activePoly.getNormal();
			CircularPoint cp0 = activePoly.cp[0];
			Vector s0 = activePoly.cp[0].loc;
			Vector s1 = activePoly.cp[1].loc;
			Vector s2 = activePoly.cp[2].loc;
			if(cp0.baseColor!=null){
				double rr=100,gg=100,bb=100;
				rr+=(2*s0.getX()-s1.getX()-s2.getX())*-.5;
				gg+=(2*s0.getY()-s1.getY()-s2.getY())*-.5;
				bb+=(2*s0.getZ()-s1.getZ()-s2.getZ())*-.5;
				double light= 50*(  flashlightRadius-Math.min(flashlightRadius, Math.pow(10,-7)*camDist(points[i].loc))  )/flashlightRadius;
				rr+=light;
				gg+=light;
				bb+=light;
				cp0.setLightingColor(rr, gg, bb);
				cp0.mixColors();
			}
		}
	}
	
	public void setSides(){ //precondition: the first line must not specify a vertex. not sure why.
		wireframe.removeAll(wireframe);
		polies.removeAll(polies);
		for(int i = 0; i<points.length; i++)points[i]=new CircularPoint();
		Scanner in = null;
		try {
			in = new Scanner(new FileReader("teapot.obj"));
			int mult = 10;
			int firstVertex = 0;
			for(int i=0; in.hasNext(); i++){
				String currentLine = in.nextLine();
				if(!currentLine.isEmpty()){			
					String[] parts = currentLine.split(" ");
					int partsCount = parts.length;
					if(parts[0].equals("v") && partsCount==4){
						if(firstVertex==0)firstVertex=i;
						Vector v = new Vector();
						v.set('x', mult*Double.parseDouble(parts[1]));
						v.set('y', mult*Double.parseDouble(parts[2]));
						v.set('z', mult*Double.parseDouble(parts[3]));
						points[i].loc.setAll(v);
					}
					if(parts[0].equals("f")){
						CircularPoint[] cp = new CircularPoint[partsCount-1];
						for(int j=0; j<partsCount-1; j++)cp[j] = points[Integer.parseInt(parts[j+1])+firstVertex-1];
						Poly p = new Poly(cp);
						polies.add(p);
					}
				}
			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		finally{
			in.close();
		}
		for(int i = 0; i<points.length; i++)points[i].mixColors();
	}
}
