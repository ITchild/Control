package com.fei.control.activity;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.fei.control.R;
import com.fei.control.utils.ComUtils;
import com.fei.control.utils.StringUtils;

public class SettingActivity extends AppCompatActivity{

    private EditText setting_Ip_et;
    private EditText setting_port_et;
    private Button setting_save_bt;
    private Button setting_cancle_bt;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);
        initView();
        initData();
        initListener();

    }


    private void initView(){
        setting_Ip_et = findViewById(R.id.setting_Ip_et);
        setting_port_et = findViewById(R.id.setting_port_et);
        setting_save_bt = findViewById(R.id.setting_save_bt);
        setting_cancle_bt = findViewById(R.id.setting_cancle_bt);
    }


    private void initData(){
        setting_Ip_et.setText(ComUtils.getIpOrPort("ip"));
        setting_port_et.setText(ComUtils.getIpOrPort("port"));
    }


    private void initListener(){
        setting_save_bt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveIpAndPort();
            }
        });
        setting_cancle_bt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }


    private void saveIpAndPort(){
        String ip = setting_Ip_et.getText().toString();
        if(StringUtils.isEmpty(ip)){
            StringUtils.showToast("请输入Ip地址");
            return;
        }
        String port = setting_port_et.getText().toString();
        if(StringUtils.isEmpty(port)){
            StringUtils.showToast("请输入端口号");
            return;
        }
        ComUtils.saveIpOrPort(ip,port);
        StringUtils.showToast("保存成功");
        finish();
    }

}
