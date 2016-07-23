package com.wb.nextgen.fragment;

import android.content.Intent;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.wb.nextgen.NextGenApplication;
import com.wb.nextgen.R;
import com.wb.nextgen.data.MovieMetaData;
import com.wb.nextgen.util.NextGenUtils;
import com.wb.nextgen.util.PicassoTrustAll;
import com.wb.nextgen.util.utils.StringHelper;
import com.wb.nextgen.widget.ECMediaController;


/**
 * Created by gzcheng on 3/31/16.
 */
public class ShareClipFragment extends ECVideoViewFragment implements View.OnClickListener{
    TextView shareClipTitleTextView;
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
            //Glide.with(getActivity()).load(NextGenUtils.getPacakageImageUrl(R.drawable.back_logo)).into(prevClipButton);
        }
        nextClipButton = (ImageButton) view.findViewById(R.id.next_clip_btn);
        if (nextClipButton != null){
            nextClipButton.setOnClickListener(this);
            //Glide.with(getActivity()).load(NextGenUtils.getPacakageImageUrl(R.drawable.back_logo)).into(nextClipButton);
        }
        shareClipTitleTextView = (TextView) view.findViewById(R.id.share_clip_title_txt);
        if (shareClipTitleTextView != null && shareClipExperience != null){
            shareClipTitleTextView.setText(shareClipExperience.title);
        }
        updateUI();

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.share_clip_button:
                videoView.pause();
                String imageUrl = selectedAVItem.getPosterImgUrl();
                String videoUrl = selectedAVItem.videoUrl;
                Intent share = new Intent(Intent.ACTION_SEND);
                share.setType("text/plain");
                share.addFlags(Intent.FLAG_ACTIVITY_CLEAR_WHEN_TASK_RESET);
                share.putExtra(Intent.EXTRA_SUBJECT, "Next Gen Share");
                share.putExtra(Intent.EXTRA_TEXT, videoUrl);

                startActivity(Intent.createChooser(share, ""));
                break;
            case R.id.prev_clip_btn:
                if (itemIndex >= 0){
                    setShouldAutoPlay(false);
                    setExperienceAndIndex(shareClipExperience, itemIndex - 1);
                    updateUI();
                }
                break;
            case R.id.next_clip_btn:
                if (itemIndex < shareClipExperience.getChildrenContents().size()){
                    setShouldAutoPlay(false);
                    setExperienceAndIndex(shareClipExperience, itemIndex + 1);
                    updateUI();
                }
                break;

        }
    }

    public void setExperienceAndIndex(MovieMetaData.ExperienceData shareClipExperience, int itemIndex){
        this.shareClipExperience = shareClipExperience;
        this.itemIndex = itemIndex;
        MovieMetaData.AudioVisualItem presentationDataItem = shareClipExperience.getChildrenContents().get(itemIndex).audioVisualItems.get(0);

        setAudioVisualItem(presentationDataItem);
    }

    public void updateUI(){
        int prevBtnVisibility = View.VISIBLE;
        int nextBtnVisibility = View.VISIBLE;
        if (itemIndex == 0){
            prevBtnVisibility = View.INVISIBLE;
        }else if (itemIndex == shareClipExperience.getChildrenContents().size() -1){
            nextBtnVisibility = View.INVISIBLE;
        }
        prevClipButton.setVisibility(prevBtnVisibility);
        nextClipButton.setVisibility(nextBtnVisibility);
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


}
