import java.awt.Container;

import javax.swing.JFrame;
import javax.swing.JTextField;


public class MainGeo {
	public static void main(String[] args) {
		JFrame w = new JFrame("3D Test");
		w.setSize(1080,720);
		w.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		Container c = w.getContentPane();
		SceneGraphic p = new SceneGraphic(c);
		c.add(p);
		w.setVisible(true);
	}
}
