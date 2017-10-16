package com.zxz.utils;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

public class DateUtils {

	/**得到指定格式的时间
	 * @param date
	 * @param pattern  yyyy/MM/dd hh:mm:ss
	 * @return
	 */
	public static String getFormatDate(Date date,String pattern) {
		DateFormat sdf2 = new SimpleDateFormat(pattern);		
		String sdate = sdf2.format(date);
		return sdate;
	}
	/**得到yyyy-MM-dd HH:mm:ss:SSS格式的时间
	 * @param date
	 * @param pattern  yyyy/MM/dd hh:mm:ss
	 * @return
	 */
	public static String getDateByFormat(Date date) {
		String pattern = "yyyy-MM-dd HH:mm:ss:SSS";
		DateFormat sdf2 = new SimpleDateFormat(pattern);		
		String sdate = sdf2.format(date);
		return sdate;
	}
	/**
	 * 得到游戏持续时间
	 * @param date
	 * @return
	 */
	public static String getTimeLast(Date dateStart,Date dateEnd) {
		 long time = dateEnd.getTime() - dateStart.getTime();
//		 long time = 1000;
	     long days = time / (1000 * 60 * 60 * 24);
	     long hours = (time - days * (1000 * 60 * 60 * 24)) / (1000 * 60 * 60);
	     long minutes = (time - days * (1000 * 60 * 60 * 24) - hours * (1000 * 60 * 60)) / (1000 * 60);
	     long seconds = (time - days * (1000 * 60 * 60 * 24) - hours * (1000 * 60 * 60) - minutes * (1000 * 60)) / (1000);
         
		return days +"天 "+hours+":"+minutes+":"+seconds;
	}
	
	/**得到当前的时间
	 * @return
	 */
	public static String getCurrentDate(){
		SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss:SSS");
		String formatStr =formatter.format(new Date());
		return formatStr;
	}
	public static void main(String[] args) {
		String timeLast = getTimeLast(new Date(), new Date());
		System.out.println(timeLast);
	}

}
