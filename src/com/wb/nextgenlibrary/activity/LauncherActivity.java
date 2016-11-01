package com.wb.nextgenlibrary.activity;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;

import com.wb.nextgenlibrary.NextGenExperience;
import com.wb.nextgenlibrary.R;
import com.wb.nextgenlibrary.util.concurrent.ResultListener;
import com.wb.nextgenlibrary.util.concurrent.Worker;
import com.wb.nextgenlibrary.util.utils.StringHelper;

import java.util.concurrent.Callable;

/**
 * Created by gzcheng on 11/1/16.
 */

public class LauncherActivity extends Activity {
	@Override
	protected void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);
		setContentView(R.layout.next_gen_launcher_view);
	}

	@Override
	protected void onStart() {
		super.onStart();
		final ProgressDialog mDialog = ProgressDialog.show(this, "", "Loading", false, false);

		final NextGenExperience.ManifestItem item = NextGenExperience.getManifestItem();
		if (!StringHelper.isEmpty(item.getManifestFileUrl())) {
			Worker.execute(new Callable<Boolean>() {
				public Boolean call() throws Exception {
					return NextGenExperience.startNextGenParsing(item);
				}
			}, new ResultListener<Boolean>() {
				public void onResult(Boolean result) {
					if (result) {
						LauncherActivity.this.runOnUiThread(new Runnable() {
							@Override
							public void run() {

								mDialog.dismiss();
								mDialog.cancel();
								Intent intent = new Intent(LauncherActivity.this, NextGenActivity.class);
								LauncherActivity.this.startActivity(intent);
								LauncherActivity.this.finish();
							}
						});
					} else {
						// TODO error handling if startNextGenParsing returns false
						mDialog.dismiss();
						mDialog.cancel();
					}
				}

				public void onException(Exception e) {
					mDialog.dismiss();
					mDialog.cancel();

				}
			});
		}else {
			mDialog.dismiss();
			mDialog.cancel();
		}

	}

}
