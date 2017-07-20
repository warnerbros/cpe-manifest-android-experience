package com.wb.nextgenlibrary.fragment;

import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import com.wb.nextgenlibrary.NextGenExperience;
import com.wb.nextgenlibrary.R;
import com.wb.nextgenlibrary.analytic.NGEAnalyticData;
import com.wb.nextgenlibrary.data.MovieMetaData;
import com.wb.nextgenlibrary.videoview.IVideoViewActionListener;
import com.wb.nextgenlibrary.widget.CustomMediaController;


/**
 * Created by gzcheng on 3/31/16.
 */
public class ShareClipFragment extends ECVideoViewFragment implements View.OnClickListener{
    TextView shareClipTitleTextView, ecContentNameTextView;
    Button shareClipButton;
    ImageButton prevClipButton, nextClipButton;
    MovieMetaData.ExperienceData shareClipExperience;
    int itemIndex;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.share_clip_view, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        shareClipButton = (Button)view.findViewById(R.id.share_clip_button);
        if (shareClipButton != null){
            shareClipButton.setOnClickListener(this);
        }

        prevClipButton = (ImageButton) view.findViewById(R.id.prev_clip_btn);
        if (prevClipButton != null){
            prevClipButton.setOnClickListener(this);
            //Glide.with(getActivity()).load(NGEUtils.getPacakageImageUrl(R.drawable.back_logo)).into(prevClipButton);
        }
        nextClipButton = (ImageButton) view.findViewById(R.id.next_clip_btn);
        if (nextClipButton != null){
            nextClipButton.setOnClickListener(this);
            //Glide.with(getActivity()).load(NGEUtils.getPacakageImageUrl(R.drawable.back_logo)).into(nextClipButton);
        }
        shareClipTitleTextView = (TextView) view.findViewById(R.id.share_clip_title_txt);
        if (shareClipTitleTextView != null && shareClipExperience != null){
            shareClipTitleTextView.setText(shareClipExperience.title);
        }

        ecContentNameTextView = (TextView) view.findViewById(R.id.ec_content_name);
        if (ecContentNameTextView != null && selectedAVItem != null){
            ecContentNameTextView.setText(selectedAVItem.getTitle());
        }


        updateUI(true);
        mediaController.setVisibilityChangeListener(new CustomMediaController.MediaControllerVisibilityChangeListener() {
            @Override
            public void onVisibilityChange(boolean bShow) {
                if (bShow) {
                    prevClipButton.setVisibility( View.INVISIBLE);
                    nextClipButton.setVisibility( View.INVISIBLE);
                } else {
                    updateUI(false);
                    /*prevClipButton.setVisibility( View.VISIBLE);
                    nextClipButton.setVisibility( View.VISIBLE);*/
                }
            }
        });
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.share_clip_button) {
            playerControl.pause();
            String videoUrl = selectedAVItem.getVideoUrl();

            NextGenExperience.getNextGenEventHandler().handleShareLink(null, this, videoUrl);

            Intent share = new Intent(Intent.ACTION_SEND);
            share.setType("text/plain");
            share.addFlags(Intent.FLAG_ACTIVITY_CLEAR_WHEN_TASK_RESET);
            share.putExtra(Intent.EXTRA_SUBJECT, "Next Gen Share");
            share.putExtra(Intent.EXTRA_TEXT, videoUrl);

            startActivity(Intent.createChooser(share, ""));
            NGEAnalyticData.reportEvent(getActivity(), ShareClipFragment.this, NGEAnalyticData.AnalyticAction.ACTION_SHARE_VIDEO, selectedAVItem.videoId, null);
        } else if (v.getId() == R.id.prev_clip_btn){
                if (itemIndex > 0){
                    setShouldAutoPlay(false);
                    setExperienceAndIndex(shareClipExperience, itemIndex - 1);
                    updateUI(true);
                    NGEAnalyticData.reportEvent(getActivity(), ShareClipFragment.this, NGEAnalyticData.AnalyticAction.ACTION_SELECT_PREVIOUS, selectedAVItem.videoId, null);
                }
        } else if (v.getId() == R.id.next_clip_btn){
                if (itemIndex < shareClipExperience.getChildrenContents().size()){
                    setShouldAutoPlay(false);
                    setExperienceAndIndex(shareClipExperience, itemIndex + 1);
                    updateUI(true);
                    NGEAnalyticData.reportEvent(getActivity(), ShareClipFragment.this, NGEAnalyticData.AnalyticAction.ACTION_SELECT_NEXT, selectedAVItem.videoId, null);
                }


        }
    }

    public void setExperienceAndIndex(MovieMetaData.ExperienceData shareClipExperience, int itemIndex){
        this.shareClipExperience = shareClipExperience;
        this.itemIndex = itemIndex;
        MovieMetaData.AudioVisualItem presentationDataItem = shareClipExperience.getChildrenContents().get(itemIndex).audioVisualItems.get(0);
        if (ecContentNameTextView != null && presentationDataItem != null){
            ecContentNameTextView.setText(presentationDataItem.getTitle());
        }
        setAudioVisualItem(presentationDataItem);
    }

    public void updateUI(boolean bHideController){
        int prevBtnVisibility = View.VISIBLE;
        int nextBtnVisibility = View.VISIBLE;

        if (itemIndex == 0){
            prevBtnVisibility = View.INVISIBLE;
        }
        if (itemIndex == shareClipExperience.getChildrenContents().size() -1){
            nextBtnVisibility = View.INVISIBLE;
        }
        prevClipButton.setVisibility(prevBtnVisibility);
        nextClipButton.setVisibility(nextBtnVisibility);
        if (bHideController)
            mediaController.hide();

    }

    // Method to share either text or URL.
    private void shareTextUrl() {
        Intent share = new Intent(android.content.Intent.ACTION_SEND);
        share.setType("text/plain");
        share.addFlags(Intent.FLAG_ACTIVITY_CLEAR_WHEN_TASK_RESET);

        // Add data to the intent, the receiving app will decide
        // what to do with it.
        share.putExtra(Intent.EXTRA_SUBJECT, "Title Of The Post");
        share.putExtra(Intent.EXTRA_TEXT, "https://www.codeofaninja.com");

        startActivity(Intent.createChooser(share, "Share link!"));
    }

    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        if (newConfig.orientation == Configuration.ORIENTATION_PORTRAIT){

        } else if (newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE){
            playerControl.pause();
        }
    }

    public void setMainMovieListener(IVideoViewActionListener mainMovieListener){

    }


}
