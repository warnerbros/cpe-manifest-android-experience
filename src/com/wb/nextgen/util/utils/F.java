package com.wb.nextgen.util.utils;

import com.wb.nextgen.NextGenApplication;
import com.wb.nextgen.util.TabletUtils;

import android.os.Build;

/**
 * All of the constants
 */

public class F {


	
	public static final String PACKAGE = "com.wb.nextgen";
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

	/* Misc */
	public static final long SPLASH_DURATION = 1800;
	public static final long MSK_PROMPT_DURATION = SPLASH_DURATION + 5000;
	
	/* Time */
	public static final long MINUTE_MS = 60 * 1000;
	public static final long DAY_MS = 24 * 60 * MINUTE_MS;
	
	/* Ads */
	public static final long AD_FREE_PERIOD_MS = 2 * DAY_MS;
	public static final long AD_FETCH_INTERVAL_MS = 30 * MINUTE_MS;
	
	/* Admin */
	public static final String ADMIN_UNLOCK_LONG = "REINDEER FLOTILLA";
	public static final String ADMIN_UNLOCK_SHORT = "FLX";
	public static final String ADMIN_UNLOCK_IOS = "UUDDLRLRBA";
	
	/* Diagnostic */
	public static final String DIAGNOSTIC_UNLOCK = "DGNS";
	
	public static final String KEY_CONTENT_ID = "KEY_MOVIE_ID";
	public static final String KEY_THEATER_ID = "KEY_THEATER_ID";
	
	public static final String TAB_COL = "collections";
	public static final String TAB_REDEEM = "redeem";
	public static final String TAB_SETTINGS = "settings";
	public static final String TAB_HELP = "help";
	public static final String TAB_LANDING = "landing";
	public static final String TAB_SHOWTIME_TICKET = "showtime_ticket";

	/**************************************** NextGenLogger *************************************/

	public static final String WEBDEV_SUPPORT_EMAIL = "WebDev@warnerbros.com";
	public static final String ENSEMBLE_SUPPORT_EMAIL = "wb-fv-qa@ensemble.com";
	public static final String DC2_SUPPORT_EMAIL = "support@dcplus.flixster.com";
	public static final String UV_SUPPORT_EMAIL = "uvsupport@flixster-inc.com";
	
	public static final String LOGGER_FILE_DIR = "/Android/data/com.flixster.video/files/log/";
	public static final String LOGGER_FILE_NAME = "flixsterUserLog";
	public static final String LOGGER_FILE_EXT = ".txt";

	/**************************************** US Flixster File Folder *************************************/
	public static final String US_FLIXSTER_FILE_FOLDER = "/Android/data/net.flixster.android/files/wv/";
	
	/**************************************** Flixster URLS **************************************/
	
	
	public static final String REST_URL = "";
	
	public static final String REGISTRATON_URL = "/register?webview=true";
	public static final String USFLIXSTERUSER_REG_URL = "/us_optin.html";
	public static final String FORGOT_URL = "/forgot-password?webview=true";
	public static final String CONFIG_URL = "/config.json";
	public static final String UNSUPPORTED_MODELS_URL = "/data/android_blacklist.json";
	public static final String UV_URL = "/uv/link?webview=true";
	public static final String REPERMISSION_URL = "/repermission?webview=true";
	public static final String UV_RELINK_URL = "/uv/link?webview=true";
	public static final String CONTACT_US_URL = "/support/contact.html";

	public static final String WEBVIEW_JS_INTERFACE_NAME = "flxvid";
	
	/**************************************** Flixster API URLS **************************************/
	
	/* location */
	public static final String LOCALIZTION_URL = "/locales/";
	public static final String LANG = "/lang";
	
	/* Geo API */
	public static final String GEO_LOC = "/rest/location/ip_address/";
	public static final String GEO_IP_LOCATION_CHECK = "/rest/location";
	
