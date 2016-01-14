package com.wb.nextgen.util.utils;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import net.flixster.android.localization.Localizer;
import net.flixster.android.localization.constants.KEYS;

/**
 * Locale-aware date time parser and formatters
 */
public class DateTimeHelper extends LocalizedHelper {

	private static final DateTimeHelper INSTANCE = new DateTimeHelper();
	private static final String HOUR_FORMAT_24 = "HH:mm";
	private static final String HOUR_FORMAT_12 = "hh:mm aa";
	private static final SimpleDateFormat API_HOUR_PARSER = new SimpleDateFormat(HOUR_FORMAT_12);
	private static final SimpleDateFormat MONTH_DAY_FORMATTER = new SimpleDateFormat("MMMMM d");
	private static final SimpleDateFormat MONTH_DAY_YEAR_FORMATTER = new SimpleDateFormat("MMMMM d, yyyy");
	
	public static final String SHORT_DATE_FORMAT = "MM-dd-yyyy";
	public static final SimpleDateFormat SHORT_DATE_FORMATTER = new SimpleDateFormat(SHORT_DATE_FORMAT);
	
	public static final String CONTENT_MODIFIED_DATE_FORMAT = "yyyy-MM-dd'T'HH:mm:ss.SSSZ";
	public static final SimpleDateFormat CONTENT_MODIFIED_DATE_FORMATTER = new SimpleDateFormat(CONTENT_MODIFIED_DATE_FORMAT);
	
	public static final String ISO_DATETIME_TIME_ZONE_FORMAT = "yyyy-MM-dd'T'HH:mm:ssZZ";
	public static final SimpleDateFormat ISO_DATETIME_TIME_ZONE_FORMATTER = new SimpleDateFormat(
			ISO_DATETIME_TIME_ZONE_FORMAT);
	
	private final DateFormat mediumDateFormatter, longDateFormatter, shortTimeFormatter;
	
	private DateTimeHelper() {
		Locale locale = Localizer.getLocale();
		mediumDateFormatter = DateFormat.getDateInstance(DateFormat.MEDIUM, locale);
		longDateFormatter = DateFormat.getDateInstance(DateFormat.LONG, locale);
		shortTimeFormatter = isFrance ? new SimpleDateFormat(HOUR_FORMAT_24) : new SimpleDateFormat(HOUR_FORMAT_12);
	}
	
	public static DateFormat mediumDateFormatter() {
		return INSTANCE.mediumDateFormatter;
	}
	
	public static DateFormat longDateFormatter() {
		return INSTANCE.longDateFormatter;
	}
	
	/** @return 24-hour format for France, 12-hour format for rest */
	public static String shortTimeFormat(String time) {
		if (!INSTANCE.isFrance) {
			return time;
		}
		
		Date datetime = null;
		try {
			datetime = API_HOUR_PARSER.parse(time);
		} catch (ParseException e) {
			FlixsterLogger.w(F.TAG, "DateTimeHelper.shortTimeFormat", e);
		}
		return datetime != null ? INSTANCE.shortTimeFormatter.format(datetime) : time;
	}
	
	/** @return "August 12" */
	public static String formatMonthDay(Date date) {
		return MONTH_DAY_FORMATTER.format(date);
	}
	
	/** @return "August 12, 2011" */
	public static String formatMonthDayYear(Date date) {
		return MONTH_DAY_YEAR_FORMATTER.format(date);
	}
	
	public static String formatRentalExprTime(long timeDiffInSec) {
		long timeDiffInMin = timeDiffInSec / 60;
		int count = 0;
		String labelText = "";
		if (timeDiffInMin <= 45) {
			count = Math.round(timeDiffInMin);
	        labelText = (count == 1 ? Localizer.get(KEYS.EXPIRE_IN_ONE_MINUTE) : String.format(Localizer.get(KEYS.EXPIRE_IN_X_MINUTES), count ));
	    } else if (timeDiffInMin <= 1440) {
	    	count = Math.round(timeDiffInMin / 60.0f);
	        labelText = (count == 1 ? Localizer.get(KEYS.EXPIRE_IN_ABOUT_ONE_HOUR) : String.format(Localizer.get(KEYS.EXPIRE_IN_ABOUT_X_HOURS), count ));
	    } else if (timeDiffInMin <= 43200) {
	    	count = Math.round(timeDiffInMin / 1440.0f);
	        labelText = (count == 1 ? Localizer.get(KEYS.EXPIRE_IN_ONE_DAY) : String.format(Localizer.get(KEYS.EXPIRE_IN_X_DAYS), count ));
	    } else {
	    	count = Math.round(timeDiffInMin / 43200.0f);
	        labelText = (count == 1 ? Localizer.get(KEYS.EXPIRE_IN_ABOUT_ONE_MONTH) : String.format(Localizer.get(KEYS.EXPIRE_IN_X_MONTHS), count ));
	    }
		return labelText;
		
	}
}
