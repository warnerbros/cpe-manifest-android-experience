package com.wb.nextgenlibrary.util;

import java.util.ArrayList;
import java.util.Arrays;

import android.os.Build;


public class NGEUtils {

    // copy a String with a NEW tailored backing char[]
    public static String copyString(String string) {
        if (string == null) {
            return null;
        }
        char tempChars[] = string.toCharArray();
        String tempString = String.valueOf(tempChars);
        return tempString;
    }
    
    private final static ArrayList<String> S5_MODELS_LIST = new ArrayList<String>(Arrays.asList(
    		"SM-G900A",	//AT&T
        	"SM-G900F", "SM-G900H", "SM-G900I", "SM-G900R4", "SM-G900T", //(T-Mobile)
        	"SM-G900V", //(Verizon).
        	"SM-G900RZWAUSC", //(U.S. Cellular).
        	"SM-G900P", //(Sprint).
        	"SM-G900W8", //(Canadian LTE).
        	"SM-G900FD", 
        	// Galaxy S5 Mini
        	"SM-G800F", "SM-G800H", "SM-G800H/DS", "SM-G800M", "SM-G800Y"
        ));
    
    private static boolean bCheckedDeviceModel = false;
    private static boolean bIsGalaxyS5 = false;
    
    public static boolean isDeviceGalaxyS5(){
    	if (!bCheckedDeviceModel){
    		bCheckedDeviceModel = true;
    		bIsGalaxyS5 = false; 
    		if ("Samsung".equalsIgnoreCase(Build.MANUFACTURER)){
    			for(String s5Model : S5_MODELS_LIST){
    				if (Build.MODEL.contains(s5Model)){
    					bIsGalaxyS5 = true;
    					break;
    				}
    			}
    			
    		}else
    			bIsGalaxyS5 = false;
    	}
		return bIsGalaxyS5;
    	
    }

	public static String getFacebookLogoUrl(){
		return "https://www.facebookbrand.com/img/trademarks/fb-2.jpg";
	}

	public static String getInstagramLogoUrl(){
		return "https://www.facebookbrand.com/img/trademarks/instagram-2.jpg";
	}

	public static String getTwitterLogoutUrl(){
		return "https://g.twimg.com/about/feature-corporate/image/twitterbird_RGB.png";
	}

	public static String getPacakageImageUrl(int id){
		return "android.resource://com.wb.nextgen/drawable/" + id;
	}
}