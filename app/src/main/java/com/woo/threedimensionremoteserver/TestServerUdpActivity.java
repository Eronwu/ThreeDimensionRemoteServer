package com.woo.threedimensionremoteserver;

import android.app.Instrumentation;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.TextView;

import java.io.IOException;
import java.io.InputStream;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.ServerSocket;
import java.net.Socket;

public class TestServerUdpActivity extends AppCompatActivity {
    private final String TAG = "TestServerActivity";
    private final int PORT_NUM = 6666;
    private DatagramSocket mDatagramSocket;
    private String mData;
    private int x = 0, y = 0;
    private RemoteJNI remoteJNI;
    private boolean isThreadRun = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test_server);

        remoteJNI = new RemoteJNI();
        int ret = remoteJNI.initVirtualMouse();
        Log.d(TAG, "onCreate: init virtual mouse:" + ret);
        SocketAcceptThread socketAcceptThread = new SocketAcceptThread();
        socketAcceptThread.start();

        // TODO Heart beat log
    }

    class SocketAcceptThread extends Thread {
        @Override
        public void run() {
            try {
                Log.d(TAG, "run: wait mServerSocket.accept");

                mDatagramSocket = new  DatagramSocket (PORT_NUM);
                if (mDatagramSocket != null) {
                    mDatagramSocket.setBroadcast(true);
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
                isThreadRun = true;
                byte buffer[] = new byte[108];
                DatagramPacket datagramPacket = new DatagramPacket(buffer , buffer.length);
                while (isThreadRun) {
                    if (mDatagramSocket != null) {
                        mDatagramSocket.receive(datagramPacket);
                        byte[] data;
                        data = datagramPacket.getData();
                        int readSize = data.length;
                        if(readSize == 0) continue;
                            parseData(data);
                    }
                    sleep(100);
                }
                Log.d(TAG, "run: socket client exit! pls connect again!");
                // TODO toast
//                SocketAcceptThread socketAcceptThread = new SocketAcceptThread();
//                socketAcceptThread.start();
            } catch (IOException e) {
                e.printStackTrace();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    private void parseData(byte[] data) {
        byte[] bx = new byte[4];
        byte[] by = new byte[4];
        for (int i = 0; i < 4; i++) {
            bx[i] = data[i];
            by[i] = data[i + 4];
        }
            x = dataBytes2Int(bx);
            y = dataBytes2Int(by);
        mData = x + " " + y;
        Log.d(TAG, "parseData: " + mData);

        int ret = remoteJNI.setMoveRel(x, y);
        Log.d(TAG, "onCreate: set virtual mouse:" + ret);
    }

    public static int dataBytes2Int(byte[] bytes) {
        int num = bytes[3] & 0xFF;
        num |= ((bytes[2] << 8) & 0xFF00);
        num |= ((bytes[1] << 16) & 0xFF0000);
        num |= ((bytes[0] << 24) & 0xFF000000);

        return num;
    }

    @Override
    public void onDestroy() {
        isThreadRun = false;
        if (mDatagramSocket != null)
            mDatagramSocket.close();
        remoteJNI.closeVirtualMouse();
        super.onDestroy();
    }
}
