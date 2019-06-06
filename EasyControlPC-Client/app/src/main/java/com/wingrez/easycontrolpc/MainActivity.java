package com.wingrez.easycontrolpc;

import android.annotation.SuppressLint;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
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

    private TextView touchTextView;
    private EditText addressEditText;
    private EditText portEditText;
    private EditText sendMsgEditText;
    private TextView connectTextView;
    private TextView sendMsgTextView;
    private TextView msgTextView;

    double nowX, nowY;
    double preX, preY;
    private int moveX;
    private int moveY;

    Runnable listenRunnable = new Runnable() {
        @Override
        public void run() {
            while (true) {
                try {
                    if (client != null) {
                        MessageBean msgBean = client.receiveMsg();
                        new ListenaTask().execute(msgBean);
                        if (msgBean == null || msgBean.getState() == -1) break;
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initView();
        client = null;
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

        addressEditText.setEnabled(true);
        portEditText.setEnabled(true);

        connectTextView.setOnClickListener(new ConnectListener());
        sendMsgTextView.setOnClickListener(new SendListener());
        touchTextView.setOnTouchListener(new TouchListener());
    }

    //connectTextView的点击事件 连接
    private class ConnectListener implements View.OnClickListener {

        @Override
        public void onClick(View v) {
            if (connectTextView.getText().toString().equals("连接")) {
                connect();
            } else if (connectTextView.getText().toString().equals("断开连接")) {
                shutdown();
            }
        }

        //连接
        private void connect() {
            if (addressEditText.getText().toString().isEmpty() || portEditText.getText().toString().isEmpty()) {
                Toast.makeText(getApplicationContext(), "请填写连接信息", Toast.LENGTH_SHORT).show();
                return;
            }

            address = addressEditText.getText().toString();
            port = Integer.valueOf(portEditText.getText().toString());

            if (!Utils.checkAddressValid(address) || !Utils.checkPortValid(port)) {
                Toast.makeText(getApplicationContext(), "连接信息格式非法", Toast.LENGTH_SHORT).show();
                return;
            }
            connectTextView.setText("正在连接");
            addressEditText.setEnabled(false);
            portEditText.setEnabled(false);

            new ClientTask().execute();
        }

        //关闭连接
        private void shutdown() {
            try {
                if (client != null) {
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            client.sendMsg(new MessageBean(-1, "断开连接", 0, 0));
                        }
                    }).start();
                    client.close();
                }
                client = null;
                connectTextView.setText("连接");
                addressEditText.setEnabled(true);
                portEditText.setEnabled(true);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private class ClientTask extends AsyncTask<Void, Void, MessageBean> {
        @Override
        protected MessageBean doInBackground(Void... voids) {
            try {
                client = new Client(address, port);
                return client.receiveMsg();
            } catch (Exception e) {
                e.printStackTrace();
                return null;
            }
        }

        protected void onPostExecute(MessageBean msgBean) {
            if (msgBean == null || msgBean.getState() != 1) {
                msgTextView.setText(Utils.getTime() + " " + "连接失败！\n" + msgTextView.getText());
                connectTextView.setText("连接");
                addressEditText.setEnabled(true);
                portEditText.setEnabled(true);
            } else {
                msgTextView.setText(Utils.getTime() + " " + "连接成功！\n" + msgTextView.getText());
                msgTextView.setText(Utils.getTime() + " " + "接收到消息：" + msgBean.getMessage() + "\n" + msgTextView.getText());
                connectTextView.setText("断开连接");
                addressEditText.setEnabled(false);
                portEditText.setEnabled(false);
                new Thread(listenRunnable).start();
            }
        }
    }

    private class ListenaTask extends AsyncTask<MessageBean, Void, MessageBean> {
        @Override
        protected MessageBean doInBackground(MessageBean... msgBeans) {
            if (msgBeans == null) return null;
            return msgBeans[0];
        }

        protected void onPostExecute(MessageBean msgBean) {
            if (msgBean != null) {

                msgTextView.setText(Utils.getTime() + " 接收到消息：" + msgBean.getMessage() + "\n" + msgTextView.getText());

                if (msgBean.getState() == -1) {
                    msgTextView.setText(Utils.getTime() + " 服务端已下线！" + "\n" + msgTextView.getText());
                    try {
                        if (client != null) client.close();
                        client = null;
                        connectTextView.setText("连接");
                        addressEditText.setEnabled(true);
                        portEditText.setEnabled(true);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }

                if (msgBean.getState() == 6) {
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            client.sendMsg(new MessageBean(2, "客户端已接收！", 0, 0));
                        }
                    }).start();
                }
            } else {
                msgTextView.setText(Utils.getTime() + " 与服务端失去连接！\n" + msgTextView.getText());
                try {
                    if (client != null) client.close();
                    client = null;
                    connectTextView.setText("连接");
                    addressEditText.setEnabled(true);
                    portEditText.setEnabled(true);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }

    //sendMsgTextView的点击事件
    private class SendListener implements View.OnClickListener {

        @Override
        public void onClick(View v) {
            if (!connectTextView.getText().toString().equals("断开连接") || client == null) {
                Toast.makeText(getApplicationContext(), "未连接", Toast.LENGTH_SHORT).show();
                return;
            }
            if (sendMsgEditText.getText().toString().isEmpty()) {
                Toast.makeText(getApplicationContext(), "请填写要发送的信息", Toast.LENGTH_SHORT).show();
                return;
            }

            new Thread(new Runnable() {
                @Override
                public void run() {
                    client.sendMsg(new MessageBean(6, sendMsgEditText.getText().toString(), 0, 0));
                }
            }).start();
        }
    }

    private class TouchListener implements View.OnTouchListener {

        @Override
        public boolean onTouch(View v, MotionEvent event) {
            if (client == null) {
                Toast.makeText(getApplicationContext(), "未连接", Toast.LENGTH_SHORT).show();
                return false;
            }
            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    nowX = preX = event.getX();
                    nowY = preY = event.getY();
                    break;
                case MotionEvent.ACTION_UP:
                    nowX = event.getX();
                    nowY = event.getY();
                    if (nowX == preX && nowY == preY) {
                        new Thread(new Runnable() {
                            @Override
                            public void run() {
                                client.sendMsg(new MessageBean(5, "鼠标点击", 0, 0));
                            }
                        }).start();
                    }
                    break;
                case MotionEvent.ACTION_MOVE:
                    preX = nowX;
                    preY = nowY;
                    nowX = event.getX();
                    nowY = event.getY();
                    moveX = (int) Math.round(nowX - preX);
                    moveY = (int) Math.round(nowY - preY);
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            client.sendMsg(new MessageBean(4, "鼠标移动", moveX, moveY));
                        }
                    }).start();
                    break;
                default:
                    break;
            }
            return true;
        }
    }
}
