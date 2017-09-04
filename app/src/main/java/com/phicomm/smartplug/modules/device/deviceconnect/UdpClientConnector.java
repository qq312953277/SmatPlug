package com.phicomm.smartplug.modules.device.deviceconnect;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Created by yangf on 2017/7/10.
 */

public class UdpClientConnector {
    private static UdpClientConnector mUdpClientConnector;

    private UdpClient client = null;

    public interface ConnectListener {
        void onReceiveData(String data);

        void onSoTimeout();
    }

    public static UdpClientConnector getInstance() {
        if (mUdpClientConnector == null) {
            mUdpClientConnector = new UdpClientConnector();
        }
        return mUdpClientConnector;
    }

    public void createConnector(String message, ConnectListener listener) {
        ExecutorService exec = Executors.newCachedThreadPool();
        client = new UdpClient(message, listener);
        exec.execute(client);
        send();
    }

    //发送消息
    public void send() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                client.send();
            }
        }).start();
    }

    public void setUdpFinished(boolean b) {
        client.setUdpFinished(b);
        releaseResource();
    }

    public void releaseResource() {
        client.releaseListener();
    }

}