	/* Stream API */
	public final static String START_STREAM = "/rest/stream/start";
	public final static String RENEW_STREAM = "/rest/stream/renew/stream_handle_id/";
	public final static String FIND_STREAMS = "/rest/stream/all";
	public final static String STOP_STREAM = "/rest/stream/delete";	// append with stream_id/play_position
	public final static String TICK_RENTAL_CLOCK_URL = "/rest/stream/tick_rental_clock";
	public final static String UPDATE_BOOKMARK = "/rest/stream/bookmarkPlaybackPosition";
	
	/* Download API */
	public final static String START_DOWNLOAD = "/rest/download/start";
	public final static String STOP_DOWNLOAD = "/rest/download/delete";
	public final static String FIND_DOWNLOAD = "/rest/download/all/rights_id/";
	public final static String RMH_DOWNLOAD_STATUS_UPDATE = "/rest/download/update_download_status/";
	
	/* Token API */
	public final static String VALIDATE_TOKEN = "/rest/token/validate";
	public final static String CONSUME_TOKEN = "/rest/token/consume/token/";
	public final static String VALIDATE_TOKEN_NEW = "/rest/token/validate/1.0/";
	public final static String CONSUME_TOKEN_NEW = "/rest/token/consume/token/1.0/";
	public final static String CAPTCHA_URL	= "/captcha.html";
	/* Content API */
	public final static String GET_USER_RIGHT_LOCKER = "/rest/user/user_rights_locker";	// old
	public final static String GET_USER_COLLECTION = "/rest/user/user_collection";
	public final static String GET_ALL_CONTENT = "/rest/content/all";
	public final static String GET_CONTENT = "/rest/content/detail/cid/";
	public final static String GET_CONTENT_DETAIL_BY_RIGHTS_ID = "/rest/user/content_details/rights_id/";
	public final static String GET_CONTENT_DETAIL_BY_GUID = "/rest/offer/find/guid/";
	public final static String GET_USER_RENTAL_LOCKER = "/rest/user/user_rental_locker";
	public final static String GET_UV_RIGHTS_IDS_TRANSLATION = "/rest/content/get/uv_rights_ids";
	
	/* Discover Page API*/
	public final static String SEARCH_OFFER = "/rest/offer/search";
	public final static String FIND_OFFER_GUID = "/rest/offer/find/guid/";
	
	/* Catalog API */
	public final static String CATALOG_ALL = "/rest/catalog/all";
	
	/* USER API */
	public final static String USER_LOGIN = "/rest/user/login";
	public final static String USER_FB_LOGIN = "/rest/user/facebook/login";
	public final static String USER_FB_LINK = "/rest/user/facebook/link";
	public final static String USER_GOOGLE_LOGIN = "/rest/user/google/login";
	public final static String USER_FIND = "/rest/user/find";
	public final static String USER_UPDATE = "/rest/user/update/fv";
	public final static String USER_SYNC = "/rest/user/find/es_account";
	public final static String USER_UPDATE_PASSWORD = "/rest/user/password/update";
	public final static String UV_DETAILS = "uv_details";
	public final static String USER_CONTENT_MEDIA_PREF = "/rest/user/content_media_pref/rights_id/";
	//public final static String USER_CONTENT_VIDEO_BOOKMARK = "/rest/user/content_video_bookmark/rights_id/%s/bookmark/%s";
	public final static String USER_FORGOT_PASSWORD = "/rest/user/password/forgot";



	/**************************************** Flixster REST Field Values **************************************/
	
