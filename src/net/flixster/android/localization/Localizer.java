package net.flixster.android.localization;

import java.io.IOException;
import java.net.MalformedURLException;
import java.text.ParseException;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.Callable;

import net.flixster.android.localization.constants.KEYS;
import net.flixster.android.localization.dao.LocalizationsDAO;

import com.wb.nextgen.NextGenApplication;
import com.wb.nextgen.util.ExceptionHandler;
import com.wb.nextgen.util.ResourceUtils;
import com.wb.nextgen.util.concurrent.ResultListener;
import com.wb.nextgen.util.concurrent.Worker;
import com.wb.nextgen.util.utils.F;
import com.wb.nextgen.util.utils.FlixsterLogger;

import org.json.JSONException;
import org.json.JSONObject;

import android.content.res.Resources;
import android.os.Handler;


/**
 * The Class Localizer. Core class of localization. This app not used andorid string xml. On application startup
 * Localizer request remote server and parse response to HashMap, app use this map to get strings by keys form enum KYES
 * 
 * @author vandreev
 */
final public class Localizer {
	
	private static Map<KEYS, String> localizedKeys = new EnumMap<KEYS, String>(KEYS.class);
	private static Map<KEYS, String> backupLocalizedKeys = new EnumMap<KEYS, String>(KEYS.class);
	private static Map<String, String> allLocalizedStrings = new HashMap<String, String>();
	private static Map<String, String> backupAllLocalizedStrings = new HashMap<String, String>();

	public final static int LOAD_TRANSLATION_COMPLETE = 9082103;
	private static Locale savedLocale;
	
	/**
	 * Inits the localization of application mast be invoked first.
	 * 
	 * @param l the l
	 */
	public static void init(final ResultListener<Locale> l) {
		// at start use local locale
		savedLocale = Resources.getSystem().getConfiguration().locale;
		Worker.execute(new Callable<Map<KEYS, String>>() {
			
			@Override
			public Map<KEYS, String> call() throws Exception {
				Locale locale = NextGenApplication.getLocale();
				Map<KEYS, String> templocale = null;
				try {
					templocale = initOfflineLocalization(locale);
					FlixsterLogger.d(F.TAG, "localization used from cache");
				} catch (Exception e) {
					templocale = new EnumMap<KEYS, String>(backupLocalizedKeys);
					FlixsterLogger.d(F.TAG, "localization used from assets");
				}
				
				//if (NextGenApplication.isConnected()) {
					boolean isfileNew = false;
					try {
						isfileNew = LocalizationsDAO.isLocaleModified(""/*FlixsterAPI.getLocalizationURL(locale.toString())*/);
					} catch (Exception e) {
						ExceptionHandler.logException(e, Localizer.class);
					}
					if (isfileNew) {
						Map<KEYS, String> newlocale = null;
						try {
							newlocale = changeLocalization(locale);
						} catch (Exception e) {
							ExceptionHandler.logException(e, Localizer.class);
						}
						if (newlocale != null)
							templocale = newlocale;
						FlixsterLogger.d(F.TAG, "localization used from server");
					}
				//}
				
				return templocale;
			}
		}, new ResultListener<Map<KEYS, String>>() {
			
			@Override
			public void onResult(Map<KEYS, String> result) {
				localizedKeys = result;
				l.onResult(NextGenApplication.getLocale());
			}
			
			@Override
			public <E extends Exception> void onException(E e) {
				initDefaultLocalization(NextGenApplication.getLocale());
				l.onResult(NextGenApplication.getLocale());
				
			}
		});
		
	}
	
	/**
	 * Gets the.
	 * 
	 * @param key the key
	 * @return the string
	 */
	public static String get(KEYS key) {
		String s = localizedKeys.get(key);
		if (s == null || s.length() == 0) {
			s = backupLocalizedKeys.get(key);	//should look for default error message if there is none.
			FlixsterLogger.d(F.TAG, "Localizer.get " + key + " from backup: " + s);
		} else {
			//Log.d("Localizer","good - " + s);
		}
		return s != null ? s : "";
	}
	
	private static final String errorStringPrefix = "Hint-error-";
	
	public static String getMessageForErrorCode(String errCode){
		
		if (errCode.equals("FLX_104") ||errCode.equals("DC_103") ||errCode.equals("DC_200")) {	//Bad credentials
			errCode = "DC_100";
		} else if (errCode.equals("40003") ){ //Bad email address
			errCode = "DC_101";
		} else if (errCode.equals("DC_1093") ||  errCode.equals("DC_701") ){ //Invalid redemption code
			errCode = "DC_503";
		} else if (errCode.equals("DC_1623") ||  errCode.equals("ES_704") ){ //SKU not available
			errCode = "DC_1622";
		}
		
		
		
		String keyCode = errorStringPrefix + errCode;
		String s = allLocalizedStrings.get(keyCode);
		if (s == null || s.length() == 0) {
			s = backupAllLocalizedStrings.get(keyCode);	//should look for default error message if there is none.
			FlixsterLogger.d(F.TAG, "Localizer.getMessageForErrorCode " + keyCode + " from backup: " + s);
		}else {
			//Log.d("Localizer","good - " + s);
		}
		return s != null ? s : get(KEYS.HINT_ERROR_GENERICERRORMESSAGE);
	}

