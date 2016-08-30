package com.wb.nextgenlibrary.util;

import com.wb.nextgenlibrary.util.utils.F;
import android.app.Activity;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Build;
import android.view.Display;


/**
 * The Class TabletUtils - provides helper methods tablets.
 * 
 * @author vandreev
 */
final public class TabletUtils {
	private static boolean isTablet = (Resources.getSystem().getConfiguration().screenLayout & Configuration.SCREENLAYOUT_SIZE_MASK) >= Configuration.SCREENLAYOUT_SIZE_LARGE
			&& (Build.VERSION_CODES.HONEYCOMB <= F.API_LEVEL);
	
	private TabletUtils() {
	};
	
	/**
	 * Sets the tablet layout h x w for floating activity. Use this method with floating activity style declared in
	 * manifest.
	 * 
	 * @param a the new tablet layout
	 */
	public static void setTabletLayout(Activity a) {
		Display d = a.getWindowManager().getDefaultDisplay();
		int width = d.getWidth() - 50;
		a.getWindow().setLayout(width, d.getHeight() - 30);
	}
	
	/**
	 * Checks if is tablet.( screen size >= 7")
	 * 
	 * @param context the context
	 * @return true, if is tablet
	 */
	public static boolean isTablet() {
		return isTablet;
	}
}
