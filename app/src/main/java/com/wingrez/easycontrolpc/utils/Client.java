package com.wingrez.easycontrolpc.utils;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.Socket;

public class Client {

    private Socket socket;
    private InputStream is;
    private OutputStream os;
    private BufferedReader br;
    private PrintWriter pw;


    public Client(String address, int port) throws Exception{
            socket=new Socket(address,port);
            os=socket.getOutputStream();
            pw=new PrintWriter(os);
            is=socket.getInputStream();
            br=new BufferedReader(new InputStreamReader(is));
    }

    public void sendMsg(String msg){
            pw.write(msg);
            pw.flush();
    }

    public String receiveMsg() throws Exception {
        return br.readLine();
    }

    public void close() throws Exception {
        if(br!=null) br.close();
        if(is!=null) is.close();
        if(pw!=null) pw.close();
        if(os!=null) os.close();
        if(socket!=null) socket.close();
    }


}
