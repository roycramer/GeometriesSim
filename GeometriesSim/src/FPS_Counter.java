import java.awt.Color;
import java.awt.Graphics;
import javax.swing.JPanel;

public class FPS_Counter {
	private long nextSecond = System.currentTimeMillis() + 1000;
	private int frameInLastSecond = 0, framesInCurrentSecond = 0;
	
	public void updateFPS() {
		long currentTime = System.currentTimeMillis();
		if (currentTime > nextSecond) {
//			System.out.println(frameInLastSecond + " fps");
			nextSecond += 1000;
			frameInLastSecond = framesInCurrentSecond;
			framesInCurrentSecond = 0;
		}
		framesInCurrentSecond++;
	}
	
	public void drawFPS(Graphics g, JPanel jp) {
		g.setColor(Color.WHITE);
		g.drawString(frameInLastSecond + " fps", jp.getWidth()/40, jp.getHeight()/2);
	}
	
	public void printFPS(){
		System.out.println(frameInLastSecond + " fps");
	}
	
}
