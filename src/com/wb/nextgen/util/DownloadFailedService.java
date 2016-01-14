package com.wb.nextgen.util;

import java.util.ArrayList;
import java.util.List;

public class DownloadFailedService {
	
	private static List<DownloadFailedListener> listeners;
	
	private DownloadFailedService() {
	}
	
	public static void update() {
		if(listeners != null) {
			for(DownloadFailedListener l: listeners) {
				l.downloadFailed();
			}
		}
	}
	
	public static void addListener(DownloadFailedListener listener) {
		if (listeners == null) {
			listeners = new ArrayList<DownloadFailedListener>();
		}
		listeners.add(listener);
	}
	
	public static void removeListener(DownloadFailedListener listener) {
		if (listeners != null) {
			listeners.remove(listener);
		}
	}
	
}
