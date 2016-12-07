package com.wb.nextgenlibrary.util.utils;

import android.os.Build;

/**
 * All of the constants
 */

public class F {


	
	public static final String PACKAGE = "com.wb.nextgenlibrary";
	public static final String CRYPT_KEY = "SDAFsdfsdxsadf894ew5sdg;lj34;ln5/ldfsg;ldf9g0gvdfgbkld;fg";
	
	/* Device properties */
	public static final int API_LEVEL = Build.VERSION.SDK_INT;
	public static final String ANDROID_VERSION = Build.VERSION.RELEASE;
	public static final String MANUFACTURER = Build.MANUFACTURER.toLowerCase();
	
	/* Functional switches */
	//public static final boolean IS_DEV_BUILD = (APP_MODE == APP_MODE_TYPE.DEV_MODE);
	public static final boolean IS_ADS_ENABLED = true;
	// native DRM for ICS devices here
	
	/* Third party build switches */
	public static final boolean IS_AMAZON_BUILD = false;
	public static final boolean IS_NOOK_BUILD = false;
	public static final boolean IS_VODAFONE_BUILD = false;
	public static final boolean IS_THIRD_PARTY_BUILD = IS_AMAZON_BUILD || IS_NOOK_BUILD || IS_VODAFONE_BUILD;

	
	/* Log tags */
	public static final String TAG = "NextGenMain";
	public static final String TAG_API = "NextGenApi";
	public static final String TAG_GA = "NextGenGa";
	public static final String TAG_DRM = "NextGenDrm";
	public static final String TAG_USER_ACTION = "NextGenUserAction";
	public static final String TAG_COMMENTARY = "NextGenCommentary";

	/* Misc */
	public static final long SPLASH_DURATION = 1800;
	
	/* Time */
	public static final long MINUTE_MS = 60 * 1000;
	public static final long DAY_MS = 24 * 60 * MINUTE_MS;


	/**************************************** NextGenLogger *************************************/

	public static final String LOGGER_FILE_DIR = "/Android/data/com.flixster.video/files/log/";
	public static final String LOGGER_FILE_NAME = "flixsterUserLog";
	public static final String LOGGER_FILE_EXT = ".txt";



	/**************************************** Flixster REST Field Values **************************************/
	

	public static final String ID = "id";
	public static final String TITLE = "title";

	public static final String URL = "url";


	public static final String RUNTIME = "runtime";
	
	public static final String CLIENT_COUNTRY = "client_country";
	public static final String CLIENT_LANGUAGE = "client_language";

	public static final String CODE = "code";

	public static final String CACHE_DIR = "cache/";

	/**************************************** HTTPHelper **************************************/
	public static final String AUTHORIZATION = "Authorization";
	public static final String X_FORWARDED_FOR = "X-Forwarded-For";
	public static final String ATTACH_UDID_TO_AUTHTOKEN = "Attached_UDID";
	public static final String ACCEPT = "Accept";

}
