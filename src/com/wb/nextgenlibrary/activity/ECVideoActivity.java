package com.wb.nextgenlibrary.activity;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.method.ScrollingMovementMethod;
import android.view.View;
import android.widget.TextView;

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

	public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

		//rightVideoFrame = (ECVideoViewFragment) getSupportFragmentManager().findFragmentById(R.id.ec_video_view_fragment);
		ecVideoFragment = new ECVideoViewFragment();

		castFragment = new ECCastPlayerFragment();

		if (NextGenExperience.getNextGenEventHandler().isCasting()) {
			activePlayerInterface = castFragment;
		} else {
			activePlayerInterface = ecVideoFragment;
		}


		activePlayerInterface.setShouldAutoPlay(false);
		activePlayerInterface.setEcsAdaptor(this);



		fragmentTransactionEngine = new NextGenFragmentTransactionEngine(this);
		fragmentTransactionEngine.transitFragment(getSupportFragmentManager(), R.id.next_gen_ec_content_view, (Fragment)activePlayerInterface);


        contentMetaFrame = (View) findViewById(R.id.ec_content_meta_frame);
		selectedECNameTextView = (TextView) findViewById(R.id.ec_content_name);
		descriptionTextView = (TextView) findViewById(R.id.ec_content_description);
		if (descriptionTextView != null)
			descriptionTextView.setMovementMethod(new ScrollingMovementMethod());
    }

    @Override
    public void onDestroy() {
        if (activePlayerInterface != null)
			activePlayerInterface.setEcsAdaptor(null);
        super.onDestroy();
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
}
