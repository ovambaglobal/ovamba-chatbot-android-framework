package com.sample.chatbotlib;

import android.net.Uri;
import android.util.Log;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

/**
 * Created by psqit on 11/16/2016.
 */
public class AppUtilityFunction {
    private static final int REQUEST_IMAGE_CAPTURE = 11;
    private static final int SELECT_PHOTO = 22;
private static String TAG="largestring";
    public static SimpleDateFormat dateFormatter = new SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH);
    public static Uri mImageCaptureUri;




    public static boolean isNextDate(String date, SimpleDateFormat dateFromat) {

        Log.i("App Utility", "Seleted Date : " + date);
        Calendar selectedDate = Calendar.getInstance(Locale.ENGLISH);
        selectedDate.setTime(DateTimeUtil.convertStringToDate(date, dateFromat));
        Calendar currentDate = Calendar.getInstance(Locale.ENGLISH);
        currentDate.setTime(DateTimeUtil.currentDate());
        return selectedDate.after(currentDate);

    }

}
