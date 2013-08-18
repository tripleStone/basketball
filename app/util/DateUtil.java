package util;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import play.Logger;

public class DateUtil {

	private static final  SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
	private static final  SimpleDateFormat sdf1 = new SimpleDateFormat("MM-dd-yyyy");
	private static final  SimpleDateFormat sdf24 = new SimpleDateFormat("yyyy-MM-dd hh:mm");
	
	public static String getDateStr(Date date){
		return sdf.format(date);
	}
	
	public static String getMMddyyyy(Date date){
		return sdf1.format(date);
	}
	
	public static String getDate24(Date date) throws ParseException{
		return sdf24.format(date);
	}
	
	public static String getDateStr(Date date,String format){
		SimpleDateFormat sdf = new SimpleDateFormat(format);
		return sdf.format(date);
	}
	
	public static String format(Date date,String format,Locale local) throws ParseException{
		SimpleDateFormat sdf = new SimpleDateFormat(format,local);
		return sdf.format(date);
	}
	
	public static Date parseDate(String date) throws ParseException{
		return sdf.parse(date);
	}
	
	public static Date parseDate(Date date) throws ParseException{
		return sdf.parse(sdf.format(date));
	}
	
	public static Date parseDate(String date,String format) throws ParseException{
		SimpleDateFormat sdf = new SimpleDateFormat(format);
		return sdf.parse(date);
	}
	
	public static Date parseDate24(String date) throws ParseException{
		return sdf24.parse(date);
	}
	
	public static Date parse(String date,String format,Locale local) throws ParseException{
		SimpleDateFormat sdf = new SimpleDateFormat(format,local);
		return sdf.parse(date);
	}
	
	public static Date addDate(Date now ,int daies) {
		Calendar cal = Calendar.getInstance();
		cal.setTime(now);
		cal.add(Calendar.DAY_OF_MONTH, daies);
		return cal.getTime();
	}
	
	public static long getDayBetween(Date before){
		Calendar calendar = Calendar.getInstance();
		long today = calendar.getTimeInMillis();
		calendar.setTime(before);
		long past = calendar.getTimeInMillis();
		long daies = (past-today)/(1000*60*60*24);
		return daies;
	}
	
	public static long getDayBetween(Date date1,Date date2){
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(date1);
		long day1 = calendar.getTimeInMillis();
		calendar.setTime(date2);
		long day2 = calendar.getTimeInMillis();
		long daies = (day2-day1)/(1000*60*60*24);
		return daies;
	}
	
	
	public static int getWeekDay(Date date){
		Calendar calendar = Calendar.getInstance();
	    calendar.setTime(date);
		int dayOfWeek = calendar.get(Calendar.DAY_OF_WEEK);
		return dayOfWeek;
	}
	
	public static void main(String[] args) throws ParseException{
		System.out.println(DateUtil.getWeekDay(new Date()));
	}
}
