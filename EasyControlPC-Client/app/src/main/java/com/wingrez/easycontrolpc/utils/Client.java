package com.wingrez.easycontrolpc.utils;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.Socket;

public class Client {

    private Socket socket;
    private InputStream is;
    private OutputStream os;
    private BufferedReader in;
    private PrintWriter out;


    public Client(String address, int port) throws Exception {
        socket = new Socket();
        socket.connect(new InetSocketAddress(address, port), 5000);
        os = socket.getOutputStream();
        out = new PrintWriter(new OutputStreamWriter(os,"UTF-8"),false);
        is = socket.getInputStream();
        in = new BufferedReader(new InputStreamReader(is, "UTF-8"));
    }

    public void sendMsg(MessageBean msg) {
        if (out != null) {
            out.println(msg.toString());
            out.flush();
        }
    }

    public MessageBean receiveMsg() throws Exception {
        if (in != null) {
            String data = in.readLine();
            if (data != null) return new MessageBean(data);
            return null;
        }
        return null;
    }

    public void close() throws Exception {
        if (in != null) in.close();
        if (is != null) is.close();
        if (out != null) out.close();
        if (os != null) os.close();
        if (socket != null) socket.close();
    }


}
