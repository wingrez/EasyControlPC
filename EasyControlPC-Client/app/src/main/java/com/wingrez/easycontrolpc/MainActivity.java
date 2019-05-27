package com.wingrez.easycontrolpc;

import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.wingrez.easycontrolpc.utils.Client;
import com.wingrez.easycontrolpc.utils.MessageBean;
import com.wingrez.easycontrolpc.utils.Utils;

public class MainActivity extends AppCompatActivity {

    private String address;
    private int port;

    private Client client;
    private MessageBean msgBean;

    private TextView touchTextView;
    private EditText addressEditText;
    private EditText portEditText;
    private EditText sendMsgEditText;
    private TextView connectTextView;
    private TextView sendMsgTextView;
    private TextView msgTextView;

    double nowX,nowY;
    double preX,preY;
    private int moveX;
    private int moveY;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        client=null;
        msgBean=null;
        initView();
    }

    private void initView() {
        setContentView(R.layout.activity_main);

        touchTextView = (TextView) findViewById(R.id.touchTextView);
        addressEditText = (EditText) findViewById(R.id.addressEditText);
        portEditText = (EditText) findViewById(R.id.portEditText);
        sendMsgEditText = (EditText) findViewById(R.id.sendMsgEditText);
        connectTextView = (TextView) findViewById(R.id.connectTextView);
        sendMsgTextView = (TextView) findViewById(R.id.sendMsgTextView);
        msgTextView = (TextView) findViewById(R.id.msgTextView);

        connectTextView.setClickable(true);
        addressEditText.setEnabled(true);
        portEditText.setEnabled(true);

        connectTextView.setOnClickListener(new ConnectListener());
        sendMsgTextView.setOnClickListener(new SendListener());
        touchTextView.setOnTouchListener(new TouchListener());
    }

    //connectTextView的点击事件
    private class ConnectListener implements View.OnClickListener {

        @Override
        public void onClick(View v) {
            if (connectTextView.getText().toString().equals("连接")) {
                connect();
            }
        }

        private boolean connect() {
            if (addressEditText.getText().toString().isEmpty() || portEditText.getText().toString().isEmpty()) {
                Toast.makeText(getApplicationContext(), "请填写连接信息", Toast.LENGTH_SHORT).show();
                return false;
            }

            address = addressEditText.getText().toString();
            port = Integer.valueOf(portEditText.getText().toString());
            Log.e("address", address);
            Log.e("port", String.valueOf(port));

            if (!Utils.checkAddressValid(address) || !Utils.checkPortValid(port)) {
                Toast.makeText(getApplicationContext(), "连接信息格式非法", Toast.LENGTH_SHORT).show();
                return false;
            }
            connectTextView.setText("正在连接");
            connectTextView.setClickable(false);

            new ClientTask().execute();

            return true;
        }
    }

    private class ClientTask extends AsyncTask<Void, Void, MessageBean> {
        @Override
        protected MessageBean doInBackground(Void... voids) {
            Log.e("test", "doInBackground");
            try {
                client = new Client(address, port);
                return client.receiveMsg();
            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }

        protected void onPostExecute(MessageBean msgBean) {
            Log.e("test", "onPostExecute");
            if (msgBean == null || msgBean.getState() != 1) {
                msgTextView.setText(Utils.getTime() + ": " + "连接失败\n" + msgTextView.getText());
                connectTextView.setText("连接");
                connectTextView.setClickable(true);
                addressEditText.setEnabled(true);
                portEditText.setEnabled(true);
                return;
            }
            msgTextView.setText(Utils.getTime() + ": " + msgBean.getMessage() + "\n" + msgTextView.getText());
            connectTextView.setText("断开连接");
            connectTextView.setClickable(true);
            addressEditText.setEnabled(false);
            portEditText.setEnabled(false);
        }
    }

    private class SendListener implements View.OnClickListener {

        @Override
        public void onClick(View v) {
            if (!connectTextView.getText().toString().equals("断开连接") || client == null) {
                Toast.makeText(getApplicationContext(), "未连接", Toast.LENGTH_SHORT).show();
                return;
            }
            Log.e("test", "SendListener");

            if (sendMsgEditText.getText().toString().isEmpty()) {
                Toast.makeText(getApplicationContext(), "请填写要发送的信息", Toast.LENGTH_SHORT).show();
                return;
            }

            msgBean = new MessageBean();
            msgBean.setState(5); //状态码：发送文本消息
            msgBean.setMessage(sendMsgEditText.getText().toString());
            client.sendMsg(msgBean);

            new ReceiveTask().execute();
        }
    }

    private class ReceiveTask extends AsyncTask<Void, Void, MessageBean> {
        @Override
        protected MessageBean doInBackground(Void... voids) {
            Log.e("test", "doInBackground");
            try {
                return client.receiveMsg();
            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }

        protected void onPostExecute(MessageBean msgBean) {
            Log.e("test", "onPostExecute");
            if (msgBean == null || msgBean.getState() != 2) {
                msgTextView.setText(Utils.getTime() + ": " + "发送失败\n" + msgTextView.getText());
            } else {
                msgTextView.setText(Utils.getTime() + ": " + msgBean.getMessage() + "\n" + msgTextView.getText());
            }
        }

    }

    private class TouchListener implements View.OnTouchListener {

        @Override
        public boolean onTouch(View v, MotionEvent event) {
            if(client==null) return false;
            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    nowX=preX = event.getX();
                    nowY=preY = event.getY();
                    break;
                case MotionEvent.ACTION_MOVE:
                    preX = nowX;
                    preY = nowY;
                    nowX=event.getX();
                    nowY=event.getY();
                    moveX = (int) Math.round(nowX - preX);
                    moveY = (int) Math.round(nowY - preY);
                    client.sendMsg(new MessageBean(4, "鼠标移动事件", moveX, moveY));
                    new ReceiveTask().execute();
                    System.out.println("moveX:" + moveX);
                    System.out.println("moveY:" + moveY);
                    break;
                case MotionEvent.ACTION_UP:
                    nowX=event.getX();
                    nowY=event.getY();
                    if (nowX == preX && nowY == preY) {
                        client.sendMsg(new MessageBean(5, "鼠标点击事件", 0, 0));
                        new ReceiveTask().execute();
                    }
                    break;
                default:
                    break;
            }
            return true;
        }
    }

}