	public static final String LANGUAGE_CODES = "languageCodes";
	public static final String ID = "id";
	public static final String LABEL = "label";
	public static final String PARATURE_TOKEN = "paratureToken";
	public static final String PARATURE_BASEURL = "paratureBaseUrl";
	public static final String DEFAULT_LOCALE = "defaultLocale";
	public static final String PARATURE_LOCALE_LINKS = "paratureLocaleLinks";
	public static final String COUNTRY_LIST = "country_list";
	public static final String SHOWTIME_AND_TICKETS_COUNTRY_LIST = "showtime_and_tickets_enabled";
	public static final String TERMS = "terms";
	public static final String PRIVACY = "privacy";
	public static final String COOKIES = "cookies";
	public static final String COMPANY = "company";
	public static final String TRANSFER = "transfer";
	public static final String EULA = "eula";
	public static final String USAGE = "usage";
	public static final String PROVIDER = "provider";
	public static final String OPTIN = "optIn";
	public static final String DEFAULT = "default";
	public static final String UNIVERSAL = "UNIVERSAL";
	public static final String UPGRADE = "upgrade";
	public static final String ANDROID = "android";
	public static final String WARNING_MIN = "warning_min";
	public static final String FORCE_MIN = "force_min";
	public static final String UV = "uv";
	public static final String MANAGE = "manage";
	public static final String REDEEM = "redeem";
	public static final String MY_COLLECTION = "my_collection";
	public static final String SIGNUP = "signup";
	public static final String COUNTRY_SETTINGS = "country_settings";
	public static final String SEARCH_BOX_DELAY_TIME = "search_box_delay_time";
	public static final String SYSTEM_TIME = "system_time";

	public static final String USER_CONSENTS_LIST = "user_consents_list";
	public static final String USER_CONSENT_KEY = "user_consent_key";
	
	public static final String NATIVE_UNSUPPORTED_MODELS = "native_unsupported_models";
	public static final String PLUGIN_DOWNLOAD_UNSUPPORTED_MODELS = "plugin_download_unsupported_models";
	public static final String TWO_GB_UNSUPPORTED_MODELS = "2gb_unsupported_models";
	public static final String VOB_UNSUPPORTED_MODELS = "vob_unsupported_models";
	
	public static final String RESULT = "result";
	public static final String RESULT_LIST = "result_list";
	public static final String COLLECTION_LIST = "collection_list";
	public static final String RENTAL_LIST = "rental_list";
	public static final String ERROR = "error";
	public static final String DOWNLOAD = "download";
	public static final String STREAM = "stream";
	public static final String TOTAL = "total";
	public static final String PAGE = "page";
	public static final String PAGES = "pages";
	public static final String OFFER_READ = "offer_read";
	public static final String USER_COLLECTION = "user_collection";
	public static final String USER_RENTAL = "user_rental";
	public static final String UV_SYNC_STATUS = "uv_sync_status";
	public static final String UV_RELINK_REQUIRED = "uv_relink_required";
	public static final String RESULT_SUCCESS = "SUCCESS";
	public static final String RESULT_IN_PROGRESS = "IN_PROGRESS";
	
