
public class SpeedTest {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		double sx = Math.random()-0.5;
		double sy = Math.random()-0.5;
		double sz = Math.random()-0.5;
		float f = System.nanoTime();
		for(int i=0; i<10000000; i++){
			double ix = Math.cos(Math.atan2(sz, sx));
			double iy = Math.cos(Math.atan2(sz, sy));
		}
		float a1 = System.nanoTime()-f;
		System.out.println(a1);
		f=System.nanoTime();
		for(int i=0; i<10000000; i++){
			double ix = Math.signum(sx)/Math.sqrt(sz*sz/sx/sx+1);
			double iy = Math.signum(sy)/Math.sqrt(sz*sz/sy/sy+1);
		}
		float a2 = System.nanoTime()-f;
		System.out.println(a2);
		System.out.println((a1/a2)+" times faster performing ten billion calculations!");
		//191 to 199 times faster, though with small amounts it becomes less grand.
	}

}
