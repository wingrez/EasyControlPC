package main;

import com.google.gson.Gson;

public class MessageBean {

	/*
	 * state
	 * -1：断开连接
	 * 1：连接成功
	 * 2：接收消息成功
	 * 3：发送状态消息
	 * 4、发送鼠标移动消息
	 * 5、发送鼠标点击消息
	 * 6、发送文本消息
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

	public MessageBean(int state, String message, int moveX, int moveY){
		this.state=state;
		this.message=message;
		this.moveX=moveX;
		this.moveY=moveY;
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

}
