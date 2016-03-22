package net.flixster.android.localization.dao;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Locale;
import java.util.Map;
import java.util.TimeZone;

import net.flixster.android.localization.constants.KEYS;
import com.wb.nextgen.util.HttpHelper;
import com.wb.nextgen.util.utils.F;
import com.wb.nextgen.util.utils.NextGenLogger;

import org.json.JSONException;
import org.json.JSONObject;


final public class LocalizationsDAO {
	
	public static Map<KEYS, String> getLocalizationFromURL(final String url, final boolean fromCache,
			final boolean shouldCache) throws MalformedURLException, IOException, JSONException, ParseException {
		return getLocalizationFromJSON(new JSONObject(HttpHelper.fetchUrl(new URL(url), fromCache, shouldCache)));
	}
	
	public static boolean isLocaleModified(final String url) throws MalformedURLException, IOException, ParseException {
		String lastModification = HttpHelper.fetchFileLastModifiedDateFromUrl(new URL(url));
		if (lastModification == null || lastModification.length() == 0)
			return false;
		NextGenLogger.d(F.TAG_API, "Localizer lastModification: " + lastModification);
		DateFormat date = new SimpleDateFormat("EEE, dd MMM yyyy kk:mm:ss z", Locale.ENGLISH);
		date.setTimeZone(TimeZone.getTimeZone("GMT"));
		Date newDate = date.parse(lastModification);
		/*Date oldDate = FlixsterApplication.getLocalizationLastModifiedDate(url);
		if (oldDate == null || oldDate.compareTo(newDate) < 0) {
			FlixsterApplication.setLocalizationLastModifiedDate(url, lastModification);
			return true;
		} else {*/
			return false;
		//}
		
	}
	
	public static Map<KEYS, String> getLocalizationFromJSON(final JSONObject jo) {
		Map<KEYS, String> map = new EnumMap<KEYS, String>(KEYS.class);
		for (KEYS key : KEYS.values()) {
			if (jo.has(key.value))
				map.put(key, jo.optString(key.value));
		}
		return map;
	}
	
	public static Map<String, String> jsonToMap(final JSONObject jo){
		Map<String, String> map = new HashMap<String, String>();
		
		Iterator<?> keys = jo.keys();
		
		while(keys.hasNext()){
			try{
				String key = (String)keys.next();
				map.put(key, jo.getString(key));
			}catch (JSONException ex){}
		}
		
		return map;
	}
	
}
