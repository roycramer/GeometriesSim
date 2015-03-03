import java.awt.Graphics2D;
import java.util.Collections;
import java.util.Random;



public class HeightMap extends PointCloud{
	int pointCount = 0;
	Vector random = new Vector();
	public HeightMap(int pointsPerSide){
		super(pointsPerSide);
//		canDrawPolies=false;
		canDrawSegments=false;
		canDrawPoints=false;
		override=true;
		setSides(pointsPerSide);
		setColor();
	}
	
	public void setColor(){
		for(int i = 1; i<points.length-31; i++){
			double rr = 2.0*Math.random();
			double gg = 2.0*Math.random();
			double bb = 2.0*Math.random();
			points[i].setBaseColor(rr,gg,bb);
		}
	}
	
	public int bound(int in, int lim){
		return Math.min(Math.max(in, 0),lim);
	}
	
	public void setSides(int dotsTotal){
		if(pointCount==0)pointCount = dotsTotal;
		int rows = (int)Math.sqrt(pointCount);
		int size = 400000/pointCount;
		wireframe.removeAll(wireframe);
		polies.removeAll(polies);
		for(int i = 0; i<pointCount; i++){
			points[i]=new CircularPoint();
			points[i].teleport((i%rows-rows/2)*10*size, ((int)(i/rows)-rows/2)*10*size, 0); //form into grid
		}
		for(int i = 0; i<dotsTotal; i++){	
			if(i<points.length-rows-1&&i%rows!=0&&i%rows!=rows-1){
//				polies.add(new Poly(points[i],points[i+1],points[i+rows+1]));
//				polies.add(new Poly(points[i],points[i+rows],points[i+rows+1]));
				polies.add(new Poly(points[i+1],points[i],points[i+rows],points[i+rows+1]));
			}
		}
	}
	
	public void earthBending(){
		for(int i = points.length-1-(int)rows; i>0; i--){
			if(camDist(points[i].loc)<3000000){
				points[i].loc.multiplyAll(1,1,1.1);
			}
		}
	}
	
	public void iterate(){
		int flashlightRadius = 2000;
		double light = 0;
		for(int i = points.length-1-(int)rows; i>0; i--){
			if(points[i].baseColor!=null){
//				double rr=0,gg=0,bb=0;
//				rr-=(points[i].loc.getZ()-points[i+31].loc.getZ())*1;
//				gg-=(points[i].loc.getZ()-points[i+ 1].loc.getZ())*1;
//				bb-=(points[i].loc.getZ()-points[i- 1].loc.getZ())*1;
				light= 255*(  flashlightRadius-Math.min(flashlightRadius, Math.pow(10,-4)*camDist(points[i].loc))  )/flashlightRadius;
//				rr+=light;
//				gg+=light;
//				bb+=light;
//				points[i].loc.add('z',-4*(Math.sin(time*10.1*Math.sin(time*0.1))*light));
//				points[i].loc.add('z',-1*Math.sin(light*0.1));
				if(light>0){
//					points[i].loc.add('z',-.1*light*Math.sin(light*0.1));
					points[i].loc.add('z',-.1*light);
//					points[i].loc.add('x',40*(light*0.1));
//					points[i].setLightingColor(rr, gg, bb);
//					points[i].mixColors();
				}
			}
		}
		
		for(int i=0; i<polies.size(); i++){
			CircularPoint cpp0 = polies.get(i).cp[0];
			CircularPoint cpp1 = polies.get(i).cp[1];
			CircularPoint cpp2 = polies.get(i).cp[2];
			if(cpp0.baseColor!=null){
				double rr=00,gg=00,bb=00;
				double mult = 1;
				rr+=Math.abs(cpp0.loc.getZ()-cpp1.loc.getZ()-10)*mult;
				gg+=Math.abs(cpp0.loc.getZ()-cpp2.loc.getZ()+10)*mult;
				bb+=Math.abs(cpp2.loc.getZ()-cpp1.loc.getZ()-50)*mult;
				cpp0.setLightingColor(rr, gg, bb);
				cpp0.mixColors();
			}
		}

	}
}