	public static final String CONTENT = "content";
	//public static final String CONTENT_ID = "content_id";
	public static final String FLX_MOVIE_ID = "flx_movie_id";
	public static final String ES_CONTENT_ID = "es_cid";
	public static final String NAME = "name";
	public static final String TYPE = "type";
	public static final String ROT_TOM_SCORE = "rot_tom_score";
	public static final String FAN_SCORE = "fan_score";
	public static final String RIGHTS_TYPE = "rights_type";
	public static final String SHORT_SYNOPSIS = "short_synopsis";
	public static final String LONG_SYNOPSIS = "long_synopsis";
	public static final String CAN_STREAM = "can_stream";
	public static final String CAN_DOWNLOAD = "can_download";
	public static final String STREAMING_MEDIA_LIST = "stream_media_list";
	public static final String PHYSICAL_ASSET_LIST = "physical_asset_list";
	public static final String SUBTITLE_URL_LIST = "subtitle_list";
	public static final String RIGHTS_ID = "rights_id";
	public static final String ES_RIGHTS_ID = "es_rights_id";
	public static final String RMH_PASS_ID = "rmh_pass_id";
	public static final String RIGHTS = "rights";
	public static final String DOWNLOAD_ID = "download_id";
	public static final String ES_CLIENT_TYPE = "es_client_type";
	public static final String DOWNLOAD_MEDIA_LIST = "download_media_list";
	public static final String DOWNLOAD_ASSET_LIST = "download_asset_list";
	public static final String ASSET_LIST = "asset_list";
	public static final String VIDEO_ASSET_LIST = "video_asset";
	public static final String RIGHTS_LOCKER = "rights_locker";
	public static final String RIGHTS_ID_LIST = "rights_id_list";
	public static final String CID_LIST = "cid_list";
	public static final String CID = "cid";
	public static final String OLD_CID = "old_cid";
	public static final String ASSET_TYPE = "asset_type";
	public static final String ASSET_SUBTYPE = "asset_sub_type";
	public static final String URL = "url";
	public static final String BITRATE = "bit_rate";
	public static final String BITRATE_PROFILE = "bitrate_profile";
	public static final String MEDIA_TYPE = "media_type";
	public static final String MEDIA_TRANSFER_TYPE = "media_transfer_type";
	public static final String PROFILE_NAME = "profile";
	public static final String CLAS_CONTENT_ID = "clas_content_id";
	public static final String CLAS_PROFILE_ID = "clas_profile_id";
	public static final String AVAILABLE_CONTENT_LIST = "available_content_list";
	public static final String CONTENT_LIST = "content_list";
	public static final String UNFULFILLABLE_LIST = "unfullfillable_content_list";
	public static final String NUM_OF_DOWNLOADS = "num_of_downloads";
	public static final String FULFILLABLE = "fulfillable";
	public static final String UV_UPGRADEABLE = "uv_upgradeable";
	public static final String DELIVERY_FORMAT = "delivery_format";
	public static final String STREAM_WEB_LOCATION = "stream_web_loc";
	public static final String LOCATION = "location";
	public static final String STUDIO = "studio";
	public static final String SEASON_NUMBER = "season_number";
	public static final String SERVER_FALLBACK_REQUIRED = "fall_back_required";
	public static final String PARENT_RIGHTS_ID = "parent_rights_id";
	public static final String EPISODE_NUMBER = "episode_number";
	public static final String CHILDREN = "children";
	public static final String LASP = "lasp";
	public static final String WB_LASP = "wb_lasp";
	public static final String WB_ASSET_PROFILE = "wb_asset_profile";
	public static final String SONIC_LASP = "sonic_lasp";
	public static final String DATE_ADDED = "date_added";
	public static final String ACTIVATION_DATE = "activation_date";
	public static final String CAN_CAST = "chrome_cast";
	public static final String IS_CURRENT = "is_current";

	public static final String SONIC_STREAM_ASSET_PROFILE = "sonic_stream_asset_profile";
	public static final String SONIC_DOWNLOAD_ASSET_PROFILE = "sonic_download_asset_profile";
	public static final String WB_STREAM_ASSET_PROFILE = "wb_stream_asset_list";
	public static final String WB_DOWNLOAD_ASSET_PROFILE = "wb_download_asset_list";
	public static final String WVN_ASSET = "wvn_asset";

	public static final String START_DATE = "start_date";
	public static final String END_DATE = "end_date";
	public static final String EXPIRATION_DATE = "expiry_date";
	public static final String STATUS = "status";
	public static final String AVAILABILITY = "availability";
	public static final String UPDATED_DATE = "updatedDate";
	public static final String EXPIRY_MINS = "expiry_mins";
	
	public static final String OFFER_ID = "offer_id";
	public static final String OFFER_START_DATE = "ofr_sart_date";
	public static final String OFFER_RELEASE_DATE = "ofr_release_date";
	public static final String OFFER_END_DATE = "ofr_end_date";
	public static final String OFFER_DISPLAY_START_DATE = "ofr_end_date";
	public static final String OFFER_DISPLAY_END_DATE = "ofr_end_date";
	
