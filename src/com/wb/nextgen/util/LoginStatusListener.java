package com.wb.nextgen.util;

public interface LoginStatusListener {
	enum LoginStatus{ USER_LOGGED_IN, USER_LOGGED_OUT};
	
	public void onUserLoggedInStatusChanged(LoginStatus newStatus);
}
