package com.wb.nextgenlibrary.util;

import com.wb.nextgenlibrary.util.utils.F;
import android.app.Activity;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AbsListView;

public class UiHelper {
	
	public static void setBackground(View view, Drawable background) {
		if (F.API_LEVEL < Build.VERSION_CODES.JELLY_BEAN) {
			view.setBackgroundDrawable(background);
		} else {
			view.setBackground(background);
		}
	}
	
	public static void smoothScrollToPositionFromTop(AbsListView view, int position) {
		if (F.API_LEVEL < Build.VERSION_CODES.HONEYCOMB) {
			// smoothScrollToPosition only scrolls a few pixels. turn off for API < 11
		} else {
			view.smoothScrollToPositionFromTop(position, 0);
		}
	}
	
	public static void hideSoftKeyboard(Activity activity, View v) {
		InputMethodManager imm = (InputMethodManager) activity.getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(v.getApplicationWindowToken(), 0);
	}
	
	public static void showSoftKeyboard(Activity activity, View v) {
		InputMethodManager imm = (InputMethodManager) activity.getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.showSoftInput(v, InputMethodManager.SHOW_IMPLICIT);
	}
}
