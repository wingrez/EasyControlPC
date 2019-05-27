package com.wingrez.easycontrolpc;

import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.wingrez.easycontrolpc.utils.Client;
import com.wingrez.easycontrolpc.utils.Utils;

import java.io.IOException;

public class MainActivity extends AppCompatActivity {

    private String address;
    private int port;

    private Client client;

    private EditText addressEditText;
    private EditText portEditText;
    private TextView connectTextView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        InitView();

    }

    private void InitView() {
        setContentView(R.layout.activity_main);
        addressEditText=(EditText)findViewById(R.id.addressEditText);
        portEditText=(EditText)findViewById(R.id.portEditText);
        connectTextView=(TextView)findViewById(R.id.connectTextView);
        connectTextView.setOnClickListener(new ConnectListener());
        connectTextView.setClickable(true);
        addressEditText.setEnabled(true);
        portEditText.setEnabled(true);
    }


    public class ConnectListener implements View.OnClickListener {

        @Override
        public void onClick(View v) {
            if(!addressEditText.getText().toString().isEmpty() && !portEditText.getText().toString().isEmpty()){
                address=addressEditText.getText().toString();
                port=Integer.valueOf(portEditText.getText().toString());
                Log.e("address", address);
                Log.e("port", String.valueOf(port));
            }
            else{
                Toast.makeText(getApplicationContext(), "请填写连接信息", Toast.LENGTH_SHORT).show();
                return;
            }

            if(!Utils.checkAddressValid(address) || !Utils.checkPortValid(port)){
                Toast.makeText(getApplicationContext(), "连接信息格式非法", Toast.LENGTH_SHORT).show();
                return;
            }

            connectTextView.setText("正在连接");
            connectTextView.setClickable(false);

            new Thread(new Runnable(){
                @Override
                public void run() {
                    try {
                        client=new Client(address,port);
//                        client.sendMsg("Hello, I am wingrez\n");
                    } catch (Exception e) {
                        Toast.makeText(getApplicationContext(), "连接失败", Toast.LENGTH_SHORT).show();
                        connectTextView.setText("连接");
                        connectTextView.setClickable(true);
                        e.printStackTrace();
                        return;
                    }
                }
            }).start();

            connectTextView.setText("断开连接");
            connectTextView.setClickable(true);
            addressEditText.setEnabled(false);
            portEditText.setEnabled(false);
        }
    }
}
