package com.sample.chatbotlib;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

/**
 * Created by Anand on 10-03-2016.
 */
public class DateTimeUtil {

    public static String currentDate(String dateFormat) {
        Calendar cal = Calendar.getInstance(Locale.getDefault());
        SimpleDateFormat sdf = new SimpleDateFormat(dateFormat);
        return sdf.format(cal.getTime());
    }

    public static String currentDateTime(String dateTimeFormat) {
        Calendar cal = Calendar.getInstance(Locale.getDefault());
        SimpleDateFormat sdf = new SimpleDateFormat(dateTimeFormat);
        return sdf.format(cal.getTime());
    }

    public static Date currentDate() {
        Calendar cal = Calendar.getInstance(Locale.getDefault());

        return cal.getTime();
    }

    public static String dateStringFromMillis(long timeMillis,String dateFormat){
        SimpleDateFormat sdf=new SimpleDateFormat(dateFormat);
        return sdf.format(timeMillis);
    }


    public static Date convertStringToDate(String date, SimpleDateFormat dateFormat) {
      //  SimpleDateFormat sdf = new SimpleDateFormat(dateFormat);
        try {
            return dateFormat.parse(date);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return null;
    }


}
