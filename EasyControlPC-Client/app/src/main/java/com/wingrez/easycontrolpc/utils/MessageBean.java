package com.wingrez.easycontrolpc.utils;


import com.google.gson.Gson;

public class MessageBean {

	/*
	 * state
	 * 1：连接成功
	 * 2：接收消息成功
	 * 3：发送状态消息
	 * 4、发送鼠标消息
	 * 5、发送文本消息
	 *
	 * */

	private int state;
	private String message;
	private int moveX;
	private int moveY;
	
	public MessageBean() {
		init();
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
	public int getMoveX() {
		return moveX;
	}
	public void setMoveX(int moveX) {
		this.moveX = moveX;
	}
	public int getMoveY() {
		return moveY;
	}
	public void setMoveY(int moveY) {
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

	public void init() {
		state=0;
		message="";
		moveX=0;
		moveY=0;
	}
	
	
//	public static void main(String[] args) {
//		MessageBean msgBean=new MessageBean();
//		msgBean.setState(200);
//		msgBean.setMessage("Hello world!");
//		System.out.println(msgBean.toJson());
//	}
}
