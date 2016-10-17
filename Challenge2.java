import java.applet.*;
import java.awt.*;
import java.awt.event.*;

public class Challenge2 extends Applet
{
	double x, y;
	double velX, velY;
	double gravity = 0.003;
	double mGrav = 75.0;
	int height, width;
	boolean mButton1 = false;
	boolean mButton2 = false;
	int mX, mY;
	int radius = 3;
	long latestTime;
	updateThread myThread;
	boolean running = true;
	double elastic = 0.5;
	double friction = 0.995;
	long lastFail;
	long score;
	long highScore;
	
	private class updateThread extends Thread {
		public void run()
		{
			while(running) {
				try {
					updateBall();
				} catch(InterruptedException e) {};
			}
		}
	}
	
	public void paint (Graphics g)
	{
		g.drawString("o", (int)Math.round(x) - radius, (int)Math.round(y) + radius);
		g.drawString("DON'T LET IT TOUCH THE SIDES               score: " + String.valueOf((double)score / 1000.0), 100, 100);
		g.drawString("high score: " + String.valueOf((double)highScore / 1000.0), 800, 100);
		g.drawRect(0,0,width - 1,height - 1);
	}
	
	public void init()
	{
		height = getSize().height;
		width = getSize().width;
		
		x = width / 2;
		y = height / 2;
		
		latestTime = System.currentTimeMillis();
		
		addMouseListener(new MouseAdapter() {
			public void mousePressed(MouseEvent press) {
				
				if(press.getButton() == MouseEvent.BUTTON1)
					mButton1 = true;
				else if(press.getButton() == MouseEvent.BUTTON3)
					mButton2 = true;
			}
			
			public void mouseReleased(MouseEvent release) {
				if(release.getButton() == MouseEvent.BUTTON1)
					mButton1 = false;
				else if(release.getButton() == MouseEvent.BUTTON3)
					mButton2 = false;
			}
		});
		addMouseMotionListener(new MouseAdapter() {
			public void mouseMoved(MouseEvent move) {
				mX = move.getX();
				mY = move.getY();
			}
			
			public void mouseDragged(MouseEvent drag) {
				mX = drag.getX();
				mY = drag.getY();
			}
		});
		myThread = new updateThread();
		myThread.start();
	}
	
	public void updateBall() throws InterruptedException
	{
		double accelX = 0.0;
		double accelY = gravity;
		
		if(mButton1 || mButton2) {
			double accelM = mGrav / Math.pow(Math.sqrt(Math.pow(x - mX, 2) + Math.pow(y - mY, 2)), 2);
			double angle = Math.atan2(mY - y, mX - x);
			double accelMX = accelM * Math.cos(angle);
			double accelMY = accelM * Math.sin(angle);
			
			if(mButton1) {
				accelX += accelMX;
				accelY += accelMY;
			} else {
				accelX -= accelMX;
				accelY -= accelMY;
			}
		}
		
		long prevTime = latestTime;
		latestTime = System.currentTimeMillis();
		int interval = (int)(latestTime - prevTime);
		
		double uX = velX;
		double uY = velY;
		
		velX += accelX * interval;
		velY += accelY * interval;
		
		if((x - radius <= 0 && velX < 0) || (x + radius >= width && velX > 0)) {
			velX *= -1.0 * elastic;
			velY *= friction;
			lastFail = System.currentTimeMillis();
		}
		
		if((y - radius <= 0 && velY < 0) || (y + radius >= height && velY > 0)) {
			velY *= -1.0 * elastic;
			velX *= Math.pow(friction, interval);
			lastFail = System.currentTimeMillis();
		}
		
		
	//	x += velX * interval;
	//	y += velY * interval;
	
		x += 0.5 * (uX + velX) * interval;
		y += 0.5 * (uY + velY) * interval;
		
		score = System.currentTimeMillis() - lastFail;
		if(score > highScore && lastFail != 0)
			highScore = score;
		
		repaint();
		Thread.sleep(0);
	}
}