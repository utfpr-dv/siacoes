package br.edu.utfpr.dv.siacoes.util;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class DateUtils {

	public static Calendar getToday(){
		Calendar today = Calendar.getInstance();
		today.set(Calendar.HOUR_OF_DAY, 0);
		today.set(Calendar.MINUTE, 0);
		today.set(Calendar.SECOND, 0);
		today.set(Calendar.MILLISECOND, 0);
		
		return today;
	}
	
	public static Calendar getNow(){
		Calendar now = Calendar.getInstance();
		now.set(Calendar.MILLISECOND, 0);
		
		return now;
	}
	
	public static int getSemester(){
		Calendar today = DateUtils.getToday();
		
		if(today.get(Calendar.MONTH) >= 6){
			return 2;
		}else{
			return 1;
		}
	}
	
	public static int getSemester(Date date){
		Calendar cal = Calendar.getInstance();
		cal.setTime(date);
		
		if(cal.get(Calendar.MONTH) >= 6){
			return 2;
		}else{
			return 1;
		}
	}
	
	public static int getYear(){
		Calendar today = DateUtils.getToday();
		
		return today.get(Calendar.YEAR);
	}
	
	public static int getYear(Date date){
		Calendar cal = Calendar.getInstance();
		cal.setTime(date);
		return cal.get(Calendar.YEAR);
	}
	
	public static int getHour(Date date){
		Calendar cal = Calendar.getInstance();
		cal.setTime(date);
		return cal.get(Calendar.HOUR_OF_DAY);
	}
	
	public static Date concat(Date date, Date time){
		Calendar cal = Calendar.getInstance();
		Calendar cal2 = Calendar.getInstance();
		
		cal.setTime(date);
		cal2.setTime(time);
		
		cal.set(Calendar.HOUR_OF_DAY, cal2.get(Calendar.HOUR_OF_DAY));
		cal.set(Calendar.MINUTE, cal2.get(Calendar.MINUTE));
		
		return cal.getTime();
	}
	
	public static String format(Date date, String format){
		try{
			DateFormat df = new SimpleDateFormat(format);
			
			return df.format(date);
		}catch(Exception e){
			return "";
		}
	}
	
	public static Date addMinute(Date date, int minutes){
		Calendar cal = Calendar.getInstance();
		cal.setTime(date);
		
		cal.add(Calendar.MINUTE, minutes);
		
		return cal.getTime();
	}
	
	public static Date addHour(Date date, int hours){
		Calendar cal = Calendar.getInstance();
		cal.setTime(date);
		
		cal.add(Calendar.HOUR_OF_DAY, hours);
		
		return cal.getTime();
	}
	
	public static Date addDay(Date date, int days){
		Calendar cal = Calendar.getInstance();
		cal.setTime(date);
		
		cal.add(Calendar.DAY_OF_YEAR, days);
		
		return cal.getTime();
	}
	
	public static Date getSunday(Date date){
		Calendar c = Calendar.getInstance();
		c.setFirstDayOfWeek(Calendar.SUNDAY);
		c.setTime(date);
		int today = c.get(Calendar.DAY_OF_WEEK);
		c.add(Calendar.DAY_OF_WEEK, -today + Calendar.SUNDAY);
		
		return c.getTime();
	}
	
	public static Date getStartDate(int semester, int year){
		Calendar cal = Calendar.getInstance();
		
		if(semester == 1){
			cal.set(year, 0, 1, 0, 0, 0);
		}else{
			cal.set(year, 7, 1, 0, 0, 0);
		}
		
		return cal.getTime();
	}
	
	public static Date getEndDate(int semester, int year){
		Calendar cal = Calendar.getInstance();
		
		if(semester == 1){
			cal.set(year, 6, 31, 23, 59, 59);
		}else{
			cal.set(year, 11, 31, 23, 59, 59);
		}
		
		return cal.getTime();
	}
	
}
