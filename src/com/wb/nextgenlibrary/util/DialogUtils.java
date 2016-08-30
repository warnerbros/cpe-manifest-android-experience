package com.wb.nextgenlibrary.util;

import java.text.SimpleDateFormat;
import java.util.Calendar;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.view.ContextThemeWrapper;

import com.wb.nextgenlibrary.R;


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
        //locations[2] = "" + NextGenExperience.getUserLocation();
        return locations;
    }

    public static void showLeavingAppDialog(Activity activity, DialogInterface.OnClickListener  okClickListener){
        AlertDialog.Builder builder = new AlertDialog.Builder(new ContextThemeWrapper(activity, R.style.CenterAlignedAlertDialog));
        builder.setTitle(activity.getResources().getString(R.string.leave_app_dialogbox_title));
        builder.setMessage(activity.getResources().getString(R.string.leave_app_dialogbox_text));
        builder.setCancelable(true);
        builder.setNegativeButton(activity.getResources().getString(R.string.label_no), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        /*builder.setOnCancelListener(new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialog) {
                dialog.dismiss();
            }
        });*/
        builder.setPositiveButton(activity.getResources().getString(R.string.label_yes), okClickListener);
        builder.create().show();
    }
}