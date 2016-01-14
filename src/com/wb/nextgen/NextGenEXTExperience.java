package com.wb.nextgen;

import android.app.Activity;
import android.content.Intent;

/**
 * Created by gzcheng on 1/12/16.
 */
public class NextGenEXTExperience {
    private NextGenCallBackInterface callback;
    private Activity startupActivity;
    public NextGenEXTExperience(NextGenCallBackInterface callbackInterface, Activity startupActivity, Object contentObject, String manifestUrl){
        callback = callbackInterface;
        this.startupActivity = startupActivity;
    }

    public void start(){
        Intent nextGenIntent = new Intent(startupActivity, NextGenActivity.class);
        startupActivity.startActivity(nextGenIntent);
    }
}
