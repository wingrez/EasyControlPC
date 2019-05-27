package main;

import java.awt.AWTException;
import java.awt.MouseInfo;
import java.awt.Point;
import java.awt.PointerInfo;
import java.awt.Robot;

public class MouseMove {

	private Robot robot;
	
	public MouseMove() {
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
	
	public void setAutoDelay(int ms) {
		robot.setAutoDelay(ms);
	}
	
//	public static void main(String[] args) {
//		MouseMove mouseMove=new MouseMove();
//		mouseMove.setAutoDelay(3000);
//		mouseMove.move(0, 0);
//		mouseMove.move(200, 0);
//		mouseMove.move(0, 200);
//	}
}
