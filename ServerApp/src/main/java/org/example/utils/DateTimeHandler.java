package org.example.utils;

import java.text.SimpleDateFormat;
import java.util.Date;

public class DateTimeHandler {

    public static String timeNow(){
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date currentDate = new Date();
        return sdf.format(currentDate);
    }

    public static String timestampNow()
    {
        long currentTimeMillis = System.currentTimeMillis();
        return ""+ currentTimeMillis;
    }
}
