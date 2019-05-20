package com.woo.threedimensionremoteserver;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import java.io.IOException;
import java.io.InputStream;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.ServerSocket;
import java.net.Socket;

public class ThreeDimensionRemoteServer extends Service {
    private final String TAG = "RemoteServer";
    private final int PORT_NUM = 6666;
    private RemoteJNI remoteJNI;
    private DatagramSocket mDatagramSocket;
    private int x = 0, y = 0;
    private boolean isThreadRun = false;

    @Override
    public void onCreate() {
        super.onCreate();

        remoteJNI = new RemoteJNI();
        int ret = remoteJNI.initVirtualMouse();
        Log.d(TAG, "onCreate: init virtual mouse:" + ret);
        ThreeDimensionRemoteServer.SocketAcceptThread socketAcceptThread = new ThreeDimensionRemoteServer.SocketAcceptThread();
        socketAcceptThread.start();

        // TODO Heart beat log
    }

    class SocketAcceptThread extends Thread {
        @Override
        public void run() {
            try {
                Log.d(TAG, "run: wait mServerSocket.accept");

                mDatagramSocket = new DatagramSocket(PORT_NUM);
                if (mDatagramSocket != null) {
                    mDatagramSocket.setBroadcast(true);
                    ThreeDimensionRemoteServer.SocketDataReadThread socketDataReadThread = new ThreeDimensionRemoteServer.SocketDataReadThread();
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
                byte[] data;
                int readSize;
                DatagramPacket datagramPacket = new DatagramPacket(buffer, buffer.length);
                while (isThreadRun) {
                    if (mDatagramSocket != null) {
                        mDatagramSocket.receive(datagramPacket);
                        data = datagramPacket.getData();
                        readSize = data.length;
                        if (readSize == 0) continue;
                        parseData(data);
                    }
                    sleep(1);
                }
                Log.d(TAG, "run: socket client exit!");
            } catch (IOException e) {
                e.printStackTrace();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
    int ret;
    byte[] bx = new byte[4];
    byte[] by = new byte[4];

    private void parseData(byte[] data) {
        switch (data[0]) {
            case 0:
                for (int i = 0; i < 4; i++) {
                    bx[i] = data[i + 1];
                    by[i] = data[i + 5];
                }
                x = dataBytes2Int(bx);
                y = dataBytes2Int(by);
                Log.d(TAG, "parseData: " + x + " " + y);

                ret = remoteJNI.setMoveRel(x, y);
//                Log.d(TAG, "onCreate: set virtual mouse:" + ret);
                break;
            case 1:
                if (data[1] == 0) {
                    ret = remoteJNI.setLeftClick();
                    Log.d(TAG, "parseData: set left click" + ret);
                } else if (data[1] == 1) {
                    ret = remoteJNI.setRightClick();
                    Log.d(TAG, "parseData: set right click" + ret);
                }
                break;
            default:
                return;
        }
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

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}
