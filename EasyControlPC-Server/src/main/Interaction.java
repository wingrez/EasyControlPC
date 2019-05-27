package main;

import java.awt.AWTException;
import java.awt.MouseInfo;
import java.awt.Point;
import java.awt.PointerInfo;
import java.awt.Robot;
import java.awt.event.KeyEvent;

public class Interaction {

	private Robot robot;
	
	public Interaction() {
		try {
			robot=new Robot();
			
		} catch (AWTException e) {
			e.printStackTrace();
		}
	}
	
	public void move(int x,int y) {
		PointerInfo pinfo = MouseInfo.getPointerInfo();
		Point p = pinfo.getLocation();
		int now_x = (int)p.getX();
		int now_y = (int)p.getY();
		robot.mouseMove(now_x+x, now_y+y);
	}
	
	public void click() {
		robot.mousePress(KeyEvent.BUTTON1_DOWN_MASK);
		robot.mouseRelease(KeyEvent.BUTTON1_DOWN_MASK);
	}
	
}
