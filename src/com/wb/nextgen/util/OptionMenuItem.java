package com.wb.nextgen.util;

import java.util.HashMap;

import net.flixster.android.localization.Localizer;
import net.flixster.android.localization.constants.KEYS;
import android.view.MenuItem;

import com.wb.nextgen.R;


public class OptionMenuItem {
	
	public static final OptionMenuItem MENUITEM_SEARCH = new OptionMenuItem(KEYS.DISCOVER_SEARCH, R.drawable.ic_action_search,
			MenuItem.SHOW_AS_ACTION_ALWAYS | MenuItem.SHOW_AS_ACTION_COLLAPSE_ACTION_VIEW, R.layout.action_bar_search);
	public static final OptionMenuItem MENUITEM_SORT = new OptionMenuItem(KEYS.SORT_HEADER, R.drawable.ic_ab_sort, MenuItem.SHOW_AS_ACTION_IF_ROOM, -1);
	public static final OptionMenuItem MENUITEM_FILTER = new OptionMenuItem(KEYS.MENU_FILTER, R.drawable.ic_action_filter, MenuItem.SHOW_AS_ACTION_IF_ROOM, -1);

	public static final OptionMenuItem MENUITEM_SEARCH_TABLET = new OptionMenuItem(KEYS.DISCOVER_SEARCH, R.drawable.ic_action_search,
			MenuItem.SHOW_AS_ACTION_ALWAYS | MenuItem.SHOW_AS_ACTION_COLLAPSE_ACTION_VIEW, R.layout.action_bar_search);
	//public static final OptionMenuItem MENUITEM_SEARCH_TABLET = new OptionMenuItem(KEYS.DISCOVER_SEARCH, R.drawable.ic_action_search,
	//		MenuItem.SHOW_AS_ACTION_ALWAYS, R.layout.action_bar_search);
	public static final OptionMenuItem MENUITEM_REFRESH = new OptionMenuItem(KEYS.MENU_REFRESH, R.drawable.ic_action_refresh, MenuItem.SHOW_AS_ACTION_ALWAYS, -1);
	

	public static final OptionMenuItem MENUITEM_BACK = new OptionMenuItem(KEYS.MENU_BACK, R.drawable.ic_action_back, MenuItem.SHOW_AS_ACTION_ALWAYS, -1);
	public static final OptionMenuItem MENUITEM_FORWARD = new OptionMenuItem(KEYS.MENU_FORWARD, R.drawable.ic_action_forward, MenuItem.SHOW_AS_ACTION_ALWAYS, -1);

	private final KEYS optionName;
	public final int drawableId;
	public final int showAsAction;
	public final int actionViewId;
	
	private static final HashMap<Integer, OptionMenuItem> optionMenuHashTable = new HashMap<Integer, OptionMenuItem>();
	//R.id.menuChromecast, R.id.menuRefresh, R.id.menuFilter, R.id.menuSort, R.id.menuSearch, R.id.menuBack, R.id.menuForward
	static{
		optionMenuHashTable.put(new Integer(R.id.menuChromecast), MENUITEM_FILTER);
		optionMenuHashTable.put(new Integer(R.id.menuChromecast), MENUITEM_FILTER);
		optionMenuHashTable.put(new Integer(R.id.menuChromecast), MENUITEM_FILTER);
		optionMenuHashTable.put(new Integer(R.id.menuChromecast), MENUITEM_FILTER);
		optionMenuHashTable.put(new Integer(R.id.menuChromecast), MENUITEM_FILTER);
		optionMenuHashTable.put(new Integer(R.id.menuChromecast), MENUITEM_FILTER);
		optionMenuHashTable.put(new Integer(R.id.menuChromecast), MENUITEM_FILTER);
		optionMenuHashTable.put(new Integer(R.id.menuChromecast), MENUITEM_FILTER);
	}
	
	
	public OptionMenuItem(KEYS name, int drawable, int actionEnum, int actionView) {
		optionName = name;
		drawableId = drawable;
		showAsAction = actionEnum;
		actionViewId = actionView;
	}
	
	public String getDisplayText(){
		return Localizer.get(optionName);
	}
}