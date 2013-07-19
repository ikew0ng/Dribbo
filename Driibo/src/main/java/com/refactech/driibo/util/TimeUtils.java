
package com.refactech.driibo.util;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

/**
 * Created by Issac on 7/19/13.
 */
public class TimeUtils {

    public static CharSequence getListTime(String created_at) {
        Calendar calendar = Calendar.getInstance();
        Date date = null;
        SimpleDateFormat srcDateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss Z", Locale.US);
        SimpleDateFormat dstDateFormat = new SimpleDateFormat("MMMM dd yyyy", Locale.US);
        try {
            date = srcDateFormat.parse(created_at);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return dstDateFormat.format(date);
    }
}
