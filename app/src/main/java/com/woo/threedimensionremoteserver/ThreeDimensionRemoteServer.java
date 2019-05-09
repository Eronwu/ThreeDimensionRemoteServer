package com.woo.threedimensionremoteserver;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log;

import java.io.IOException;
import java.io.InputStream;
import java.net.ServerSocket;
import java.net.Socket;

public class ThreeDimensionRemoteServer extends Service {
    private final String TAG = "RemoteServer";
    private final int PORT_NUM = 6666;
    private ServerSocket mServerSocket;
    private InputStream mInputStream;
    private Socket mSocket;
    private RemoteJNI remoteJNI;

    @Override
    public void onCreate() {
        super.onCreate();

        try {
            mServerSocket = new ServerSocket(PORT_NUM);
            SocketAcceptThread socketAcceptThread = new SocketAcceptThread();
            socketAcceptThread.start();
        } catch (IOException e) {
            e.printStackTrace();
        }

        remoteJNI = new RemoteJNI();
        int ret = remoteJNI.initVirtualMouse();
        Log.d(TAG, "onCreate: init virtual mouse:" + ret);
    }

    class SocketAcceptThread extends Thread {
        @Override
        public void run() {
            try {
                Log.d(TAG, "run: wait mServerSocket.accept");
                mSocket = mServerSocket.accept();
                Log.d(TAG, "run: mServerSocket.accept ok");
                SocketDataReadThread socketDataReadThread = new SocketDataReadThread();
                socketDataReadThread.start();
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
                byte[] data = new byte[10];
                while (true) {
                    if (mInputStream != null) {
                        while (mInputStream.read(data) != -1) {
                        }
                        parseData(data);
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
        String s = x + " " + y;
        Log.d(TAG, "parseData: " + s);
        MainActivity.mData = s;

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
            if (mServerSocket != null)
                mServerSocket.close();
            if (mSocket != null)
                mSocket.close();
            if (mInputStream != null)
                mInputStream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        super.onDestroy();
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}
