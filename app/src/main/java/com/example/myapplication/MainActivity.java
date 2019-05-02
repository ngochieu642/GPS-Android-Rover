package com.example.myapplication;

import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.widget.TextView;

import org.json.JSONException;
import org.json.JSONObject;

import java.net.URISyntaxException;

import io.socket.client.IO;
import io.socket.client.Socket;
import io.socket.emitter.Emitter;

public class MainActivity extends FragmentActivity {

    private Socket mSocket;
    private static String host_url = "http://192.168.1.12:3000/"; //local host
//    private static String host_url = "https://gps-server-nodejs.herokuapp.com/";
    private TextView txtMessage, txtSocketID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        txtMessage=findViewById(R.id.txtMessage);
        txtSocketID=findViewById(R.id.txtSocketID);
    }

    @Override
    protected void onPause() {
        mSocket.disconnect();
        super.onPause();
    }

    @Override
    protected void onResume() {
        Connect2Server();
        mSocket.on("server-send-GPS-message",onReceiveGPSData);
        super.onResume();
    }

    private void Connect2Server() {
        try {
            mSocket = IO.socket(host_url);
            mSocket.emit("android-client-connected", "hi");
            mSocket.connect();
            txtSocketID.setText("Socket ID: "+mSocket.toString()+"\nHost: "+ host_url);
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }
    }

    private Emitter.Listener onReceiveGPSData = new Emitter.Listener() {
        @Override
        public void call(final Object... args) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    JSONObject object =(JSONObject)args[0];
                    String data = null; //Dữ liệu nhận được dạng String
                    int count = 0;
                    try {
                        data = object.getString("GPS");
                        count= object.getInt("count");

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    if(data!=null){
                        System.out.println("\n\n\n\nReceive " + data.length()+" bytes from Server");
                        System.out.println(data);
                        txtMessage.setText(data + "\nTimes: "+Integer.toString(count));
                    }
                }
            });
        }
    };
}
