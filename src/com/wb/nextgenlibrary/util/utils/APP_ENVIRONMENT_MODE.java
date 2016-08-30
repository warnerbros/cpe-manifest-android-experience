package com.wb.nextgenlibrary.util.utils;

public enum APP_ENVIRONMENT_MODE {
	UNDEFINITED, LOCAL_MODE, DEV_MODE, QA_MODE, PRODUCTION_MODE;
	
	public String toString(){
		return this.name();
	}
	
	
	public static APP_ENVIRONMENT_MODE envFromString(String keyString) {
		if (keyString == null)
			return PRODUCTION_MODE;	
		
		for (APP_ENVIRONMENT_MODE key : APP_ENVIRONMENT_MODE.values()) {
			String s = key.name();

			if(s.equalsIgnoreCase(keyString.toString())) 
				return key;
		}
		//TODO vandreev return some deafault value
		return PRODUCTION_MODE;
	}

	
	
}