	public static final String AUTH_TOKEN = "auth_token";
	public static final String EMAIL = "email";
	public static final String FLIXSTER_USER_ID = "flixster_id";
	public static final String FLIXSTER_AUTH_TOKEN = "flixster_auth_token";
	public static final String OLD_PASSWORD = "old_password";
	public static final String PASSWORD = "password";
	public static final String LOCALE = "locale";
	public static final String CLIENT_TYPE = "client_type";
	public static final String DEVICE_TYPE = "device_type";
	public static final String CLIENT_TYPE_RESULT = TabletUtils.isTablet() ? "TABLET_ANDROID" : "MOBILE_ANDROID";
	public static final String CLIENT_VERSION = "client_version";
	public static final String LANGUAGE = "language";
	public static final String UDID = "udid";
	public static final String EXPIRE = "expire_in_days";
	public static final int EXPIRE_DAYS = 365;
	public static final String FIRST_NAME = "first_name";
	public static final String LAST_NAME = "last_name";
	public static final String USER_ID = "user_id";
	public static final String BIRTHDAY = "birthday";
	public static final String BIRTHDAY_DATE = "birthday_date";
	public static final String GENDER = "gender";
	public static final String COUNTRY = "country";
	public static final String USER_SOURCES = "sources";
	public static final String ACCESS_TOKEN = "access_token";
	public static final String UV_LINK_PROMPT = "uv_link_prompt";
	public static final String UV_LINKED = "uv_linked";
	public static final String TOU_COUNTRY_LIST = "tou_countries_list";
	public static final String TOU_COUNTRY_LIST_ARRAY = "tou_countries_list[]";
	public static final String UV_PRIMARY_EMAIL = "uv_primary_email";
	public static final String UNIVERSAL_OPT_IN = "universal_opt_in";
	
	public static final String APID = "apid";
	public static final String AUDIO = "audio";
	public static final String IS_DELETED = "isDeleted";
	public static final String STANDARD = "standard";
	public static final String AUDIO_LANGUAGE = "audio_language";
	public static final String SUBTITLE_LANGUAGE = "subtitle_language";
	public static final String AUDIO_PREF = "audio_pref";
	public static final String SUBTITLE_PREF = "subtitle_pref";

	public static final String FACEBOOK_USER_ID = "fb_user_id";
	public static final String FACEBOOK_ACCESS_TOKEN = "fb_access_token";
	
	public static final String GOOGLE_USER_ID = "google_id";
	public static final String GOOGLE_ACCESS_TOKEN = "google_access_token";
	public static final String GOOGLE_AUTH_CODE = "google_auth_code";

	public static final String FILE_TYPE = "file_type";
	
	public static final String RATING_LIST = "rating_list";
	public static final String RATING_BOARD = "rating_board";
	public static final String DESCRIPTION = "description";
	public static final String DISPLAY_NAME = "display_name";
	
	public static final String RELEASE_LIST = "release_list";
	public static final String RELEASE_TYPE = "release_type";
	public static final String RELEASE_DATE = "release_date";
	public static final String RUNTIME = "runtime";
	public static final String RUNTIME_IN_HRS = "runtime_in_hrs";
	public static final String RUNTIME_IN_MINUTES = "runtime_in_minutes";
	
	public static final String GENRE_LIST = "genre_list";
	
	public static final String TALENT_LIST = "talent_list";
	public static final String ROLE = "role";
	public static final String CHARACTER_NAME = "character_name";
	
	public static final String CLIENT_COUNTRY = "client_country";
	public static final String CLIENT_LANGUAGE = "client_language";
	
	public static final String CLIENT_INFO = "client_info";
	public static final String MEDIA_PROFILE = "media_profile";
	public static final String ANDROID_DRM_FRAMEWORK = "android_drm_framework";
	public static final String ANDROID_DRM_PDF = "PDF";
	public static final String ANDROID_DRM_ODF = "ODF";
	public static final String CHROMECAST = "CHROMECAST";
	public static final String STREAM_ID = "stream_id";
	public static final String EXPR_TIME = "expiration_time";

