package com.wanding.xingpos.util;

import android.annotation.SuppressLint;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * 日期工具类
 */
public class DateUtil {
	
	/** 获取系统时间 */
	@SuppressLint("SimpleDateFormat") 
	public static String getSystemTime(){
		
		SimpleDateFormat formatter = new SimpleDateFormat("yyyyMMddHHmmss");     
		Date curDate = new Date(System.currentTimeMillis()); 
		return formatter.format(curDate);  
	}
	
	/*
	 * 将时间转换为时间戳
	 */
	public static String dateToStamp(String time) throws ParseException {
	    SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyyMMddHHmmss");
	    Date date = simpleDateFormat.parse(time);
	    long ts = date.getTime();
	    return String.valueOf(ts);
	}
	
	/*
	 * 将时间戳转换为时间
	 */
	public static String stampToDate(long timeMillis){
	    SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	    Date date = new Date(timeMillis);
	    return simpleDateFormat.format(date);
	}
	
	
	/**
     * 根据日期获得星期
     * @param date
     * @return
     */
	public static String getWeekOfDate(Date date) {
		String[] weekDaysName = { "星期日", "星期一", "星期二", "星期三", "星期四", "星期五", "星期六" };
		String[] weekDaysCode = { "0", "1", "2", "3", "4", "5", "6" };
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(date);
		int intWeek = calendar.get(Calendar.DAY_OF_WEEK) - 1;
		return weekDaysName[intWeek];
	}
	
	/**
	 * 根据指定日期获取N天后的日期
	 * dateStr : 指定返回的日期String
	 */
	public static String getDateStr(int dayNum){
		String dateStr="";
		SimpleDateFormat formatDate = new SimpleDateFormat("yyyy-MM-dd");  //字符串转换
		Calendar c = Calendar.getInstance();  
	     //new Date().getTime();这个是获得当前电脑的时间，你也可以换成一个随意的时间
		 c.setTimeInMillis(new Date().getTime());
		 c.add(Calendar.DATE, dayNum);//天后的日期
		 Date date= new Date(c.getTimeInMillis()); //将c转换成Date
		 dateStr=formatDate.format(date);
		return dateStr;
	}
	
	/**
	 * 判断系统日期时间是否在指定日期时间段内
	 * beginHour：起始时间小时数  如早上05点 beginHour=05
	 * endHour ： 结束日期时间段   如晚上20点 endHour=20
	 */
	public static boolean isTimeSlot(int beginHour,int endHour){
		boolean isTime=false;
		//起始日期时间段：
		Date beginDate =new Date();
		beginDate.setHours(beginHour);
		beginDate.setSeconds(0);
		beginDate.setMinutes(0);
		 //结束日期时间段
		Date endDate =new Date();
		endDate.setHours(endHour);
		endDate.setSeconds(0);
		endDate.setMinutes(0);
		 //系统时间
		SimpleDateFormat fmt = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
//		System.out.println(fmt.format(beginDate));
		Date now =new Date();
		//系统时间在起始时间之后并且在结束时间之前
		if(now.after(beginDate) && now.before(endDate)){
			isTime = true;
		}
		return isTime;
	}

}
