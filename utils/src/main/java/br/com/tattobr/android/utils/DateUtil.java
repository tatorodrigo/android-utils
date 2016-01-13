package br.com.tattobr.android.utils;

import android.content.Context;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class DateUtil {
    public static String getDate(Long date) {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd/MM/yy", Locale.getDefault());
        return simpleDateFormat.format(new Date(date));
    }

    public static String getTime(Long time, Context context) {
        SimpleDateFormat simpleDateFormat;
        if (android.text.format.DateFormat.is24HourFormat(context)) {
            simpleDateFormat = new SimpleDateFormat("HH:mm", Locale.getDefault());
        } else {
            simpleDateFormat = new SimpleDateFormat("hh:mm a", Locale.getDefault());
        }
        return simpleDateFormat.format(time);
    }

    public static int getYearCurrent() {
        Calendar calendar = Calendar.getInstance();
        return calendar.get(Calendar.YEAR);
    }

    public static Long updateTime(Long time, int hourOfDay, int minute) {
        Date date = new Date();
        date.setTime(time);
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        cal.set(Calendar.HOUR_OF_DAY, hourOfDay);
        cal.set(Calendar.MINUTE, minute);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MILLISECOND, 0);
        Date zeroedDate = cal.getTime();

        return zeroedDate.getTime();
    }

    public static String getFormatDateOrTime(Long time, Context context) {
        Long differenceDateMilli = System.currentTimeMillis() - time;
        Long differenceDateDays = differenceDateMilli / (1000 * 60 * 60 * 24);

        if (differenceDateDays < 1) {
            return getTime(time, context);
        } else if (differenceDateDays > 1) {
            return getDate(time);
        } else {
            return context.getResources().getString(R.string.tattobr_utils_yesterday).toUpperCase();
        }
    }
}