	public static final String USER_REVIEW_SUMMARY = "user_review_summary";
	public static final String CRITIC_REVIEW_SUMMARY = "critic_review_summary";
	public static final String SCORE = "score";
	
	public static final String DRM_SERVER_URL = "drm_server_url";
	public static final String FILE_LOCATION = "file_location";
	public static final String DEVICE_ID = "device_id";
	
	public static final String TOKEN_VALUE = "token_value";
	public static final String CODE = "code";
	public static final String OFFER_TYPE = "offer_type";
	public static final String GEO_IP_CHECK = "geo_ip_check";
	public static final String DISTRIBUTION_COUNTRY_LIST = "distribution_country_list";
	public static final String CONTENT_PROVIDER = "content_provider";
	public static final String GUID = "guid";
	public static final String OPT_IN = "opt_in";
	//public static final String OFFER_GUID = "offer_guid";
	public static final String SHORT_NAME = "short_name";
	public static final String TRAILER_URL = "trailer_url";
	
	public static final String START = "start";
	public static final String COUNT = "count";
	public static final String VALUE = "value";
	public static final String SORT = "sort";
	public static final String OPTIMIZED = "optimized";
	
	public static final String WARNING = "warning";
	
	public static final String CATALOGUE_FILTER = "catalogue_filter";
	public static final String CONTENT_FILTER = "content_filter";
	public static final String TYPE_FILTER = "type_filter";
	public static final String BUNDLE_FILTER = "bundle_filter";
	public static final String SORTING_FILTER = "sorting_filter";
	public static final String SORT_PARAM = "sort_param";
	public static final String SORT_ORDER = "sort_order";
	public static final String SKU_TYPES = "sku_types";
	public static final String GENRES = "genres";
	public static final String OFFSET = "offset";
	public static final String METER_RANGE_MIN = "meter_range_min";
	public static final String METER_RANGE_MAX = "meter_range_max";
	public static final String SEARCH = "search";

	public static final String PLAY_POSITION = "play_position";
	public static final String PLAYBACK_POSITION= "play_back_position";

	public static final String FLIXSTER_RIGHTS_ID = "flixster_rights_id";
	public static final String UV_RIGHTS_ID = "uv_rights_id";
	public static final String CAN_CANCEL = "can_cancel";
	
	
	/**************************************** UV URL **************************************/
	
	public static final String DEFAULT_UV_MANAGE_URL = "/rest/user/uvvu/login?returnto=urn:dece:portal:account";
	
	/**************************************** Download **************************************/ 
	
	public static final String FILE_PROTOCOL = "file://";
	public static final String HTTP_PROTOCOL = "http://";
	public static final String WV_DIR = "wv/"; 
	public static final String SUBTITLE_DIR = "wv/subtitle/";
	public static final String CACHE_DIR = "cache/";
	
	/**************************************** Widevine **************************************/
	
	public static final String WM_MIMETYPE = "video/wvm";
	
	//public static final String WV_SERVER_KEY = BASE_UTL + "/dc/drmlicense/widevine/stream";
	public static final String WV_SERVER_KEY_STREAM = "/drmlicense/widevine/stream";
	public static final String WV_SERVER_KEY_DOWNLOAD = "/drmlicense/widevine/download";
	public static final String PLAYREADY_SERVER_KEY_STREAM = "/drmlicense/playready/stream/";
	public static final String PLAYREADY_SERVER_KEY_DOWNLOAD = "/drmlicense/playready/download/";
	public static final String WV_SESSION_ID_KEY = "sess4321";
	public static final String WV_CLIENT_ID_KEY = "digitalcopy";
	public static final String WV_PORTAL_KEY = "sorbrenraw";
	public static final String WV_PORTAL_KEY_SONIC = "CN";
	public static final String WV_CA_DATA_KEY = "";
	public static final String WV_DRIVEN_KEY = "0";
	
