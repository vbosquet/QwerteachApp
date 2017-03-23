package com.qwerteach.wivi.qwerteachapp.common;

import android.icu.text.SimpleDateFormat;
import android.icu.util.Calendar;
import android.os.Build;
import android.support.annotation.RequiresApi;

import java.text.ParseException;
import java.util.Date;

/**
 * Created by wivi on 23/03/17.
 */

@RequiresApi(api = Build.VERSION_CODES.N)
public class Common {

    private static Calendar now = Calendar.getInstance();

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
}
