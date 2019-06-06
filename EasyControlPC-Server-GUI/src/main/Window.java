package main;

import java.awt.EventQueue;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JTextArea;
import javax.swing.JTextField;

import utils.Interaction;
import utils.MessageBean;

import javax.swing.JButton;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.awt.event.ActionEvent;

public class Window {

	private JFrame frame;
	
	private JLabel portLabel;
	private JLabel ipLabel;
	private JLabel sendMsgLabel;
	
	private JTextField ipText;
	private JTextField portText;
	private JTextArea msgText;
	private JTextArea outputText;
	
	private JButton startButton;
	private JButton sendButton;
	
	private Server server;
	
	private SimpleDateFormat dataformat=new SimpleDateFormat("hh:mm:ss");
	
	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					Window window = new Window();
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Create the application.
	 */
	public Window() {
		initialize();
	}

	/**
	 * Initialize the contents of the frame.
	 */
	private void initialize() {
		frame = new JFrame();
		frame.setResizable(false);
		frame.setBounds(100, 100, 300, 500);
		frame.setTitle("EasyControlPC");
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.getContentPane().setLayout(null);
		frame.setVisible(true);
		frame.setResizable(false);

		ipLabel = new JLabel("IP");
		ipLabel.setBounds(30, 10, 30, 30);
		frame.getContentPane().add(ipLabel);
		
		portLabel = new JLabel("端口");
		portLabel.setBounds(30, 40, 30, 30);
		frame.getContentPane().add(portLabel);

		sendMsgLabel = new JLabel("消息");
		sendMsgLabel.setBounds(30, 70, 30, 30);
		frame.getContentPane().add(sendMsgLabel);
		
		ipText = new JTextField(5);
		ipText.setToolTipText("本地IP");
		ipText.setBounds(80, 10, 100, 25);
		ipText.setEditable(false);
		frame.getContentPane().add(ipText);
		ipText.setColumns(10);
		
		portText = new JTextField(5);
		portText.setToolTipText("开放端口号");
		portText.setBounds(80, 40, 100, 25);
		frame.getContentPane().add(portText);
		portText.setColumns(10);
		
		msgText = new JTextArea();
		msgText.setToolTipText("发送给客户端的消息");
		msgText.setLineWrap(true);
		msgText.setBounds(80, 70, 180, 120);
		frame.getContentPane().add(msgText);
		msgText.setColumns(10);

		
		startButton = new JButton("开启服务");
		startButton.setBounds(45, 200, 100, 25);
		frame.getContentPane().add(startButton);
		
		sendButton = new JButton("发送消息");
		sendButton.setBounds(155, 200, 100, 25);
		frame.getContentPane().add(sendButton);
		
		outputText = new JTextArea();
		outputText.setEditable(false);
		outputText.setLineWrap(true);
		outputText.setBounds(0, 240, 294, 232);
		frame.getContentPane().add(outputText);
		
		try {
			InetAddress addr = InetAddress.getLocalHost();
			String ip=addr.getHostAddress().toString();
			ipText.setText(ip);
		} catch (UnknownHostException e1) {
			ipText.setText("本地IP获取失败！");
		}  
        
		
		
		startButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				if(startButton.getText().equals("关闭服务")) {
					try {
						if(server!=null) {
							server.sendMsg(-1,msgText.getText());
							server.close(); 
						}
						outputText.setText(dataformat.format(new Date())+": "+"关闭服务成功！\n"+outputText.getText());
						startButton.setText("开启服务");
						portText.setEditable(true);
					} catch (Exception e) {
						outputText.setText(dataformat.format(new Date())+": "+"关闭服务失败！\n"+outputText.getText());
						return;
					}
				}
				
				else if(startButton.getText().equals("开启服务")){
					int port;
					try {
						port=Integer.parseInt(portText.getText());
						if(port<0 || port>65535) throw new Exception();
					}catch(Exception e){
						outputText.setText(dataformat.format(new Date())+": "+"端口号不合法！\n"+outputText.getText());
						return;
					}
					
					new Thread(new Runnable() {
						@Override
						public void run() {
							server=new Server(port);
							server.listen();
						}
					}).start();
					
					startButton.setText("关闭服务");
					portText.setEditable(false);
				}
				
			}
		});
		
		sendButton.addActionListener(new ActionListener() {
			
			public void actionPerformed(ActionEvent arg0) {
				if(server==null) {
					outputText.setText(dataformat.format(new Date())+": "+"连接未建立！\n"+outputText.getText());
					return;
				}
				if(msgText.getText().isEmpty()) {
					outputText.setText(dataformat.format(new Date())+": "+"消息为空！\n"+outputText.getText());
					return;
				}
				
				server.sendMsg(6,msgText.getText());
				
			}
		});
		
	}
	
	private class Server {
		private ServerSocket serverSocket;
		private Socket server;
		private BufferedReader in;
		private PrintWriter out;
		private MessageBean msgBean;
		private Interaction interaction;
		
		private String remoteIP;
		private int remotePort;

		public Server(int port) {
			msgBean = new MessageBean();
			interaction = new Interaction();

			try {
				serverSocket = new ServerSocket(port);

				outputText.setText(dataformat.format(new Date())+": "+"服务已开启，等待建立连接"+"\n"+outputText.getText());
				
				server = serverSocket.accept();
				// 获取客户端地址和端口信息
				remoteIP = server.getInetAddress().getHostAddress();
				remotePort = server.getLocalPort();
				// 获取客户端的输入输出流
				in = new BufferedReader(new InputStreamReader(server.getInputStream()));
				out = new PrintWriter(server.getOutputStream(), false);
				
				outputText.setText(dataformat.format(new Date())+": "+"客户端上线：" + remoteIP + ":" + remotePort+"\n"+outputText.getText());
				
				// 发送连接成功消息
				sendMsg(new MessageBean(1, "欢迎使用EasyControlPC", 0, 0));
				
			} catch (Exception e) {
				if(server==null) outputText.setText(dataformat.format(new Date())+": "+"开启服务失败。端口被占用。"+"\n"+outputText.getText());
				else outputText.setText(dataformat.format(new Date())+": "+"失去连接！"+"\n"+outputText.getText());
			}

		}
		
		public void listen() {
			// 接收客户端消息
			try {
				while (true) {
					msgBean = receiveMsg();
					if (msgBean != null) {
						outputText.setText(dataformat.format(new Date())+": "+"接收到消息：" + msgBean.getMessage()+"\n"+outputText.getText());

						if (msgBean.getState() == -1) {
							outputText.setText(dataformat.format(new Date())+": "+"客户端退出：" + remoteIP + ":" + remotePort+"\n"+outputText.getText());
							close();
							break;
						}

						if (msgBean.getState() == 4) {
							interaction.move(msgBean.getMoveX(), msgBean.getMoveY());
						}

						else if (msgBean.getState() == 5) {
							interaction.click();
						}

						sendMsg(new MessageBean(2, "服务端已接收！", 0, 0));
					} else {
						outputText.setText(dataformat.format(new Date())+": "+"客户端退出：" + remoteIP + ":" + remotePort+"\n"+outputText.getText());
						close();
						break;
					}
				}
			}catch(Exception e) {
				outputText.setText(dataformat.format(new Date())+": "+"消息接收或发送失败！"+"\n"+outputText.getText());
			}
		}
		
		public void sendMsg(int state, String msg) {
			if (out != null) {
				out.println(new MessageBean(state, msg, 0, 0).toString());
				out.flush();
			}
		}

		public void sendMsg(MessageBean msg) {
			if (out != null) {
				out.println(msg.toString());
				out.flush();
			}
		}

		public MessageBean receiveMsg() throws IOException {
			if (in != null) {
				String data = in.readLine();
				if (data != null)
					return new MessageBean(data);
				return null;
			}
			return null;
		}

		public void close() throws IOException {
			if (out != null)
				out.close();
			if (in != null)
				in.close();
			if (server != null)
				server.close();
			if (serverSocket != null)
				serverSocket.close();
		}
		
	}
}
