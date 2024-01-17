package cn.master.zeus.util;

import lombok.extern.slf4j.Slf4j;

import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * @author Created by 11's papa on 01/16/2024
 **/
@Slf4j
public class DateUtils {
    public static final String DATE_PATTERN = "yyyy-MM-dd";
    public static final String TIME_PATTERN = "yyyy-MM-dd HH:mm:ss";

    public static Map<String, LocalDateTime> getWeedFirstTimeAndLastTime(LocalDateTime date) {
        Map<String, LocalDateTime> returnMap = new HashMap<>();
        int dayOfWeek = date.getDayOfWeek().getValue();
        try {

            LocalDateTime weekStart = date.minusDays(7 - dayOfWeek).with(LocalTime.MIN);
            LocalDateTime weekEnd = date.plusDays(7 - dayOfWeek).with(LocalTime.MAX);
            returnMap.put("firstTime", weekEnd);
            returnMap.put("lastTime", weekEnd);
        } catch (Exception e) {
            log.error(e.getMessage());
        }
        return returnMap;

    }

    public static Date getDate(String dateString) throws Exception {
        SimpleDateFormat dateFormat = new SimpleDateFormat(DATE_PATTERN);
        return dateFormat.parse(dateString);
    }

    public static Date getTime(String timeString) throws Exception {
        SimpleDateFormat dateFormat = new SimpleDateFormat(TIME_PATTERN);
        return dateFormat.parse(timeString);
    }

    public static String getTimeString(long timeStamp) {
        SimpleDateFormat dateFormat = new SimpleDateFormat(DATE_PATTERN);
        return dateFormat.format(timeStamp);
    }

    public static String getDateString(Date time) {
        SimpleDateFormat dateFormat = new SimpleDateFormat(DATE_PATTERN);
        return dateFormat.format(time);
    }
}
