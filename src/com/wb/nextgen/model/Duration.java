package com.wb.nextgen.model;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by gzcheng on 4/6/16.
 */
public class Duration {

    public final int hour, minutes, second;
    public Duration(String durationString){
        Date d = null;
        try {
            d = new SimpleDateFormat("'PT's'S'").parse(durationString);
        }catch (Exception ex){
        }
        if (d != null) {

            hour = d.getHours();
            minutes = d.getMinutes();
            second = d.getSeconds();
        }else{
            hour = 0;
            minutes = 0;
            second = 0;

        }
    }
}
