package com.fei.control.activity;

import android.content.Intent;
import android.os.Build;
import android.os.Handler;
import android.os.Message;
import android.os.StrictMode;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.fei.control.R;
import com.fei.control.socket.SocketClient;
import com.fei.control.socket.SocketConfigtion;
import com.fei.control.socket.SocketEventBean;
import com.fei.control.utils.ComUtils;
import com.fei.control.utils.StringUtils;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

public class MainActivity extends AppCompatActivity {

    private Button main_buttonUp_bt;
    private Button main_buttonDown_bt;
    private TextView main_setting_tv;
    private TextView main_state_tv;

    private SocketClient socketClient;
    private boolean isTopTwo = false;
    private boolean isButtomTwo = false;

    private Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what){
                case 999 :  //NW
                    if(isTopTwo){
                        sendCommand("06A6010101FF");
                        isTopTwo = false;
                    }
                    break;
                case 1000 : //SE
                    if(isButtomTwo){
                        sendCommand("06A6020101FF");
                        isButtomTwo = false;
                    }
                    break;
            }
        }
    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //EventBus进行注册
        EventBus.getDefault().register(this);
        initView();
        initData();
        initListener();
    }

    private void initView(){
        main_buttonUp_bt = findViewById(R.id.main_buttonUp_bt);
        main_buttonDown_bt = findViewById(R.id.main_buttonDown_bt);
        main_setting_tv = findViewById(R.id.main_setting_tv);
        main_state_tv = findViewById(R.id.main_state_tv);
    }

    private void initData(){
        socketClient = new SocketClient();
        if (ComUtils.isWifiConnected(this)) {
            disCon();
            //获取指令的tcp连接
            socketClient.Con();
        } else {
            ComUtils.showTipsDialog(this, "提示", "Wifi网络未连接，请开启", 1);
        }
    }

    private void initListener(){
        main_buttonUp_bt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(isTopTwo){
                    isTopTwo = false; //双击
                    sendCommand("06A6010102FF");
                }else {
                    isTopTwo = true;
                    handler.sendEmptyMessageDelayed(999, 300);
                }
            }
        });

        main_buttonDown_bt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(isButtomTwo){
                    isButtomTwo = false; //双击
                    sendCommand("06A6020102FF");
                }else {
                    isButtomTwo = true;
                    handler.sendEmptyMessageDelayed(1000, 300);
                }
            }
        });
        main_setting_tv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this,SettingActivity.class);
                startActivity(intent);
            }
        });
    }


    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void MAIN_Click(SocketEventBean bean){
        switch (bean.getFlag()) {
            case SocketConfigtion.SOCKET_CON_SUCCESS:
                sendCommand("06AA0101AAFF");
                break;
            case SocketConfigtion.SOCKET_CON_FAIL:
                StringUtils.showToast("TCP/IP连接失败");
                main_state_tv.setBackground(getResources().getDrawable(R.drawable.red_round));
//                loadingDialog.cancel();
                break;
            case SocketConfigtion.SOCKET_SENDSUCCESS:
                StringUtils.showLog("发送成功");
//                loadingDialog.cancel();
                break;
            case SocketConfigtion.SOCKET_SENDFIAL:
                StringUtils.showLog("发送失败");
//                loadingDialog.cancel();
                break;
            case SocketConfigtion.SOCKET_MESSAGE://接受消息
//                analysisData(bean.getMsg());
                if(bean.getMsg().equals("06AA0101BBFF")){
                    StringUtils.showToast("TCP/IP连接成功");
                    main_state_tv.setBackground(getResources().getDrawable(R.drawable.green_round));
                }
                break;
        }
    }


    private void sendCommand(final String cmd){
        new Thread(new Runnable() {
            @Override
            public void run() {
                socketClient.sendNetCommand(ComUtils.hexStringToBytes(cmd));
            }
        }).start();
    }

    private void disCon() {
        //结束SOCKET
        if(socketClient != null){
            socketClient.closeSocket();
            socketClient.client = null;
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        disCon();
        //解除订阅
        EventBus.getDefault().unregister(this);
    }

}
