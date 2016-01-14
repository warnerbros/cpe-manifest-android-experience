package com.wb.nextgen.util;

import java.text.SimpleDateFormat;
import java.util.Calendar;

import android.content.Context;

import com.wb.nextgen.NextGenApplication;
import com.wb.nextgen.R;

public class DialogUtils {
    private static final SimpleDateFormat DATE_FORMATTER = new SimpleDateFormat("E, MMM d");

    private static CharSequence sShowtimesDateOptions[] = null;

    public static CharSequence[] getShowtimesDateOptions(Context context) {
        if (sShowtimesDateOptions == null) {
            sShowtimesDateOptions = new CharSequence[7];
            Calendar date = Calendar.getInstance();
            sShowtimesDateOptions[0] = context.getResources().getString(R.string.today); // SHORT_DATE_FORMATTER.format(date.getTime())
            for (int i = 1; i < sShowtimesDateOptions.length; i++) {
                date.add(Calendar.DAY_OF_MONTH, 1);
                sShowtimesDateOptions[i] = DATE_FORMATTER.format(date.getTime());
            }
        }
        return sShowtimesDateOptions;
    }

    public static CharSequence[] getLocationOptions() {
        CharSequence[] locations = new CharSequence[3];
        locations[0] = "Near me";
        locations[1] = "( change postal code )";
        //locations[2] = "" + NextGenApplication.getUserLocation();
        return locations;
    }
}