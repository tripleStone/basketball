package util;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class StringUtil {
	
	public static String getPosition(String position){
		if (position.equalsIgnoreCase("Point Guard"))
			return ("PG");
		else if (position.equalsIgnoreCase("Shooting Guard"))
			return ("SG");
		else if (position.equalsIgnoreCase("Small Forward"))
			return("SF");
		else if (position.equalsIgnoreCase("Power Forward"))
			return("PF");
		else 
			return("C");
	}
	
	public static Long getLongReg(String str){
		Long result = 0L;
		Pattern patt = Pattern.compile("\\d+");
		Matcher matc = patt.matcher(str);
		if (matc.find()){
			result = Long.valueOf(matc.group());
		}
		return result;
	}

}
