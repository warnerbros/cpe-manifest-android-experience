package com.wb.nextgenlibrary.util;


import com.wb.nextgenlibrary.util.utils.StringHelper;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;

final public class PaswordValidator {
	public static final String NOT_VALID_FIELD = "NOT_VALID_FIELD";
	private static final String USERNAME_PATTERN = "^[a-zA-Z0-9_-]{1,20}$";
	private static final String USERNAME_PATTERN2 = "^[\\.@a-zA-Z0-9_-]{1,20}$";
	private static final String PASSWORD_PATTERN = "^|[^ ]{6,20}$";
	private static final String EMAIL_PATTERN = "^[_A-Za-z0-9-\\+]+(\\.[_A-Za-z0-9-\\+]+)*@([-0-9a-zA-Z]+[.])+[a-zA-Z]{2,6}$";
	
	private PaswordValidator() {
	};
	
	public static boolean validateSignInFields(Context context, Handler handler, String username, String password) {
		StringBuilder s = new StringBuilder();
		
		if (!username.matches(EMAIL_PATTERN) || !password.matches(PASSWORD_PATTERN) || password.length() == 0) {
			//s.append(Localizer.getMessageForErrorCode(F.DC2_ERROR_LOGIN_FAIL) + "\n\n");
		}
		
		if (s.length() > 0) {
			Bundle b = new Bundle();
			b.putString(NOT_VALID_FIELD, s.toString());
			Message msg = new Message();
			msg.setData(b);
			handler.sendMessage(msg);
			return false;
		}
		
		return true;
	}
	
	public static boolean validateEmail(String email){
		return !StringHelper.isEmpty(email) && email.matches(EMAIL_PATTERN);
	}
}
