package util;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Locale;
import java.util.Random;
import java.util.TimeZone;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import models.game.GameInfo;
import models.game.GamePlayerInfo;
import models.player.PlayerInfo;

import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

public class TestB {
	
	
	

	public static void main(String[] args){
//		List<String> atTags = regxp(Pattern.compile("#([^#@\\(\\)]{1,20})#"),"阿打发打发(#哼)#123#(#不要)#222#@胡俊(2766)(#衰)@李小莉(1001)#223# ");
//		String time = "7:00 PM CST, December 12, 2012";
//		String time = "7:00 PM, December 12, 2012";
//		
//		SimpleDateFormat df = new SimpleDateFormat("h:mm a, MMMM dd, yyyy",Locale.ENGLISH);
		SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
//		try {
//		 System.out.println(df.format(new Date()));
//		 System.out.println(df.parse(time));
//		} catch (Exception e) {
//		// TODO Auto-generated catch block
//		e.printStackTrace();
//		}
//		Calendar calendar = Calendar.getInstance();
//	    Date date = new Date();
//	    calendar.setTime(date);
//		int dayOfWeek = calendar.get(Calendar.DAY_OF_WEEK);
//		System.out.println(dayOfWeek);
		
		// 本月的第一天
		  Calendar calendar  =   new  GregorianCalendar();
		  calendar.set( Calendar.DATE,  1 );
		  SimpleDateFormat simpleFormate  =   new  SimpleDateFormat( "yyyy-MM-dd" );
		  System.out.println(calendar.getTime());
		  // 本月的最后一天
		  calendar.roll(Calendar.DATE,  - 1 );
		  System.out.println(calendar.getTime());
		
		
	}
	
	
	public static List<String> regxp(Pattern pattern, String value) {
        List<String> result = new ArrayList<String>();
        if (value == null) value = "";
        Matcher m = pattern.matcher(value);
        while(m.find()) {
        	 value = value.replaceAll(m.group(), "");
        }
        System.out.println(value);
        return result;                

    }
}
