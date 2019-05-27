package main;

import com.google.gson.Gson;

public class MessageBean {

	private int state;
	private String message;
	private double moveX;
	private double moveY;
	
	public MessageBean() {
		
	}
	
	public MessageBean(String json) {
		MessageBean msgBean=fromJson(json);
		this.setState(msgBean.getState());
		this.setMessage(msgBean.getMessage());
		this.setMoveX(msgBean.getMoveX());
		this.setMoveY(msgBean.getMoveY());
	}
	
	public int getState() {
		return state;
	}
	public void setState(int state) {
		this.state = state;
	}
	public String getMessage() {
		return message;
	}
	public void setMessage(String message) {
		this.message = message;
	}
	public double getMoveX() {
		return moveX;
	}
	public void setMoveX(double moveX) {
		this.moveX = moveX;
	}
	public double getMoveY() {
		return moveY;
	}
	public void setMoveY(double moveY) {
		this.moveY = moveY;
	}
	
	private String toJson(){
		return new Gson().toJson(this);
	}
	
	public MessageBean fromJson(String json) {
		return new Gson().fromJson(json, MessageBean.class);
	}
	
	public String toString() {
		return toJson();
	}
	
	
//	public static void main(String[] args) {
//		MessageBean msgBean=new MessageBean();
//		msgBean.setState(200);
//		msgBean.setMessage("Hello world!");
//		System.out.println(msgBean.toJson());
//	}
}
