package main;

import java.io.*;
import java.net.*;


public class Server {
	private ServerSocket serverSocket;
	private Socket server;
	private BufferedReader in;
	private PrintWriter out;
	private MessageBean msgBean;
	
	public Server() {
		msgBean=new MessageBean();
		
		try {
			serverSocket = new ServerSocket(8888);
			System.out.println("等待建立连接");
			server = serverSocket.accept();
			// 获取客户端地址和端口信息
			String remoteIP = server.getInetAddress().getHostAddress();
			int remotePort = server.getLocalPort();
			// 获取客户端的输入输出流
			in = new BufferedReader(new InputStreamReader(server.getInputStream()));
			out = new PrintWriter(server.getOutputStream(), false);
			System.out.println("客户端上线：" + remoteIP + ":" + remotePort);
			System.out.println();
			//发送连接成功消息
			msgBean.setState(200);
			msgBean.setMessage("连接成功！");
			sendMsg(msgBean);
			
			// 接收客户端消息
			while(true) {
				msgBean=receiveMsg();
				if(msgBean!=null) {
					System.out.println("接收到消息："+msgBean.toString());
					msgBean.setState(200);
					msgBean.setMessage("服务端已接收");
					sendMsg(msgBean);
				}
				
			}
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		
	}
	
	public void sendMsg(MessageBean msg) {
		if(out!=null) {
			out.println(msg.toString());
			out.flush();
		}
	}
	
	public MessageBean receiveMsg() throws IOException {
		if(in!=null) {
			String data=in.readLine();
			if(data!=null) return new MessageBean(data);
			return null;
		}
		return null;
	}
	
	public void close() throws IOException {
		if(out!=null) out.close();
		if(in!=null) in.close();
		if(server!=null) server.close();
		if(serverSocket!=null) server.close();
	}

//	public static void main(String[] args) {
//		new Server();
//	}
}
