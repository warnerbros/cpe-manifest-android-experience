package com.wb.nextgen.util;

import net.flixster.android.localization.Localizer;
import net.flixster.android.localization.constants.KEYS;
import com.wb.nextgen.util.utils.StringHelper;

public class SortingFilterItem {
	
	/* filter constants */
	public static final String FILTER_ALL = "ALL";
	public static final String FILTER_CATALOG_OFFER = "OFFER";
	public static final String FILTER_CATALOG_CONTENT = "CONTENT";	// TODO for future releases
	public static final String FILTER_BUNDLE = "BUNDLE";
	public static final String FILTER_NO_BUNDLE = "NO_BUNDLE";
	public static final String FILTER_ONLY_BUNDLE = "ONLY_BUNDLE";
	public static final String FILTER_CONTENT_AVAILABLE_NOW = "AVAILABLE_NOW";
	public static final String FILTER_CONTENT_COMING_SOON = "COMING_SOON";
	public static final String FILTER_TYPE_FEATURE = "FEATURE";
	public static final String FILTER_TYPE_SEASON = "SEASON";
	
	/* sort constants */
	public static final String SORT_DATE = "RELEASE_DATE";
	public static final String SORT_DATE_ADDED = "DATE_ADDED";
	public static final String SORT_NAME = "NAME";
	public static final String SORT_FAN_SCORE = "FAN_SCORE";
	public static final String SORT_TOMATOMETER = "ROT_TOM_SCORE";
	
	/* sort order constants */
	public static final String SORT_ASC = "ASC";
	public static final String SORT_DESC = "DESC";

	// sort by 
	public final static SortingFilterItem SORT_BY_DATE_ASC = new SortingFilterItem(SORT_DATE, SORT_ASC, KEYS.SORT_OLDEST);
	public final static SortingFilterItem SORT_BY_DATE_DESC = new SortingFilterItem(SORT_DATE, SORT_DESC, KEYS.SORT_RELEASE_DATE);
	public final static SortingFilterItem SORT_BY_DATE_ADDED_DESC = new SortingFilterItem(SORT_DATE_ADDED, SORT_DESC, KEYS.SORT_DATE_ADDED);
	public final static SortingFilterItem SORT_BY_ALPHABET_ASC = new SortingFilterItem(SORT_NAME, SORT_ASC, KEYS.SORT_A_Z);
	public final static SortingFilterItem SORT_BY_ALPHABET_DESC = new SortingFilterItem(SORT_NAME, SORT_DESC, KEYS.SORT_Z_A);
	public final static SortingFilterItem SORT_BY_FAN_SCORE_DESC = new SortingFilterItem(SORT_FAN_SCORE, SORT_DESC, KEYS.SORT_FAN_SCORE);
	public final static SortingFilterItem SORT_BY_TOMATAOMETER_DESC = new SortingFilterItem(SORT_TOMATOMETER, SORT_DESC, KEYS.SORT_TOMATOMETER);

	// filter options for collection
	public final static SortingFilterItem FILTER_BY_ALL = new SortingFilterItem(FILTER_ALL, "", KEYS.MYCOLLECTION_FILTERALL);
	public final static SortingFilterItem FILTER_BY_FEATURE = new SortingFilterItem(FILTER_TYPE_FEATURE, "", KEYS.FILTER_MOVIES);	// TODO key should be Movies
	public final static SortingFilterItem FILTER_BY_SEASON = new SortingFilterItem(FILTER_TYPE_SEASON, "", KEYS.FILTER_TVSHOWS);		// TODO key should be TV Shows 
	
	// filter options for catalog
	public final static SortingFilterItem FILTER_BY_AVAILABLE_NOW = new SortingFilterItem(FILTER_CONTENT_AVAILABLE_NOW, "", KEYS.FILTER_AVAILABLE_NOW);
	public final static SortingFilterItem FILTER_BY_COMING_SOON = new SortingFilterItem(FILTER_CONTENT_COMING_SOON, "", KEYS.FILTER_COMING_SOON);
	
	public final String nameKey;
	public final String orderKey;
	private final KEYS textKey;

	public SortingFilterItem(String nameKey, String orderKey, KEYS textKey){
		this.nameKey = nameKey;
		this.orderKey = orderKey;
		this.textKey = textKey;
	}
	
	public String getDisplayText(){
		return Localizer.get(textKey);
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((nameKey == null) ? 0 : nameKey.hashCode());
		result = prime * result + ((orderKey == null) ? 0 : orderKey.hashCode());
		result = prime * result + ((textKey == null) ? 0 : textKey.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		SortingFilterItem other = (SortingFilterItem) obj;
		if (nameKey == null) {
			if (other.nameKey != null)
				return false;
		} else if (!nameKey.equals(other.nameKey))
			return false;
		if (orderKey == null) {
			if (other.orderKey != null)
				return false;
		} else if (!orderKey.equals(other.orderKey))
			return false;
		if (textKey != other.textKey)
			return false;
		return true;
	}
	
	@Override
	public String toString() {
		String str = nameKey;
		if (!StringHelper.isEmpty(orderKey))
			str += "_" + orderKey;
		
		return str;
	}

}