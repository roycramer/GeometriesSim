
public class Vector {
	private double[] c = {0,0,0};
	
	private int letterConvert(char c){
		if (c=='x')return 0;
		if (c=='y')return 1;
		if (c=='z')return 2;
		return 3;
	}
	
	public boolean isZero(){
		return (c[0]==0&&c[1]==0&&c[2]==0);
	}
	
	public Vector getNegative(){
		Vector result = new Vector();
		result.setAll(this);
		result.multiplyAll(-1, -1, -1);
		return result;
	}
	
	public double getX(){
		return c[0];
	}
	
	public double getY(){
		return c[1];
	}
	
	public double getZ(){
		return c[2];
	}
	
	public double get(int i){
		return c[i];
	}
	
	public double get(char i){
		return get(letterConvert(i));
	}
	
	public void setX(double xIn){
		c[0]=xIn;
	}
	
	public void setY(double yIn){
		c[1]=yIn;
	}
	
	public void setZ(double zIn){
		c[2]=zIn;
	}
	
	public void set(int i, double in){
		c[i]=in;
	}
	
	public void set(char i, double in){
		set(letterConvert(i), in);
	}
	
	public void add(char i, double in){
		set(letterConvert(i), in+get(i));
	}
	
	public void setAll(double xx, double yy, double zz){
		c[0]=xx;
		c[1]=yy;
		c[2]=zz;
	}
	
	public void addAll(double xx, double yy, double zz){
		c[0]+=xx;
		c[1]+=yy;
		c[2]+=zz;
	}
	
	public void setAll(Vector v){
		c[0]=v.c[0];
		c[1]=v.c[1];
		c[2]=v.c[2];
	}
	
	public void addAll(Vector v){
		addAll(v.c[0],v.c[1],v.c[2]);
	}
	
	public void multiplyAll(double xx, double yy, double zz){
		c[0]*=xx;
		c[1]*=yy;
		c[2]*=zz;
	}
	
	public void multiplyAll(double xx){
		c[0]*=xx;
		c[1]*=xx;
		c[2]*=xx;
	}
	
	public double distManhattan(Vector v){
		if(v==null)v = new Vector();
		int o = 0;
		o+=Math.abs(v.getX()-getX());
		o+=Math.abs(v.getY()-getY());
		o+=Math.abs(v.getZ()-getZ());
		return o;
	}
	
	public double distBox(Vector v){
		if(v==null)v = new Vector();
		double o=0;
		o=Math.max(Math.abs(v.getX()-getX()),o);
		o=Math.max(Math.abs(v.getY()-getY()),o);
		o=Math.max(Math.abs(v.getZ()-getZ()),o);
		return o;
	}
	
	public double distSphereUnscaled(Vector v){
		if(v==null)v = new Vector();
		int o=0;
		o+=Math.pow(v.getX()-getX(), 2);
		o+=Math.pow(v.getY()-getY(), 2);
		o+=Math.pow(v.getZ()-getZ(), 2);
		return o;
	}
	
	public double distSphere(Vector v){
		return Math.sqrt(distSphereUnscaled(v));
	}
	
	public void rotateOnce(double theta, int i1, int i2){
		double rise = get(i1);
		double runn = get(i2);
		double mag = Math.sqrt(runn*runn+rise*rise);
		double ang = Math.atan2(rise, runn)+Math.toRadians(theta);
		set(i1, mag*Math.sin(ang));
		set(i2, mag*Math.cos(ang));
	}
	
	public void rotate (double xIn, double yIn, double zIn){
		if(xIn!=0)rotateOnce(xIn,1,0);
		if(yIn!=0)rotateOnce(yIn,2,1);
		if(zIn!=0)rotateOnce(zIn,2,0);
	}
	
	public void rotate (Vector v){
		rotate(v.getX(),v.getY(),v.getZ());
	}
	
	public String toString(){
		return "||X: "+(int)getX()+"||Y: "+(int)getY()+"||Z: "+(int)getZ()+"||";
	}
}
