package com.qwerteach.wivi.qwerteachapp.common;

import android.icu.text.SimpleDateFormat;
import android.os.Build;
import android.support.annotation.RequiresApi;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;

/**
 * Created by wivi on 23/03/17.
 */

public class Common {

    private static Calendar now = Calendar.getInstance();
    public static final String IP_ADDRESS = "http://167.114.246.31:3500/";

    @RequiresApi(api = Build.VERSION_CODES.N)
    public static boolean checkIfValidTime(String date, String time) {
        Date newDate = new Date(System.currentTimeMillis());
        Calendar selectedDateTime = getSelectedDate(date);
        boolean isValidTime = true;

        int selectedHour = Integer.parseInt(time.substring(0, 2));
        int selectedMinute = Integer.parseInt(time.substring(3));

        if (selectedDateTime.get(Calendar.DAY_OF_MONTH) == now.get(Calendar.DAY_OF_MONTH)
                && selectedDateTime.get(Calendar.MONTH) == now.get(Calendar.MONTH)
                && selectedDateTime.get(Calendar.YEAR) == now.get(Calendar.YEAR)) {

            if (newDate.getHours() > selectedHour) {
                isValidTime = false;
            } else if (newDate.getHours() == selectedHour && newDate.getMinutes() > selectedMinute) {
                isValidTime = false;
            }
        }

        return isValidTime;
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    public static boolean checkIfCurrentDate(String date) {
        Calendar selectedDateTime = getSelectedDate(date);
        boolean isCurrentDate = false;

        if (selectedDateTime.get(Calendar.DAY_OF_MONTH) == now.get(Calendar.DAY_OF_MONTH)
                && selectedDateTime.get(Calendar.MONTH) == now.get(Calendar.MONTH)
                && selectedDateTime.get(Calendar.YEAR) == now.get(Calendar.YEAR)) {
            isCurrentDate = true;
        }

        return  isCurrentDate;
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    private static Calendar getSelectedDate(String date) {
        Calendar selectedDateTime = null;
        try {
            Date selectedDate = new SimpleDateFormat("dd/MM/yyyy").parse(date);
            selectedDateTime = Calendar.getInstance();
            selectedDateTime.setTimeInMillis(selectedDate.getTime());
        } catch (ParseException e) {
            e.printStackTrace();
        }

        return selectedDateTime;
    }

    public static Date getDate(String dateToFormat) {
        java.text.SimpleDateFormat format = new java.text.SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS");
        format.setTimeZone(TimeZone.getTimeZone("UTC"));
        Date date = null;
        try {
            date = format.parse(dateToFormat);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return  date;

    }

    public static int getUniqueElement(List<Integer> data) {
        List<Integer> newList = new ArrayList<>();
        for (Integer studentId : data)
            if (!newList.contains(studentId))
                newList.add(studentId);
        return newList.size();
    }
}
