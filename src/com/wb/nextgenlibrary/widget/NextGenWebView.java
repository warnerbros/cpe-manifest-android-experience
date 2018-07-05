package com.wb.nextgenlibrary.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.view.ContextMenu;
import android.view.MenuItem;
import android.webkit.WebView;

public class NextGenWebView extends WebView {
	static final int ID_SAVEIMAGE = 1;
	static final int ID_VIEWIMAGE = 2;

	public NextGenWebView(Context context, AttributeSet attrs, int defStyle){
		super(context, attrs, defStyle);
	}
	public NextGenWebView(Context context, AttributeSet attrs) {
		super(context, attrs);
	}
	@Override
	protected void onCreateContextMenu(ContextMenu menu) {
		super.onCreateContextMenu(menu);

		HitTestResult result = getHitTestResult();

		MenuItem.OnMenuItemClickListener handler = new MenuItem.OnMenuItemClickListener() {
			public boolean onMenuItemClick(MenuItem item) {
				// do the menu action
				return true;
			}
		};

		if (result.getType() == HitTestResult.IMAGE_TYPE ||
				result.getType() == HitTestResult.SRC_IMAGE_ANCHOR_TYPE) {
			// Menu options for an image.
			//set the header title to the image url
			menu.setHeaderTitle(result.getExtra());
			menu.add(0, ID_SAVEIMAGE, 0, "Save Image").setOnMenuItemClickListener(handler);
			menu.add(0, ID_VIEWIMAGE, 0, "View Image").setOnMenuItemClickListener(handler);
		}
	}
}