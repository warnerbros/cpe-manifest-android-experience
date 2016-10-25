package com.wb.nextgenlibrary.model;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by gzcheng on 4/6/16.
 */
public class Duration {

	public final int hours, minutes, seconds;

	private Date date;

	public Duration(String durationString) {
		try {
			date = new SimpleDateFormat("'PT's'S'").parse(durationString);
		} catch (Exception ex) {
			try {
				date = new SimpleDateFormat("'PT's.S'S'").parse(durationString);
			} catch (Exception ex2) {}
		}
		if (date != null) {
			hours = date.getHours();
			minutes = date.getMinutes();
			seconds = date.getSeconds();
		} else {
			hours = 0;
			minutes = 0;
			seconds = 0;
		}
	}

	public Date getDate() {
		return date;
	}
}
