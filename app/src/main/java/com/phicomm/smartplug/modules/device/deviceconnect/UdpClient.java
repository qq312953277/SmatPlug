package com.phicomm.smartplug.modules.device.deviceconnect;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;

import com.phicomm.smartplug.constants.AppConstants;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;

/**
 * Created by feilong.yang on 2017/8/7.
 */

public class UdpClient implements Runnable {
    private DatagramSocket mSocket = null;
    private boolean isFinished = false;
    public static final int HOST_PORT = 7550;
    public static final String HOST_IP = "192.168.4.1";

    private byte receiveData[] = new byte[1024];
    private final int TIMEOUT = 5000;
    private String mSendHexString;
    private UdpClientConnector.ConnectListener mListener;

    Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case AppConstants.UDP_CONNECT_SUCCESS:
                    if (null != mListener) {
                        mListener.onReceiveData(msg.getData().getString("data"));
                    }
                    break;
                case AppConstants.UDP_CONNECT_FAILED:
                    if (null != mListener) {
                        mListener.onSoTimeout();
                    }
                    break;
            }
        }
    };

    public UdpClient(String message, UdpClientConnector.ConnectListener listener) {
        super();
        mListener = listener;
        sendStr(message);
    }

    //更改UDP生命线程
    public void setUdpFinished(boolean b) {
        isFinished = b;
    }

    //发送消息
    public void send() {
        try {
            InetAddress serverAddress = InetAddress.getByName(HOST_IP);
            byte data[] = mSendHexString.getBytes("utf-8");
            DatagramPacket sendPacket = new DatagramPacket(data, data.length, serverAddress, HOST_PORT);
            //需要延时2000ms
            Thread.sleep(2000);
            mSocket.send(sendPacket);
        } catch (Exception e) {
        }
    }

    @Override
    public void run() {
        try {
            mSocket = new DatagramSocket();
            mSocket.setSoTimeout(TIMEOUT);
        } catch (SocketException e) {
            e.printStackTrace();
        }
        DatagramPacket receivePacket = new DatagramPacket(receiveData, receiveData.length);
        while (!isFinished) {
            try {
                mSocket.receive(receivePacket);
                Message msg = mHandler.obtainMessage();
                msg.what = AppConstants.UDP_CONNECT_SUCCESS;
                Bundle bundle = new Bundle();
                bundle.putString("data", new String(receivePacket.getData()));
                msg.setData(bundle);
                mHandler.sendMessage(msg);
            } catch (IOException e) {
                //5s请求超时重复发送指令
                Message msg = mHandler.obtainMessage();
                msg.what = AppConstants.UDP_CONNECT_FAILED;
                mHandler.sendMessage(msg);
            }
        }
        mSocket.close();
    }

    /**
     * 发送数据
     *
     * @param str
     */
    public void sendStr(final String str) {
        mSendHexString = str;
    }

    public void releaseListener() {
        if (null != mListener) {
            mListener = null;
        }
    }

}
