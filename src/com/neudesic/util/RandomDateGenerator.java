package com.neudesic.util;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;

public class RandomDateGenerator 
{
    public static List<Date> searchBetweenDates(Calendar startDate, Calendar endDate, int limit)  {
    	List<Date> dates = new ArrayList<>();
    	   
    	for(int i=0;i<limit;i++){  
    		long t= startDate.getTimeInMillis(); 
    		Date afterAddingOneMins=new Date(t + (50000));	
    		startDate.setTimeInMillis(t + (50000));
    		dates.add(afterAddingOneMins);    		
    	}
    	return dates;
    }
 
    public static List<Date> generateDate(String startDate, String endDate, int no_of_rows) throws ParseException {     
    	SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
    	
        Calendar startCal = new GregorianCalendar();
        dateFormat.setTimeZone(startCal.getTimeZone());
        startCal.setTime(dateFormat.parse(startDate));
       
        Calendar endCal = new GregorianCalendar();        
        dateFormat.setTimeZone(endCal.getTimeZone());
        endCal.setTime(dateFormat.parse(endDate));     
        
        return searchBetweenDates(startCal,endCal,no_of_rows);
    }
}
