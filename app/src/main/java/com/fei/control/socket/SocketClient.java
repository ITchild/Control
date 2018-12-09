package com.fei.control.socket;

import android.util.Log;

import com.fei.control.utils.ComUtils;
import com.fei.control.utils.StringUtils;

import org.greenrobot.eventbus.EventBus;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketAddress;

public class SocketClient {

    public Socket client = null;
    private String site;
    private int port;
    private SocketEventBean bean = new SocketEventBean();

    public SocketClient() {
    }

    /**
     * 进行调用的外部的方法
     */
    public void Con(){
        new Thread(netRunnable).start();
    }

    private void ConToService() {
        try {
            // 客户端 Socket 可以通过指定 IP 地址或域名两种方式来连接服务器端,实际最终都是通过 IP 地址来连接服务器
            // 新建一个Socket，指定其IP地址及端口号
            client = new Socket();
            site  = ComUtils.getIpOrPort("ip");
            port = Integer.parseInt(ComUtils.getIpOrPort("port"));
            SocketAddress socAddress = new InetSocketAddress(site, port);
            StringUtils.showLog("Client is created! site:"+site+" port:"+port);
            // 客户端socket在接收数据时，有两种超时：1. 连接服务器超时，即连接超时；2. 连接服务器成功后，接收服务器数据超时，即接收超时
            //设置服务器连接超时
            client.connect(socAddress, 5000);
            // 设置 socket 读取数据流的超时时间
//		    client.setSoTimeout(5000);
            // 发送数据包，默认为 false，即客户端发送数据采用 Nagle 算法；
            // 但是对于实时交互性高的程序，建议其改为 true，即关闭 Nagle 算法，客户端每发送一次数据，无论数据包大小都会将这些数据发送出去
            client.setTcpNoDelay(true);
            // 设置客户端 socket 关闭时，close() 方法起作用时延迟 30 秒关闭，如果 30 秒内尽量将未发送的数据包发送出去
//		    client.setSoLinger(true, 30);
            // 设置输出流的发送缓冲区大小，默认是4KB，即4096字节
            client.setSendBufferSize(4096);
//		// 设置输入流的接收缓冲区大小，默认是4KB，即4096字节
//		    client.setReceiveBufferSize(4096);
            // 作用：每隔一段时间检查服务器是否处于活动状态，如果服务器端长时间没响应，自动关闭客户端socket
            // 防止服务器端无效时，客户端长时间处于连接状态
            client.setKeepAlive(true);
            bean.setFlag(SocketConfigtion.SOCKET_CON_SUCCESS);
            EventBus.getDefault().post(bean);
        } catch (IOException e) {
            e.printStackTrace();
            bean.setFlag(SocketConfigtion.SOCKET_CON_FAIL);
            EventBus.getDefault().post(bean);
        }
    }

    private Runnable netRunnable = new Runnable() {
        @Override
        public void run() {
            // TODO Auto-generated method stub
            closeSocket();
            BufferedInputStream is = null;
            //连接服务器
            ConToService();
            try {
                is = new BufferedInputStream(getInputStream());
            } catch (Exception e) {
                return;
            }
            byte[] buffer = new byte[1024];
            while (client != null) {
                int ret = -1;
                try {
                    ret = is.read(buffer);
                } catch (IOException e) {
                    e.printStackTrace();
                    ret = -1;
                }
                if (ret >= 0) {
                    netReciverSendData(buffer,ret);
                } else {
                    StringUtils.showLog("服务器数据接收出错了，正在自己调整!");
                    //进行重新连接
//                    new Thread(netRunnable).start();
                    //跳出循环
                    break;
                }
            }
        }
    };
    /**
     * 服务器的数据解析
     * @param data
     * @param ret
     */
    private void netReciverSendData(byte[] data,int ret) {
        byte[] res = null;
        int resnum = 0;
        int j = 0;
        for (int i = 0; i < ret; i++) {
            if (j == 0) {
                resnum = data[i] & 0xFF;
                res = new byte[resnum];
            }
            if (j < resnum) {
                res[j] = data[i];
            }
            if (j == (resnum - 1)) {
                j = 0;
                String resString = ComUtils.bytesToHexString(res, res.length);
                //发送广播
                bean.setFlag(SocketConfigtion.SOCKET_MESSAGE);
                StringUtils.showLog(resString);
                bean.setMsg(resString);
                bean.setNum(ret);
                EventBus.getDefault().post(bean);
            } else {
                j++;
            }
        }
    }
    /**
     * 发送指令
     */
    public void sendNetCommand(byte[] data) {
        if (null == client) {
            //进行服务器的重新连接
            new Thread(netRunnable).start();
            SocketEventBean bean = new SocketEventBean();
            bean.setFlag(SocketConfigtion.SOCKET_SENDFIAL);
            EventBus.getDefault().post(bean);
            return;
        }
        try {
            OutputStream out = client.getOutputStream();
            out.write(data);
//            out.flush();
            Log.i("TEST", data + "     发送给服务器成功");
            SocketEventBean bean = new SocketEventBean();
            bean.setFlag(SocketConfigtion.SOCKET_SENDSUCCESS);
            EventBus.getDefault().post(bean);
        } catch (Exception e) {
            Log.i("TEST", "给服务器发送信息失败，正在进行重新连接"+e.toString());
            //进行服务器的重新连接
            new Thread(netRunnable).start();
            SocketEventBean bean = new SocketEventBean();
            bean.setFlag(SocketConfigtion.SOCKET_SENDFIAL);
            EventBus.getDefault().post(bean);
        }
    }


    public void closeSocket() {
        try {
            if (client != null) {
                if (!client.isInputShutdown()) {
                    client.shutdownInput();
                }
                if (!client.isOutputShutdown()) {
                    client.shutdownOutput();
                }
                client.close();
                client = null;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public InputStream getInputStream() {
        if (client != null) {
            try {
                return client.getInputStream();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    public OutputStream getOutputStream() {
        if (client != null) {
            try {
                return client.getOutputStream();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return null;
    }

}
