package com.wb.nextgenlibrary.fragment;

import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.MediaController;
import android.widget.TextView;

import com.wb.nextgenlibrary.R;
import com.wb.nextgenlibrary.activity.AbstractECView;
import com.wb.nextgenlibrary.data.MovieMetaData;
import com.wb.nextgenlibrary.data.MovieMetaData.ExperienceData;
import com.wb.nextgenlibrary.util.utils.NextGenGlide;
import com.wb.nextgenlibrary.widget.SelectedOverlayImageView;

/**
 * Created by gzcheng on 3/7/16.
 */
public class ECViewLeftListFragment extends ExtraLeftListFragment<ExperienceData> {
    MovieMetaData.ExperienceData listECGroupData;
    AbstractECView ecViewActivity;
    MediaController.MediaPlayerControl playerControl = null;

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        init();
        super.onViewCreated(view, savedInstanceState);


    }
    public void onAttach(Context context) {
        init();
        super.onAttach(context);

    }

    void init(){
        if(ecViewActivity == null && getActivity() instanceof AbstractECView) {
            ecViewActivity = (AbstractECView) getActivity();
            listECGroupData = ecViewActivity.getECGroupData();
        }
    }

    @Override
    public void onListItemClick(int index, ExperienceData selectedObject){
        ecViewActivity.onLeftListItemSelected(selectedObject);
    }

    protected int getListItemCount(){
        return listECGroupData.getChildrenContents().size();
    }

    protected ExperienceData getListItemAtPosition(int i){
        return listECGroupData.getChildrenContents().get(i);
    }

    protected int getListItemViewId(){
        if (getActivity() != null && getActivity() instanceof AbstractECView){
            return ((AbstractECView)getActivity()).getListItemViewLayoutId();
        }
        return R.layout.next_gen_ec_list_item;
    }

    protected void fillListRowWithObjectInfo(View rowView, ExperienceData thisEC){

        SelectedOverlayImageView imageView = (SelectedOverlayImageView)rowView.findViewById(R.id.ec_list_image);
        if (imageView != null){
            //ViewGroup.LayoutParams imageLayoutParams = imageView.getLayoutParams();
            //imageView.setTag(thisEC.title);
            NextGenGlide.load(getActivity(), thisEC.getPosterImgUrl()).fitCenter().into(imageView);
            //PicassoTrustAll.loadImageIntoView(getActivity(), thisEC.getPosterImgUrl(), imageView);
        }

        TextView ecNameText = (TextView)rowView.findViewById(R.id.ec_list_name_text);
        if (ecNameText != null){
            ecNameText.setText(thisEC.title);
        }

        TextView ecDurationText = (TextView)rowView.findViewById(R.id.ec_duration_text);
        if (ecDurationText != null){
            if (rowView.isActivated() && thisEC.audioVisualItems != null && thisEC.audioVisualItems.size() > 0){
                if (playerControl != null && playerControl.isPlaying()) {
                    ecDurationText.setText(getResources().getString(R.string.playing));
                    thisEC.setWatched();
                } else {
                    ecDurationText.setText(thisEC.getDuration());
                }
            }else if (thisEC.getIsWatched()){
                ecDurationText.setText(getResources().getString(R.string.watched));
            }else if (thisEC.getDuration() != null)
                ecDurationText.setText(thisEC.getDuration());
        }

    }

    protected String getHeaderText(){
        return listECGroupData.title;

    }

    public void scrollToTop(){
        if (listView != null)
            listView.smoothScrollToPosition(0);
    }

    protected int getStartupSelectedIndex(){
        return 0;
    }

    public void selectNextItem(){
        if (listAdaptor != null){
            int currentSelection = listAdaptor.selectedIndex;
            if (listAdaptor.getCount() -1  > currentSelection) {
                onItemClick(null, listView, currentSelection + 1, 0);
                listView.smoothScrollToPositionFromTop(currentSelection + 1, 0);
            }
        }
    }

    public boolean hasReachedLastItem(){
        if (listAdaptor != null){
            return listAdaptor.selectedIndex < listAdaptor.getCount() - 1 ;
        }
        return true;
    }

    public int getSelectedIndex(){
        if (listAdaptor != null)
            return listAdaptor.selectedIndex;
        else
            return -1;
    }

    public void setObserveVideoPlayerCotrol(MediaController.MediaPlayerControl playerCotrol){
        this.playerControl = playerCotrol;
    }

    public void onDestroy(){
        super.onDestroy();
        playerControl = null;
        ecViewActivity = null;
        listECGroupData = null;
    }
}