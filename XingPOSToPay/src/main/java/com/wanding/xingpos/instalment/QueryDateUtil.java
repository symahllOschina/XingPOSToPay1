package com.wanding.xingpos.instalment;

import android.util.Log;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class QueryDateUtil {

    /**
     * 根据指定日期获取时间戳
     */
    public static String getStartTime(String dateStr){
        String sysDateStr = "";
        String timeStr = "000000";
        String year = dateStr.split("-")[0];
        String month = dateStr.split("-")[1];
        String day = dateStr.split("-")[2];
        sysDateStr = year+month+day+timeStr;
        Log.e("生成的开始时间参数",sysDateStr);
        return sysDateStr;

    }

    /**
     * 根据指定日期获取时间戳
     */
    public static String getEndTime(String dateStr){
        String sysDateStr = "";
        SimpleDateFormat sdf = new SimpleDateFormat("HHmmss", Locale.CHINA);
        String timeStr = sdf.format(new Date());
        String year = dateStr.split("-")[0];
        String month = dateStr.split("-")[1];
        String day = dateStr.split("-")[2];
        sysDateStr = year+month+day+timeStr;

        Log.e("生成的结束时间参数",sysDateStr);
        return sysDateStr;

    }
}
