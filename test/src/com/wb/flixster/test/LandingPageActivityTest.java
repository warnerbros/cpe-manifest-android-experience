package com.flixster.video.test;

import com.robotium.solo.Solo;
import com.wb.nextgen.R;

import net.flixster.android.FlixsterApplication;
import net.flixster.android.view.ChangeNameViewActivity;
import net.flixster.android.view.CollectionActivity;
import net.flixster.android.view.Flixster;
import net.flixster.android.view.LandingPageActivity;
import net.flixster.android.view.LoginDialogView;
import net.flixster.android.view.ProfileViewActivity;
import android.app.ActionBar.Tab;
import android.test.ActivityInstrumentationTestCase2;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TabHost;
import android.widget.TableRow;

public class LandingPageActivityTest extends
		ActivityInstrumentationTestCase2<LandingPageActivity> {

	private Solo solo;
	
	public LandingPageActivityTest() {
		super(LandingPageActivity.class);
	}

	@Override
	public void setUp() throws Exception {
		solo = new Solo(getInstrumentation(), getActivity());
	}
	
	@Override
	public void tearDown() throws Exception {
		solo.finishOpenedActivities();
	}
	
	public void testLogin() {
		// find log in button and click on it
		Button signInButton = (Button) solo.getView(R.id.button_sign_in);
		assertNotNull("Assert Log In button on LandingPageActivity is not null", signInButton);
		solo.clickOnButton(signInButton.getText().toString());
		solo.assertCurrentActivity("Assert LoginDialogView is opened", LoginDialogView.class.getSimpleName());
		
		// find user edittext and enter user
		EditText userText = (EditText) solo.getView(R.id.editText_user);
		assertNotNull("Assert Username EditText is not null", userText);
		solo.enterText(userText, "qa@stomata.com");

		// find password edittext and enter password
		EditText passwordText = (EditText) solo.getView(R.id.editText_password);
		assertNotNull("Assert Password EditText is not null", passwordText);
		solo.enterText(passwordText, "password");
		
		// find log in button and click on it
		signInButton = (Button) solo.getView(R.id.button_sign_in);
		assertNotNull("Assert Log In button on LoginDialogView is not null", signInButton);
		solo.clickOnButton(signInButton.getText().toString());
		
		solo.waitForActivity(CollectionActivity.class.getSimpleName());
	}
	
	public void testChangeName() {
		testLogin();
		solo.clickOnText("Profile");

		solo.waitForActivity(ProfileViewActivity.class.getSimpleName());

		TableRow name_row = (TableRow) solo.getView(R.id.profile_name_row);
		solo.clickOnView(name_row);
		solo.waitForActivity(ChangeNameViewActivity.class.getSimpleName());

		solo = new Solo(getInstrumentation(), getActivity());
		
		EditText firstnameEditText = (EditText) solo.getView(R.id.firstname_edit_text);
		solo.enterText(firstnameEditText, "1");
		
		EditText lastnameEditText = (EditText) solo.getView(R.id.lastname_edit_text);
		solo.enterText(lastnameEditText, "2");
		

		Button submitButton = (Button) solo.getView(R.id.change_name_submit_btn);
		solo.clickOnButton(submitButton.getText().toString());
		solo.waitForActivity(ProfileViewActivity.class.getSimpleName());
	}
	
	public void testLogOut() {
		testLogin();

		solo = new Solo(getInstrumentation(), getActivity());
		Button moreButton = (Button) solo.getView(R.id.main_more_button);
		solo.clickOnButton(moreButton.getText().toString());
	}
}
