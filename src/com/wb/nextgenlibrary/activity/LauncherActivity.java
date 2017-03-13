package com.wb.nextgenlibrary.activity;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;

import com.wb.nextgenlibrary.NextGenExperience;
import com.wb.nextgenlibrary.R;

/**
 * Created by gzcheng on 11/1/16.
 */

public class LauncherActivity extends Activity {

	ProgressDialog mDialog;
	NextGenLauncherTask launcherTask;

	@Override
	protected void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);
		setContentView(R.layout.next_gen_launcher_view);

		mDialog = ProgressDialog.show(this, "", "Loading", false, true);
		mDialog.setCanceledOnTouchOutside(false);
		mDialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
			@Override
			public void onCancel(DialogInterface dialog) {
				LauncherActivity.this.finish();
			}
		});
		launcherTask = new NextGenLauncherTask();
		launcherTask.execute(NextGenExperience.getManifestItem());
	}

	@Override
	protected void onStart() {
		super.onStart();
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		if (launcherTask != null && !launcherTask.isCancelled())
			launcherTask.cancel(true);
	}


	private class NextGenLauncherTask extends AsyncTask<NextGenExperience.ManifestItem, Integer, Boolean> {
		protected Boolean doInBackground(NextGenExperience.ManifestItem... manifestItems) {

			return NextGenExperience.startNextGenParsing(manifestItems[0], NextGenExperience.getClientLocale());
		}

		protected void onProgressUpdate(Integer... progress) {
			//setProgressPercent(progress[0]);
		}

		protected void onPostExecute(Boolean result) {
			if (result){
				Intent intent = new Intent(LauncherActivity.this, StartupActivity.class);
				LauncherActivity.this.startActivity(intent);
			}
			mDialog.dismiss();
			mDialog.cancel();
			LauncherActivity.this.finish();
		}
	}


}
