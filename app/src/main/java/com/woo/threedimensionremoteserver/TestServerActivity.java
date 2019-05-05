package com.woo.threedimensionremoteserver;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.TextView;

import java.io.IOException;
import java.io.InputStream;
import java.net.ServerSocket;
import java.net.Socket;

public class TestServerActivity extends AppCompatActivity {
    private final String TAG = "TestServerActivity";
    private final int PORT_NUM = 6666;
    private ServerSocket mServerSocket;
    private InputStream mInputStream;
    private Socket mSocket;
    private TextView mTextViewData, mTextViewClient;
    private String mData;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test_server);

        mTextViewData = findViewById(R.id.text_view_data);
        mTextViewClient = findViewById(R.id.text_view_client);

        try {
            mServerSocket = new ServerSocket(PORT_NUM);
            SocketAcceptThread socketAcceptThread = new SocketAcceptThread();
            socketAcceptThread.start();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    class SocketAcceptThread extends Thread {
        @Override
        public void run() {
            try {
                Log.d(TAG, "run: wait mServerSocket.accept");
//                Toast.makeText(TestServerActivity.this, "wait socket accept", Toast.LENGTH_SHORT).show();
                mSocket = mServerSocket.accept();
                if (mSocket != null) {
                    Log.d(TAG, "run: mServerSocket.accept ok" + mSocket.getRemoteSocketAddress().toString());
//                    TestServerActivity.this.runOnUiThread(new Runnable() {
//                        @Override
//                        public void run() {
//                            String s = mSocket.getRemoteSocketAddress().toString();
//                            mTextViewClient.setText(s);
//                        }
//                    });
                    //TODO : post in handler
//                Toast.makeText(TestServerActivity.this, "socket accept ok", Toast.LENGTH_SHORT).show();
                    SocketDataReadThread socketDataReadThread = new SocketDataReadThread();
                    socketDataReadThread.start();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    class SocketDataReadThread extends Thread {
        @Override
        public void run() {
            try {
                mInputStream = mSocket.getInputStream();
                while (true) {
                    if (mInputStream != null) {
                        byte[] data = new byte[10];
                        while (mInputStream.read(data) != -1) {
                        }
                        if (data != null)
                            parseData(data);
                        else Log.e(TAG, "run: data null!");
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void parseData(byte[] data) {
        int x = 0, y = 0;
        // HEAD: 0:X 1:Y
        if (data[0] == 0)
            x = dataBytes2Int(data);
        else if (data[0] == 1)
            y = dataBytes2Int(data);
        mData = x + " " + y;
        Log.d(TAG, "parseData: " + mData);

//        TestServerActivity.this.runOnUiThread(new Runnable() {
//            @Override
//            public void run() {
//                if (mData != null)
//                    mTextViewData.setText(mData);
//                else Log.e(TAG, "run: mData == null!");
//            }
//        });

    }

    public static int dataBytes2Int(byte[] bytes) {
        int num = bytes[4] & 0xFF;
        num |= ((bytes[3] << 8) & 0xFF00);
        num |= ((bytes[2] << 16) & 0xFF0000);
        num |= ((bytes[1] << 24) & 0xFF0000);
        return num;
    }

    @Override
    public void onDestroy() {
        try {
            if (mInputStream != null)
                mInputStream.close();
            if (mSocket != null)
                mSocket.close();
            if (mServerSocket != null)
                mServerSocket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        super.onDestroy();
    }
}
