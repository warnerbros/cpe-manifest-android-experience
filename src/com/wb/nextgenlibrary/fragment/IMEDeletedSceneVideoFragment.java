package com.wb.nextgenlibrary.fragment;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.wb.nextgenlibrary.R;
import com.wb.nextgenlibrary.data.MovieMetaData;

/**
 * Created by gzcheng on 5/23/17.
 */

public class IMEDeletedSceneVideoFragment extends ECVideoViewFragment {
	TextView titleTextView, descriptionTextView;

	MovieMetaData.ExperienceData deletedSceneExperience = null;

	@Override
	public int getContentViewId(){
		return R.layout.ime_deleted_scene_view;
	}

	@Override
	public void onViewCreated(final View view, Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);
		titleTextView = (TextView)view.findViewById(R.id.deleted_scene_title_txt);
		if (titleTextView != null && deletedSceneExperience != null)
			titleTextView.setText(deletedSceneExperience.title);

		descriptionTextView = (TextView)view.findViewById(R.id.deleted_scene_detail_txt);
		if (descriptionTextView != null && selectedAVItem != null)
			descriptionTextView.setText(selectedAVItem.getTitle());

	}

	public void setExperience(MovieMetaData.ExperienceData deletedSceneExperience){
		this.deletedSceneExperience = deletedSceneExperience;
		if (titleTextView != null)
			titleTextView.setText(this.deletedSceneExperience.title);
		//MovieMetaData.AudioVisualItem presentationDataItem = shareClipExperience.getChildrenContents().get(itemIndex).audioVisualItems.get(0);

		//setAudioVisualItem(presentationDataItem);
	}

	public void setAudioVisualItem(MovieMetaData.AudioVisualItem avItem){
		super.setAudioVisualItem(avItem);
		if (descriptionTextView != null)
			descriptionTextView.setText(avItem.getTitle());

	}
}
