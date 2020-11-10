package com.example.weatherapp.common;

import android.location.Location;

import java.text.SimpleDateFormat;
import java.util.Date;

public class Common {
    public static final String APP_ID = "13c54d9ea5ec24d9bf8733a500e5df7a";

    public static Location current_location = null;



    public static String convertUnixToHour (long dt)     {

        Date date = new Date (dt*1000L);
        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm");
        String formatted = sdf.format(date);
        return formatted;

    }

}