	private static final String studioOptInStringPrefix = "provider-optIn-";
	
	public static String getStudioOptInString(String studioProvider){
		String keyCode = studioOptInStringPrefix + studioProvider;
		String s = allLocalizedStrings.get(keyCode);
		if (s == null || s.length() == 0) {
			s = backupAllLocalizedStrings.get(keyCode);	//should look for default error message if there is none.
			FlixsterLogger.d(F.TAG, "Localizer.getStudioOptInString " + keyCode + " from backup: " + s);
		}else {
			//Log.d("Localizer","good - " + s);
		}
		return s != null ? s : "";
	}
	
	/**
	 * Gets the app locale.
	 * 
	 * @return the app locale
	 */
	public static Locale getLocale() {
		return NextGenApplication.getLocale();
	}
	
	/**
	 * Change localization.
	 * 
	 * @param locale the locale
	 * @return the map
	 * @throws MalformedURLException the malformed url exception
	 * @throws IOException Signals that an I/O exception has occurred.
	 * @throws JSONException the jSON exception
	 * @throws ParseException the parse exception
	 */
	public static Map<KEYS, String> changeLocalization(final Locale locale) throws MalformedURLException, IOException,
			JSONException, ParseException {
		return LocalizationsDAO.getLocalizationFromURL(""/*FlixsterAPI.getLocalizationURL(locale.toString())*/, false, true);
		
	}
	
	/**
	 * Inits the offline localization.
	 * 
	 * @param locale
	 * @throws ParseException
	 * @throws JSONException
	 * @throws IOException
	 * @throws MalformedURLException
	 */
	private static Map<KEYS, String> initOfflineLocalization(final Locale locale) throws MalformedURLException,
			IOException, JSONException, ParseException {
		return LocalizationsDAO.getLocalizationFromURL(""/*FlixsterAPI.getLocalizationURL(NextGenApplication.getLocale().toString())*/, true, false);
		
	}
	
	/**
	 * Inits the default localization from assets folder
	 * 
	 * @param locale the locale
	 * @return the map
	 */
	public static boolean initDefaultLocalization(final Locale locale) {
		JSONObject jo = null;
		try {
			jo = new JSONObject(ResourceUtils.getStringFromAssets(locale.toString() + ".json"));
		} catch (JSONException je) {
			FlixsterLogger.e(F.TAG, "Localizer.initDefaultLocalization: Critical Error, cant parse locale from assets", je);
			return false;
		}

		backupAllLocalizedStrings = new HashMap<String, String>(LocalizationsDAO.jsonToMap(jo));
		allLocalizedStrings = new HashMap<String, String>(backupAllLocalizedStrings);

		backupLocalizedKeys = new EnumMap<KEYS, String>(LocalizationsDAO.getLocalizationFromJSON(jo));
		localizedKeys = new EnumMap<KEYS, String>(backupLocalizedKeys);

		return true;
	}
	
	public static boolean isTextloaded(){
		return localizedKeys != null && !localizedKeys.isEmpty() && allLocalizedStrings!=null && !allLocalizedStrings.isEmpty();
	}
	
	public static boolean isLocaleChanged(){
		Locale newLocale = Resources.getSystem().getConfiguration().locale;
		boolean result = savedLocale == null || ( (savedLocale != null) && !newLocale.equals(savedLocale) );
		return result;
	}
	
	public static void loadTranslations(final Handler handler){
		
		Localizer.init(new ResultListener<Locale>() {
				
			@Override
			public void onResult(Locale result) {
				handler.sendEmptyMessage(LOAD_TRANSLATION_COMPLETE);
			}
			
			@Override
			public <E extends Exception> void onException(E e) {
				if (!Localizer.initDefaultLocalization(NextGenApplication.getLocale())){

					//NextGenApplication.setLocale(Locale.US);
					Localizer.initDefaultLocalization(Locale.US);
				}
				handler.sendEmptyMessage(LOAD_TRANSLATION_COMPLETE);
			}
		});
		
		
	}
	
	
	public enum Domains {
		AT(new String[]{"de"}),
		BE(new String[]{"fr","nl"}),
		BR(new String[]{"pt"}),
		DK(new String[]{"da","fi","nb","sv"}),
		FI(new String[]{"fi","da","nb","sv"}),
		FR(new String[]{"fr"}),
		DE(new String[]{"de"}),
		IT(new String[]{"it"}),
		JP(new String[]{"ja"}),
		LU(new String[]{"fr","de","nl"}),
		MX(new String[]{"es"}),
		NL(new String[]{"nl","fr"}),
		NO(new String[]{"nb","da","fi","sv"}),
		ES(new String[]{"es"}),
		SE(new String[]{"sv","da","fi","nb"}),
		CH(new String[]{"de","fr","it"}),
		;
		
		public String toString()
	    {
	        return this.name();
	    }
	    
		public final String[] languages;
		
		public static Domains languagesFromCountry(String country) {
			for (Domains key : Domains.values()) {
				String s = key.name();
				if(s.equalsIgnoreCase(country)) 
					return key;
			}
			
			return null;
		}
		
		Domains(String[] languages) {
			this.languages = languages;
		}
	};
}
