package com.fei.control.utils;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.provider.Settings;
import android.support.v7.app.AlertDialog;


import com.fei.control.ContorlApplication;

import java.io.UnsupportedEncodingException;

/**
 * Created by fei on 2017/12/5.
 */

public class ComUtils {

    /**
     * 十六进制字符串转换成字节数组
     *
     * @param hexString the hex string
     * @return byte[]
     */
    public static byte[] hexStringToBytes(String hexString) {
        if (hexString == null || hexString.equals("")) {
            return null;
        }
        hexString = hexString.replace(" ", "");
        hexString = hexString.toUpperCase();
        int length = hexString.length() / 2;
        char[] hexChars = hexString.toCharArray();
        byte[] d = new byte[length];
        for (int i = 0; i < length; i++) {
            int pos = i * 2;
            d[i] = (byte) (charToByte(hexChars[pos]) << 4 | charToByte(hexChars[pos + 1]));
        }
        return d;
    }
    /**
     * Convert char to byte
     * @param c char
     * @return byte
     */
    private static byte charToByte(char c) {
        return (byte) "0123456789ABCDEF".indexOf(c);
    }
    /**
     * 字节数组转换成十六进制字符串
     *
     * @param src
     * @return
     */
    public static String bytesToHexString(byte[] src, int actualNumBytes) {
        StringBuilder stringBuilder = new StringBuilder("");
        if (src == null || actualNumBytes <= 0) {
            return null;
        }
        for (int i = 0; i < actualNumBytes; i++) {
            int v = src[i] & 0xFF;
            String hv = Integer.toHexString(v);
            if (hv.length() < 2) {
                stringBuilder.append(0);
            }
            stringBuilder.append(hv.toUpperCase());
        }
        return stringBuilder.toString();
    }

    public static String byteToString(byte[] src,int num){
        byte[] ress = new byte[num];
        for(int i=0;i<num;i++){
            ress[i] = src[i];
        }
        String socketRes = null;
        try {
            socketRes = new String(ress,"utf-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return socketRes;
    }

    /**
     * 判断设备是否有网络连接
     * @param context
     * @return
     */
    public static boolean isNetworkConnected(Context context) {
        if (context != null) {
            ConnectivityManager mConnectivityManager = (ConnectivityManager) context
                    .getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo mNetworkInfo = mConnectivityManager.getActiveNetworkInfo();
            if (mNetworkInfo != null) {
                //mNetworkInfo.isAvailable();
                return true;//有网
            }
        }
        return false;//没有网
    }

    //是否连接WIFI
    public static boolean isWifiConnected(Context context)
    {
        ConnectivityManager connectivityManager = (ConnectivityManager)context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo wifiNetworkInfo = connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
        if(wifiNetworkInfo.isConnected())
        {
            return true ;
        }
        return false ;
    }


    /**
     * 提示框
     * @param context
     * @param title
     * @param message
     */
    public static void showTipsDialog(Context context, String title, String message, final int type) {
//        type = 1;跳转到设置界面
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle(title)
                .setMessage(message)
                .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        Intent intent = new Intent();
                        switch(type){
                            case 1:
                                intent.setAction(Settings.ACTION_SETTINGS);
                                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                ContorlApplication.getMIntent().startActivity(intent);
                                break;
                        }
                    }
                })
                .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        // User cancelled the dialog
                    }
                });
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    public static String getIpOrPort(String flag){
        SharedPreferences preferences = ContorlApplication.getMIntent().getSharedPreferences("sHSet",0);
        switch (flag){
            case "ip" :
                return preferences.getString("ip","192.168.1.5");
            case "port" :
                return preferences.getString("Port","29");
        }
        return null;
    }
    public static void saveIpOrPort(String ip,String port){
        SharedPreferences preferences = ContorlApplication.getMIntent().getSharedPreferences("sHSet",0);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString("ip",ip);
        editor.putString("Port",port);
        editor.commit();
    }


    public static String changeString(String flag,int num){
        if(!StringUtils.isEmpty(flag)){
            if(flag.length() < num){
                flag = "0"+flag;
                return changeString(flag,num);
            }else{
                return flag;
            }
        }else{
            return null;
        }
    }

}
