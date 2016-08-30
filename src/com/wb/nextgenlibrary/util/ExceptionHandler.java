package com.wb.nextgenlibrary.util;

import com.wb.nextgenlibrary.util.utils.F;
import com.wb.nextgenlibrary.util.utils.NextGenLogger;

import android.app.Activity;

public class ExceptionHandler {
	
	private ExceptionHandler() {
	}
	
	public static void handleException(final Throwable e, final Activity a) {
		if (e != null && e.getMessage() != null)
			NextGenLogger.d(F.TAG, "ExceptionHandler.handleException: " + (a != null && a.getClass() != null ? a.getClass() + ": " + e.getMessage() : e.getMessage()));
		/*
		 * if (!FlixsterApplication.isConnected()) { // DialogBuilder.showNoInternetDialog(a); } else
		 if (a != null && !a.isFinishing()) {
			a.runOnUiThread(new Runnable() {
				@Override
				public void run() {
					DialogBuilder.dismissDialog();
					if (e instanceof DaoException) {
						DaoException de = (DaoException) e;
						if (de.getType() == Type.SERVER_ERROR_MSG) {
							DialogBuilder.showGenericErrorDialogWithContactUsButton(a, de.getMessage());
						} else if (de.getType() == Type.NETWORK) {
							if (!FlixsterApplication.isConnected()) {
								DialogBuilder.showNoInternetDialog(a);
							} else {
								//DialogBuilder.showInfoDialog(a, "", Localizer.getMessageForErrorCode(F.DC2_ERROR_UNKNOWN));
								DialogBuilder.showGenericErrorDialogWithContactUsButton(a);
							}
						} else if (de.getType() == Type.USER_ACCT) {
							if (F.UV_ERROR_UNLINKED.equals(de.getErrorCodeString()) ){
								ContentDataSource.deleteContentsForCurrentUser(true);
							}
							FlixsterApplication.logout(true);
						} else if (de.getType() == Type.SERVER_DATA) {
							//DialogBuilder.showInfoDialog(a, "", Localizer.getMessageForErrorCode(F.DC2_ERROR_UNKNOWN));
							DialogBuilder.showGenericErrorDialogWithContactUsButton(a);
						} else if (de.getType() == Type.OVER_2GB_ERROR){
							DialogBuilder.showInfoDialog(a, "", Localizer.get(KEYS.HINT_ERROR_OVER2GB_NOT_SUPPORTED));
						} else if (de.getType() == Type.SERVER_API) {
							//DialogBuilder.showInfoDialog(a, "", Localizer.getMessageForErrorCode(F.DC2_ERROR_UNKNOWN));
							DialogBuilder.showGenericErrorDialogWithContactUsButton(a);
						} else if (de.getType() == Type.STREAM_CREATE) {
							DialogBuilder.showInfoDialog(a, "", Localizer.getMessageForErrorCode(F.DC2_ERROR_MAX_STREAM));
						} else if (de.getType() == Type.NOT_LICENSED) {
							DialogBuilder.showDialog(DialogBuilder.ERROR_NOT_LICENSED, a);
						} else if (de.getType() == Type.DOWNLOAD_LICENSE_REFETCH_NEEDED) {
							DialogBuilder.showDialog(DialogBuilder.DOWNLOAD_LICENSE_REFETCH_NEEDED, a);
						} else if (de.getType() == Type.CAST_ASSET_ERROR){
							DialogBuilder.showInfoDialog(a, "", Localizer.get(KEYS.CHROMECAST_PLAYBACK_NOT_AVAILABLE));
						}
						
					} else {
						if (!FlixsterApplication.isConnected()) {
							DialogBuilder.showNoInternetDialog(a);
						} else {
							//DialogBuilder.showInfoDialog(a, "", Localizer.getMessageForErrorCode(F.DC2_ERROR_UNKNOWN));
							DialogBuilder.showGenericErrorDialogWithContactUsButton(a);
						}
					}
				}
				
			});
		}*/
		
	}

	
	public static void logException(Exception e, Class<?> clazz) {
		NextGenLogger.e(F.TAG, clazz.getSimpleName() + " " + e.getMessage(), e);
	}
}