	public static final String WVMimeType 		= "WVMimeType";
	public static final String WVAssetURIKey 	= "WVAssetURIKey";
	public static final String WVAssetRootKey	= "WVAssetRootKey";
	public static final String WVCAUserDataKey 	= "WVCAUserDataKey";
	public static final String WVDeviceIDKey 	= "WVDeviceIDKey";
	public static final String WVPortalKey 		= "WVPortalKey";
	public static final String WVStreamIDKey	= "WVStreamIDKey";
	public static final String WVFileDescriptorKey	= "FileDescriptorKey";
	public static final String WVLicenseTypeKey = "WVLicenseTypeKey";
	
	public static final int WV_LICENSE_TYPE_STREAMING = 1;
	public static final int WV_LICENSE_TYPE_OFFLINE = 2;
	public static final int WV_LICENSE_TYPE_BOTH = 3;
	
	/**************************************** Local Subtitle File Prefix **************************************/
	
	public static final String SUBTITLE_FILE_PREFIX = "sub_";
	
	/**************************************** Intent Extras Keys **************************************/
	
	public static final String CONTENT_ID_KEY = "CONTENT_ID";
	public static final String FLIX_URL = "FLIX_URL";
	public static final String ZOOM_TAG = "ZOOM_TAG";
	public static final String POSTER_URL = "poster_url";
	public static final String UV_DIALOG = "UV_DIALOG";
	public static final String JSON_STRING = "JSON_STRING";
	public static final String CALLER_NAME = "CALLER_NAME";
	public static final String CONFIRM_REDEMPTION_REQUEST = "CONFIRM_REDEMPTION_REQUEST";
	
	/**************************************** HTTPHelper **************************************/
	public static final String AUTHORIZATION = "Authorization";
	public static final String X_FORWARDED_FOR = "X-Forwarded-For";
	public static final String ATTACH_UDID_TO_AUTHTOKEN = "Attached_UDID";
	public static final String ACCEPT = "Accept";
	
	/**************************************** Error Codes **************************************/
	
	public static final String DC2_ERROR_UNKNOWN = "DC_404";
	public static final String DC2_ERROR_INVALID_COUNTRY = "DC_402";
	public static final String DC2_ERROR_MAX_STREAM = "ES_601";
	public static final String DC2_ERROR_LOGIN_FAIL = "DC_103";
	public static final String DC2_ERROR_INVALID_AUTH_TOKEN = "DC_105";
	public static final String DC2_ERROR_US_FLIXSTER_ACC_EXIST = "DC_106";
	public static final String DC2_ERROR_CREATE_ACC_FOR_FB = "DC_116";
	public static final String DC2_ERROR_AUTH_TOKEN_MALFORMED = "DC_122";
	public static final String ACCOUNT_DOB_MISSING = "DC_123";
	public static final String UV_ERROR_NOT_AUTHORIZED = "UV_40009";
	public static final String UV_ERROR_UNLINKED = "DC_119";
	public static final String DC2_ERROR_NO_ASSETS = "DC_1633";
	public static final String UV_ACCOUNT_UNAUTHORIZED_ACCESS = "DC_150";
	public static final String UV_ACCOUNT_OR_USER_NOT_ACTIVE = "DC_153";

	/**************************************** Request codes **************************************/
	public static final int UPDATE_USER_INFO_REQUEST = 1;
	
	public static final int UV_PROMO_DIALOG_REQUEST = 1400;
	public static final int UV_LINK_WEBVIEW_REQUEST = 1401;
	public static final int UV_UNFULFILLABLE_DIALOG_REQUEST = 1402;
	public static final int REPERMISSION_WEBVIEW_REQUEST = 1403;
	public static final int MANAGE_UV_WEBVIEW_REQUEST = 1404;
	public static final int UV_RELINK_WEBVIEW_REQUEST = 1405;
	
	public static final int SORT_REQUEST = 1500;
	
	/**************************************** Notification **************************************/
	public static final int NOTIFICATION_DOWNLOAD_STATUS = 1;
    public static final int NOTIFICATION_CAST_STATUS = 2;


}
