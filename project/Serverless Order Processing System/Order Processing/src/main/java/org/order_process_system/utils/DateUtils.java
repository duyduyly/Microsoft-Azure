package org.order_process_system.utils;

import java.text.SimpleDateFormat;
import java.util.Date;

public class DateUtils {

    public static String parseDateToString(String formatDate, Date date) {
        String dateStr = "";
        try {
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat(formatDate);
            dateStr = simpleDateFormat.format(date);
        } catch (Exception e) {
            System.err.println(e);
        }
        return dateStr;
    }

    public static String getCurrentStrDate(){
        String dateFormat = "dd-MM-yyyy hh:mm:ss";
        Date date = new Date();
        return parseDateToString(dateFormat, date);
    }
}
