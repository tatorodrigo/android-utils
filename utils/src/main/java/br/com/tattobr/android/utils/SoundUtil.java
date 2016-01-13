package br.com.tattobr.android.utils;

public class SoundUtil {
    public static String getFormated(Integer duration) {
        String time = null;
        if (duration != null) {
            int millis = duration.intValue();
            long second = (millis / 1000) % 60;
            long minute = (millis / (1000 * 60)) % 60;
            long hour = (millis / (1000 * 60 * 60)) % 24;

            if (hour > 0) {
                time = String.format("%02d:%02d:%02d", hour, minute, second);
            } else if (minute > 0 || second > 0) {
                time = String.format("%02d:%02d", minute, second);
            } else if (duration > 0) {
                time = "00:01";
            } else {
                time = "00:00";
            }
        }
        return time;
    }
}
