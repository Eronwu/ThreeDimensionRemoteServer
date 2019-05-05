package com.woo.threedimensionremoteserver;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.UiThread;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.net.Inet6Address;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.Enumeration;

public class MainActivity extends AppCompatActivity {
    public static String mData = null;
    private static TextView mTextViewData;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mTextViewData = findViewById(R.id.text_view_data);
        TextView boxIp = findViewById(R.id.text_view_server_ip);
        String s = getHostIP();
        if (s != null)
            boxIp.setText(s);

        Button button = findViewById(R.id.button_enter);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                startService(new Intent(MainActivity.this, ThreeDimensionRemoteServer.class));
                startActivity(new Intent(MainActivity.this, TestServerActivity.class));
                Toast.makeText(MainActivity.this, "Start Server", Toast.LENGTH_SHORT).show();
            }
        });

//        final Handler handler = new Handler() {
//            @Override
//            public void handleMessage(Message msg) {
//                switch (msg.what) {
//                    case 1:
//                        mTextViewData.setText(mData);
//                        break;
//                }
//            }
//        };
//
//        new Thread() {
//            @Override
//            public void run() {
//                Message msg = Message.obtain();
//                while (true) {
//                    if (mData != null) {
//                        msg.what = 1;
//                        handler.sendMessage(msg);
//                    }
//                    try {
//                        sleep(100);
//                    } catch (InterruptedException e) {
//                        e.printStackTrace();
//                    }
//                }
//            }
//        }.start();
    }


    @Override
    protected void onStop() {
//        stopService(new Intent(MainActivity.this, ThreeDimensionRemoteServer.class));
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    /**
     * 获取ip地址
     * @return
     */
    public static String getHostIP() {

        String hostIp = null;
        try {
            Enumeration nis = NetworkInterface.getNetworkInterfaces();
            InetAddress ia = null;
            while (nis.hasMoreElements()) {
                NetworkInterface ni = (NetworkInterface) nis.nextElement();
                Enumeration<InetAddress> ias = ni.getInetAddresses();
                while (ias.hasMoreElements()) {
                    ia = ias.nextElement();
                    if (ia instanceof Inet6Address) {
                        continue;// skip ipv6
                    }
                    String ip = ia.getHostAddress();
                    if (!"127.0.0.1".equals(ip)) {
                        hostIp = ia.getHostAddress();
                        break;
                    }
                }
            }
        } catch (SocketException e) {
            Log.i("yao", "SocketException");
            e.printStackTrace();
        }
        return hostIp;

    }
}
