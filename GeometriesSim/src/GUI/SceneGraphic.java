import java.awt.AWTException;
import java.awt.Color;
import java.awt.Container;
import java.awt.Cursor;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.MouseInfo;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.Robot;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;

import javax.imageio.ImageIO;
import javax.swing.JPanel;
import javax.swing.Timer;



public class SceneGraphic extends JPanel implements ActionListener,
		MouseListener, KeyListener, MouseWheelListener {

	public SceneGraphic(Container c) {
		try {
			image2 = ImageIO.read(new File("doge.png"));
		} catch (IOException e1) {
			e1.printStackTrace();
		}
		Timer clock = new Timer(4, this);
		clock.start();
		addKeyListener(this); addMouseListener(this); addMouseWheelListener(this);
		setBackground(Color.BLACK);
		for(int i = 0; i<pc.length; i++){
			pc[i]=new Cube(cubeQuality,cubeSize);
			pc[i].canMove=false;
			painter1.add(pc[i]);
		}
//		painter1.add(hills);
//		everythingEver.add(xyzAxis);
//		painter1.add(teapot);
//		painter1.add(twoChains);
//		twoChains.override=true;
		try {marvin = new Robot();} catch (AWTException e) {e.printStackTrace();}
	}
	Font big = new Font("Arial",Font.BOLD,72);
	Robot marvin;
	public static int ww = 500, hh = 500;
	boolean[] letterKeys = new boolean[26];
	boolean keyZ, keyX, keyW, keyA, keyS, keyD, keyF, keyR, shiftKey, keyUp, keyDown, keyLeft, keyRight, keyC, keyV, ms1;
	int xMs, yMs, xMsClicked, yMsClicked, time, blockCount = 10, cubeSize = 1000, cubeQuality = 2, scrollPower = 100;
	double xMsChange, yMsChange, zRot, turnSpeed = 0.4;
	Vector playerFacing = new Vector();
	ArrayList<PointCloud> painter1 = new ArrayList<PointCloud>();
	BufferedImage image = (BufferedImage)createImage(getWidth(), getHeight());
	BufferedImage image2;
	

	
	
	Cube[] pc = new Cube[40];
	Cube twoChains = new Cube(2,1);
	CustomizableMesh teapot = new CustomizableMesh(40000);
	PointCloud player = new PointCloud(1);
	HeightMap hills = new HeightMap(10000);
	TrueAxis xyzAxis = new TrueAxis(1);
	FPS_Counter fps = new FPS_Counter();
	Cursor invisible = getToolkit().createCustomCursor(new BufferedImage(1, 1, BufferedImage.TYPE_INT_ARGB), new Point(0, 0), "null");
	Color[][] grid;
	ArrayList<Poly> scenePolies = new ArrayList<Poly>();
	
	public void paintComponent(Graphics g) {
		super.paintComponent(g);
		Graphics2D g2 = (Graphics2D)g;
//		g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,RenderingHints.VALUE_ANTIALIAS_ON);
//		for(int i=0; i<painter1.size(); i++)painter1.get(i).draw(g2);
//		for(int i=0; i<scenePolies.size(); i++)scenePolies.get(i).draw(g2);
		int range = 2;
		grid = new Color[getWidth()/range+1][getHeight()/range+1];
		for(int i=scenePolies.size()-1; i>=0; i--){
			Poly cal = scenePolies.get(i);
			Rectangle r = cal.shape.getBounds();
			int x1 = Math.max(r.x,0);
			int x2 = Math.min(r.x+r.width, getWidth());
			int y1 = Math.max(r.y,0);
			int y2 = Math.min(r.y+r.height, getHeight());
			for(int x = x1; x<x2;x+=range){
				int xr = x+range/2;	
				for(int y = y1; y<y2;y+=range){
				int yr = y+range/2;
					if(cal.cp[0].d.getZ()>0 && grid[x/range][y/range]==null&&cal.shape.contains(xr,yr)&&cal.cp[0].isVisible){
							grid[x/range][y/range] = cal.getColor();
//							grid[x/range][y/range] = cal.getColor(x,y,image2);
					}	
				}
			}
		}
		
		for(int x = 0; x<getWidth();x+=range){
			for(int y = 0; y<getHeight();y+=range){
				Color c = grid[x/range][y/range];
				if(c!=null){
					g2.setColor(c);
					g2.fillRect(x, y, range, range);
				}
			}
		}
		fps.updateFPS();
//		fps.printFPS();
		fps.drawFPS(g2, this);
		showUI(g2);
		g2.finalize();
		g2.dispose();
	}

	public void actionPerformed(ActionEvent e) {
		
		Point mouse = MouseInfo.getPointerInfo().getLocation();
		xMs = (int) mouse.getX();
		yMs = (int) mouse.getY();
		
		if(!shiftKey){
			setCursor(invisible);
			marvin.mouseMove(ww/2, hh/2);
			xMsChange=ww/2-xMs;
			yMsChange=hh/2-yMs;
		}
		else{
			setCursor(Cursor.getDefaultCursor());
			xMsChange=0;
			yMsChange=0;
		}
		hh = getHeight(); ww = getWidth();
		playerActions();
		if(keyV){
			hills.earthBending();
		}
		if(ms1){
			Cube x =new Cube(cubeQuality,cubeSize);
			x.loc.setAll(player.loc);
			painter1.add(x);
		}
		if(!shiftKey)update();
		time++;
		repaint();
		fps.updateFPS();
//		fps.printFPS();
		
	}
	
	public void update(){
		scenePolies = new ArrayList<Poly>();
		
		for(int i=0; i<painter1.size(); i++){
			painter1.get(0).loc.setAll(0, 0, -50000);
			PointCloud actor = painter1.get(i);
			
			if(actor.withinScreen)
				scenePolies.addAll(actor.polies);
			if(actor.getClass()==HeightMap.class){
				((HeightMap) actor).iterate();
			}
			else if(actor.getClass()==Cube.class){
				if(actor==twoChains){
					actor.loc.setAll(player.loc);
				}
				else if(actor.canMove){
					double xxx = 1000;
					if(keyUp)   actor.vel.add('z', -xxx);
					if(keyDown) actor.vel.add('z', xxx);
					if(keyLeft) actor.vel.add('x', -xxx);
					if(keyRight)actor.vel.add('x', xxx);
					if(keyC)    actor.vel.add('y', -xxx);
					if(keyV)    actor.vel.add('y', xxx);
					actor.customProperties(keyR, keyF, player);
					actor.repel(player,7000,-0.001);
				}
				else{
					for(int j=0; j<actor.polies.size(); j++){
						actor.loc.setAll(j*100, 0, 0);
//						actor.polies.get(j).cp[0].isVisible=false;
					}
				}
			}
			actor.boundToWalls();
			actor.stayAbove(hills);
		}
		painter1.get(0).loc.setAll(0, 0, -30000);
		for(int i=0; i<painter1.size(); i++){
			PointCloud actor = painter1.get(i);
			for(int j=0; j<painter1.size(); j++){
				PointCloud actor2 = painter1.get(j);
				if(actor2.getClass()==Cube.class){
					if(i!=j){
						actor.bounce(actor2);
						actor.repelCube(actor2);
					}
//					if(i-1==j){
//						actor.boundTo(actor2);
//					}
				}
			}
		}
		
		for(int i=0; i<painter1.size(); i++){
			PointCloud actor = painter1.get(i);
			actor.setPerspectiveCenter(ww/2, hh/2, 5000);
			actor.setCam(player);
			actor.rCamera.setAll(player.rLocal);
			actor.drawCalc();
		}
		Collections.sort(scenePolies);
//		Collections.sort(painter1);
	}

	public void showUI(Graphics g) {
		
		g.drawString(painter1.size()+" ", ww*1/8, 3*hh/4);
		if(shiftKey){
			g.setFont(big);
			g.setColor(Color.WHITE);
			g.drawString("The game is paused.", ww*1/4, hh/2);
			g.setColor(Color.BLACK);
			g.drawString("The game is paused.", ww*1/4+1, hh/2+1);
		}
	}
	
	public void playerActions(){
		player.stayAbove(hills);
		player.rLocal.addAll(xMsChange*turnSpeed, -yMsChange*turnSpeed, 0);
		int playerSpeed = -500;
		boolean moving = keyW||keyS||keyA||keyD;
		if(moving){
			playerFacing.setAll(0,0,0);
			if(keyW)playerFacing.add('y', +playerSpeed);
			if(keyS)playerFacing.add('y', -playerSpeed);
			if(keyA)playerFacing.add('x', +playerSpeed);
			if(keyD)playerFacing.add('x', -playerSpeed);
		}
		Vector temp = new Vector();
		temp.setAll(playerFacing);
		temp.rotate(-player.rLocal.getX(),0,0);
		
		int floor = -2000;
		if(player.loc.getZ()>floor)player.loc.set('z',floor);
		if(keyZ)player.vel.add('z', -100);
		else player.vel.add('z', 100);
		player.vel.multiplyAll(1, 1, .8); //air friction of sorts.
		player.boundToWalls();
		player.loc.addAll(player.vel);
		if(moving)player.loc.addAll(temp);
		
		
		//camera limits, ex. can't look more upward than 0 degrees.
		if(player.rLocal.getX()<-360)player.rLocal.add('x',360);
		if(player.rLocal.getY()>0)player.rLocal.setY(0);
		if(player.rLocal.getY()<-180)player.rLocal.setY(-180);
	}
	public void mouseDragged(MouseEvent e) {}
	public void mouseMoved(MouseEvent e) {}
	public void mouseEntered(MouseEvent e) {}
	public void mouseExited(MouseEvent e) {}
	public void mouseReleased(MouseEvent e) {
		if(e.getButton()==MouseEvent.BUTTON1)ms1=!true;
	}
	public void mouseClicked(MouseEvent e) {}

	public void mousePressed(MouseEvent e) {
		this.requestFocus();
		if(e.getButton()==MouseEvent.BUTTON1)ms1=true;
	}

	public void keyPressed(KeyEvent k) {
		int code = k.getKeyCode();
		if(code==KeyEvent.VK_Z)keyZ=true;
		if(code==KeyEvent.VK_X)keyX=true;
		if(code==KeyEvent.VK_W)keyW=true;
		if(code==KeyEvent.VK_A)keyA=true;
		if(code==KeyEvent.VK_S)keyS=true;
		if(code==KeyEvent.VK_D)keyD=true;
		if(code==KeyEvent.VK_F)keyF=true;
		if(code==KeyEvent.VK_R)keyR=true;
		if(code==KeyEvent.VK_SHIFT)shiftKey=!shiftKey;
		
		if(code==KeyEvent.VK_UP)keyUp=true;
		if(code==KeyEvent.VK_DOWN)keyDown=true;
		if(code==KeyEvent.VK_LEFT)keyLeft=true;
		if(code==KeyEvent.VK_RIGHT)keyRight=true;
		
		if(code==KeyEvent.VK_C)keyC=true;
		if(code==KeyEvent.VK_V)keyV=true;
		
	}

	public void keyReleased(KeyEvent k) {
		int code = k.getKeyCode();
		if(code==KeyEvent.VK_Z)keyZ=!true;
		if(code==KeyEvent.VK_X)keyX=!true;
		if(code==KeyEvent.VK_W)keyW=!true;
		if(code==KeyEvent.VK_A)keyA=!true;
		if(code==KeyEvent.VK_S)keyS=!true;
		if(code==KeyEvent.VK_D)keyD=!true;
		if(code==KeyEvent.VK_F)keyF=!true;
		if(code==KeyEvent.VK_R)keyR=!true;
		
		if(code==KeyEvent.VK_UP)keyUp=!true;
		if(code==KeyEvent.VK_DOWN)keyDown=!true;
		if(code==KeyEvent.VK_LEFT)keyLeft=!true;
		if(code==KeyEvent.VK_RIGHT)keyRight=!true;
		
		if(code==KeyEvent.VK_C)keyC=!true;
		if(code==KeyEvent.VK_V)keyV=!true;
	}

	public void keyTyped(KeyEvent k) {}

	public void mouseWheelMoved(MouseWheelEvent e) {
		int code = e.getWheelRotation();
		if(code>0)player.loc.add('z', scrollPower);
		if(code<0)player.loc.add('z', -scrollPower);
	}
}