package main;

import java.io.*;
import java.net.*;


public class Server {
	private ServerSocket server;
	private Socket client;
	private BufferedReader in;
	private PrintWriter out;
	private MessageBean msgBean;
	
	public Server() {
		msgBean=new MessageBean();
		
		try {
			server = new ServerSocket(8888);
			System.out.println("等待建立连接");
			client = server.accept();
			// 获取客户端地址和端口信息
			String remoteIP = client.getInetAddress().getHostAddress();
			int remotePort = client.getLocalPort();
			// 获取客户端的输入输出流
			in = new BufferedReader(new InputStreamReader(client.getInputStream()));
			out = new PrintWriter(client.getOutputStream(), false);
			System.out.println("客户端上线：" + remoteIP + ":" + remotePort);
			System.out.println();
			//发送连接成功消息
			msgBean.setState(200);
			msgBean.setMessage("连接成功！");
			sendMsg(msgBean);
			
//			while(true) {
//				msgBean=receiveMsg();
//				if(msgBean!=null) System.out.println(msgBean.toString());
//				else break;
//			}
			
			close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		
	}
	
	public void sendMsg(MessageBean msg) {
		if(out!=null) {
			out.println(msg.toString());
		}
	}
	
	public MessageBean receiveMsg() throws IOException {
		if(in!=null) {
			String data=in.readLine();
			return new MessageBean(data);
		}
		return null;
	}
	
	public void close() throws IOException {
		if(out!=null) out.close();
		if(in!=null) in.close();
		if(client!=null) client.close();
		if(server!=null) server.close();
	}

	public static void main(String[] args) {
		new Server();
	}
}
