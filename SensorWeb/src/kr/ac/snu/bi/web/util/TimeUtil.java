package kr.ac.snu.bi.web.util;

import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.TimeZone;


public class TimeUtil {
	
	private static final TimeZone TIME_ZONE = TimeZone.getTimeZone("Asia/Seoul");
	
	public static long getCurrentTimeMilli(){
		return System.currentTimeMillis();
	}

	public static int getCurrentTime(){
		return (int)(System.currentTimeMillis()/1000);
	}

	public static int dayDiff(int fromTime, int toTime){
		
		if (fromTime > toTime){
			return 0;
		}
		
		GregorianCalendar cal = new GregorianCalendar();
		cal.setTimeInMillis(fromTime*1000L);
		cal.setTimeZone(TIME_ZONE);
		
		int fromDay = cal.get(Calendar.DAY_OF_YEAR);
		int fromYear = cal.get(Calendar.YEAR);
		int numOfDays = cal.getActualMaximum(Calendar.DAY_OF_YEAR);
		
		cal.setTimeInMillis(toTime*1000L);
		
		int toDay = cal.get(Calendar.DAY_OF_YEAR);
		int toYear = cal.get(Calendar.YEAR);
		
		if (fromYear < toYear){ // suppose maximum difference of year is one
			toDay += numOfDays;
		}
		
		return toDay - fromDay;
	}
	
	public static int toSecond(int dayOfWeek, int hour){
		GregorianCalendar calendar = new GregorianCalendar(TIME_ZONE);
		calendar.setTimeInMillis(getCurrentTimeMilli());
		calendar.set(GregorianCalendar.DAY_OF_WEEK, dayOfWeek);
		calendar.set(GregorianCalendar.HOUR_OF_DAY, hour);
		calendar.set(GregorianCalendar.MINUTE, 0);
		calendar.set(GregorianCalendar.SECOND, 0);
		return (int)(calendar.getTimeInMillis()/1000);
	}
}
