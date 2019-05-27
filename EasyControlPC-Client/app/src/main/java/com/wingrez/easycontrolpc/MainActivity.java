package com.wingrez.easycontrolpc;

import android.os.AsyncTask;
import android.os.Handler;
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
import com.wingrez.easycontrolpc.utils.MessageBean;
import com.wingrez.easycontrolpc.utils.Utils;

import java.io.IOException;

public class MainActivity extends AppCompatActivity {

    private String address;
    private int port;

    private Client client;
    private MessageBean msgBean;

    private EditText addressEditText;
    private EditText portEditText;
    private TextView connectTextView;
    private TextView msgTextView;


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
        msgTextView=(TextView)findViewById(R.id.msgTextView);
        connectTextView.setOnClickListener(new ConnectListener());
        connectTextView.setClickable(true);
        addressEditText.setEnabled(true);
        portEditText.setEnabled(true);
    }


    public class ConnectListener implements View.OnClickListener {

        @Override
        public void onClick(View v) {
            if(connectTextView.getText().toString().equals("连接")){
                if(connect()==true){

                }
            }
        }

        private boolean connect(){
            if(addressEditText.getText().toString().isEmpty() || portEditText.getText().toString().isEmpty()){
                Toast.makeText(getApplicationContext(), "请填写连接信息", Toast.LENGTH_SHORT).show();
                return false;
            }

            address=addressEditText.getText().toString();
            port=Integer.valueOf(portEditText.getText().toString());
            Log.e("address", address);
            Log.e("port", String.valueOf(port));

            if(!Utils.checkAddressValid(address) || !Utils.checkPortValid(port)){
                Toast.makeText(getApplicationContext(), "连接信息格式非法", Toast.LENGTH_SHORT).show();
                return false;
            }
            connectTextView.setText("正在连接");
            connectTextView.setClickable(false);

            new ClientTask().execute();

            return true;
        }
    }

    private class ClientTask extends AsyncTask<Void, Void ,MessageBean>{
        @Override
        protected MessageBean doInBackground(Void... voids) {
            try {
                client=new Client(address, port);
                return client.receiveMsg();
            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }

        protected void onPostExecute(MessageBean msgBean){
            msgTextView.setText(msgBean.toString());

            if(msgBean.getState()==200){
                connectTextView.setText("断开连接");
                connectTextView.setClickable(true);
                addressEditText.setEnabled(false);
                portEditText.setEnabled(false);
            }

            else {
                connectTextView.setText("连接");
                connectTextView.setClickable(true);
                addressEditText.setEnabled(true);
                portEditText.setEnabled(true);
            }
        }
    }
}
