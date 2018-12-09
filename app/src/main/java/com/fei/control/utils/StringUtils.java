package com.fei.control.utils;

import android.util.Log;
import android.widget.Toast;

import com.fei.control.ContorlApplication;

/**
 * Created by fei on 2017/12/5.
 */

public class StringUtils {


    public static void showToast(String flag){
        Toast.makeText(ContorlApplication.getMIntent(),flag,Toast.LENGTH_SHORT).show();
    }

    public static void showLog(String flag){
        if(ContorlApplication.getMIntent().isDebug()){
            Log.i("fei",flag);
        }
    }

    public static boolean isEmpty(String flag){
        if(flag == null){
            return true;
        }else if(flag.equals("") || flag.equals("null") || flag.equals("NULL")){
            return true;
        }else{
            return false;
        }
    }

}
