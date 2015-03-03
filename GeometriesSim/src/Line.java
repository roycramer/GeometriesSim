import java.awt.Graphics;


public class Line {
	CircularPoint start, end;
	
	public Line(CircularPoint startIn, CircularPoint endIn){
		start = startIn;
		end = endIn;
	}
	
	public void draw(Graphics g2){
		if(start.withinScreen()||true){
			g2.setColor(start.sphereColor);
			g2.drawLine((int)start.d.getX(), (int)start.d.getY(), (int)end.d.getX(), (int)end.d.getY());
		}
		
	}
}
