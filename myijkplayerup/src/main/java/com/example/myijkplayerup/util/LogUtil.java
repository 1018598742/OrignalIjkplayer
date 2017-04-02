package com.example.myijkplayerup.util;

import android.util.Log;

/**
 * Created by Administrator on 2017/3/28.
 */

public class LogUtil {
    private static boolean isDebug = true;
    public static void i(String tag,String info){
        if (isDebug) {
            Log.i(tag,info);
        }
    }
    public static void i(String info){
       if (isDebug){
           Log.i("MyLogUtil",info);
       }
    }
    public static void i2(String info){
        if (isDebug){
            Log.i("MyLogUtil2",info);
        }
    }

}
