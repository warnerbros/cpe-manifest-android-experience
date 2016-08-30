package com.wb.nextgenlibrary.util.utils;

import android.app.Activity;

public class ActivityHolder {
	
    private static final ActivityHolder INSTANCE = new ActivityHolder();

    private Activity topLevel;
    private int ctr;

    private ActivityHolder() {
    }

    public static ActivityHolder instance() {
        return INSTANCE;
    }

    /**
     * @return The top level activity reference if available (Caller should not keep copies of this reference)
     */
    public synchronized Activity getTopLevelActivity() {
        return topLevel;
    }

    /**
     * Set the top level activity reference
     * 
     * @return An id to be passed to {@code resetTopLevelActivity(int)}
     */
    public synchronized int setTopLevelActivity(Activity activity) {
        topLevel = activity;
        return ++ctr;
    }

    /**
     * Remove the activity reference if it is on the top of the stack
     * 
     * @param id The unique id returned when the activity reference was initially set
     */
    public synchronized void removeTopLevelActivity(int id) {
        if (id >= ctr) {
            topLevel = null;
        }
    }

    public synchronized void clear() {
        topLevel = null;
        ctr = 0;
    }
}
