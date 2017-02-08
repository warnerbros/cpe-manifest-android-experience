package com.wb.nextgenlibrary.activity;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.method.ScrollingMovementMethod;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.google.android.gms.cast.framework.CastButtonFactory;
import com.google.android.gms.cast.framework.CastContext;
import com.google.android.gms.cast.framework.CastSession;
import com.google.android.gms.cast.framework.CastState;
import com.google.android.gms.cast.framework.CastStateListener;
import com.google.android.gms.cast.framework.Session;
import com.google.android.gms.cast.framework.SessionManager;
import com.google.android.gms.cast.framework.SessionManagerListener;
import com.google.android.gms.cast.framework.media.RemoteMediaClient;
import com.wb.nextgenlibrary.NextGenExperience;
import com.wb.nextgenlibrary.R;
import com.wb.nextgenlibrary.analytic.NextGenAnalyticData;
import com.wb.nextgenlibrary.data.MovieMetaData;
import com.wb.nextgenlibrary.fragment.ECCastPlayerFragment;
import com.wb.nextgenlibrary.fragment.ECVideoViewFragment;
import com.wb.nextgenlibrary.interfaces.ECVideoPlayerInterface;
import com.wb.nextgenlibrary.util.utils.NextGenFragmentTransactionEngine;


/**
 * Created by gzcheng on 3/7/16.
 */
public class ECVideoActivity extends AbstractECView implements ECVideoViewFragment.ECVideoListAdaptor{

	ECVideoViewFragment ecVideoFragment;
	ECCastPlayerFragment castFragment;

	ECVideoPlayerInterface activePlayerInterface;

	protected View contentMetaFrame;
	protected TextView selectedECNameTextView;
	protected TextView descriptionTextView;

	private NextGenFragmentTransactionEngine fragmentTransactionEngine;
	private Menu mOptionsMenu;


	public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

		//rightVideoFrame = (ECVideoViewFragment) getSupportFragmentManager().findFragmentById(R.id.ec_video_view_fragment);
		ecVideoFragment = new ECVideoViewFragment();

		castFragment = new ECCastPlayerFragment();


        contentMetaFrame = (View) findViewById(R.id.ec_content_meta_frame);
		selectedECNameTextView = (TextView) findViewById(R.id.ec_content_name);
		descriptionTextView = (TextView) findViewById(R.id.ec_content_description);
		if (descriptionTextView != null)
			descriptionTextView.setMovementMethod(new ScrollingMovementMethod());

		resetActivePlaybackFragment();
    }


    @Override
    public void onDestroy() {
        if (activePlayerInterface != null)
			activePlayerInterface.setEcsAdaptor(null);

        super.onDestroy();
    }

	@Override
	public boolean onPrepareOptionsMenu(final Menu menu) {
		if (mOptionsMenu == null) {
			getMenuInflater().inflate(R.menu.next_gen_cast_option_menu, menu);
			mOptionsMenu = menu;
			return super.onCreateOptionsMenu(menu);
		}
		//resetMenuItems();
		return true;
	}

	/**
	 * Set up action bar items
	 */
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		super.onCreateOptionsMenu(menu);

		getMenuInflater().inflate(R.menu.next_gen_cast_option_menu, menu);

		mOptionsMenu = menu;


			CastButtonFactory.setUpMediaRouteButton(getApplicationContext(),
					menu,
					R.id.menuChromecast);



		return true;
	}

    @Override
    public int getListItemViewLayoutId(){
        return R.layout.next_gen_ec_list_item;
    }


    @Override
    public int getContentViewId(){
        return R.layout.next_gen_ecs_view;
    }

    @Override
    public void onLeftListItemSelected(MovieMetaData.ExperienceData ec){
        if (selectedEC == ec)      // avoid video reload
            return;
        else
            selectedEC = ec;
        if (ec != null && ec.audioVisualItems.size() > 0) {
			MovieMetaData.AudioVisualItem audioVisualItem = ec.audioVisualItems.get(0);
			if (activePlayerInterface != null)
				activePlayerInterface.setAudioVisualItem(audioVisualItem);
			activePlayerInterface.setResumeTimeMillisecond(-1);

			if (selectedECNameTextView != null)
				selectedECNameTextView.setText(audioVisualItem.getTitle());
			if (descriptionTextView != null)
				descriptionTextView.setText(audioVisualItem.getSummary());
            NextGenAnalyticData.reportEvent(this, null, NextGenAnalyticData.AnalyticAction.ACTION_SELECT_VIDEO, audioVisualItem.videoId, null);
        }
    }

    @Override
    public void onRequestToggleFullscreen() {
        super.onRequestToggleFullscreen();
    }

    @Override
    public void onFullScreenChange(boolean bFullscreen){
        if (!bFullscreen && getSupportActionBar() != null) {

            getSupportActionBar().hide();
        }
		if (activePlayerInterface != null)
			activePlayerInterface.onFullScreenChange(bFullscreen);

        if (contentMetaFrame != null)
            contentMetaFrame.setVisibility(bFullscreen? View.GONE : View.VISIBLE);

    }

    @Override
    public void playbackFinished(){
        listFragment.selectNextItem();
    }

    @Override
    public boolean shouldStartCountDownForNext(){
        return listFragment.hasReachedLastItem();
    }

	@Override
	public void onBackPressed(){
		finish();
	}

	@Override
	public void onCastStateChanged(int i) {
		switch (i){
			case CastState.CONNECTED:
				resetActivePlaybackFragment();
				break;
			case CastState.CONNECTING:
				break;
			case CastState.NO_DEVICES_AVAILABLE:
				break;
			case CastState.NOT_CONNECTED:
				resetActivePlaybackFragment();
				break;
		}
	}

	private void resetActivePlaybackFragment(){
		if (mSessionManager != null)
			mCastSession = mSessionManager.getCurrentCastSession();
		int currentPos = 0;
		if (activePlayerInterface != null){
			currentPos = activePlayerInterface.getCurrentPlaybackTimeMillisecond();

		}

		if (isCasting()) {
			activePlayerInterface = castFragment;
			castFragment.setCastControllers(mCastSession, remoteMediaClient, mSessionManager);
		} else {
			activePlayerInterface = ecVideoFragment;
		}

		activePlayerInterface.setShouldAutoPlay(false);
		activePlayerInterface.setEcsAdaptor(this);


		if (selectedEC != null && selectedEC.audioVisualItems.size() > 0) {
			MovieMetaData.AudioVisualItem audioVisualItem = selectedEC.audioVisualItems.get(0);
			if (activePlayerInterface != null) {
				activePlayerInterface.setAudioVisualItem(audioVisualItem);
				activePlayerInterface.setResumeTimeMillisecond(currentPos);
			}

			if (selectedECNameTextView != null)
				selectedECNameTextView.setText(audioVisualItem.getTitle());
			if (descriptionTextView != null)
				descriptionTextView.setText(audioVisualItem.getSummary());
			NextGenAnalyticData.reportEvent(this, null, NextGenAnalyticData.AnalyticAction.ACTION_SELECT_VIDEO, audioVisualItem.videoId, null);
		}

		fragmentTransactionEngine = new NextGenFragmentTransactionEngine(this);
		fragmentTransactionEngine.replaceFragment(getSupportFragmentManager(), R.id.next_gen_ec_content_view, (Fragment)activePlayerInterface);
	}
}
