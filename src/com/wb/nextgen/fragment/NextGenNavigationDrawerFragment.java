package com.wb.nextgen.fragment;

import android.animation.ValueAnimator;
import android.app.Fragment;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.animation.ScaleAnimation;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.wb.nextgen.R;
import com.wb.nextgen.data.DemoData;
import com.wb.nextgen.data.DemoData.AudSubLang;

import java.util.List;


public class NextGenNavigationDrawerFragment extends Fragment implements View.OnClickListener{

	RelativeLayout audioRow;
	RelativeLayout subtitleRow;
	TextView audioSelectionText;
	TextView subtitleSelectionText;
	ListView audioList;
	ListView subtitleList;
	int audioListHeight = 0, subtitleListHeight = 0;

	AudioSubtitleListAdaptor audioSelectionAdaptor, subtitleSelectionAdaptor;


	@Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
		return inflater.inflate(R.layout.navigation_drawer, container, false);
    }

	@Override
	public void onViewCreated(View view, Bundle savedInstanceState){
		super.onViewCreated(view, savedInstanceState);

		audioRow = (RelativeLayout)view.findViewById(R.id.drawer_audio_row);

		if (audioRow != null){
			audioRow.setOnClickListener(this);
		}

		subtitleRow = (RelativeLayout)view.findViewById(R.id.drawer_subtitle_row);

		if (subtitleRow != null){
			subtitleRow.setOnClickListener(this);
		}

		audioSelectionText = (TextView)view.findViewById(R.id.drawer_audio_selection);
		subtitleSelectionText = (TextView)view.findViewById(R.id.drawer_subtitle_selection);

		audioList = (ListView)view.findViewById(R.id.drawer_audio_list);
		audioSelectionAdaptor = new AudioSubtitleListAdaptor(getActivity(), R.layout.drawer_aud_sub_row, DemoData.audioList, -1);
		audioList.setAdapter(audioSelectionAdaptor);
		audioList.setOnItemClickListener(audioSelectionAdaptor);

		subtitleList = (ListView)view.findViewById(R.id.drawer_subtitle_list);
		subtitleSelectionAdaptor = new AudioSubtitleListAdaptor(getActivity(), R.layout.drawer_aud_sub_row, DemoData.subtitleList, 0);
		subtitleList.setAdapter(subtitleSelectionAdaptor);
		subtitleList.setOnItemClickListener(subtitleSelectionAdaptor);

		final ViewTreeObserver vto = audioList.getViewTreeObserver();
		vto.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
			@Override
			public void onGlobalLayout() {

				audioListHeight = audioList.getMeasuredHeight(); 			// Measure the expanded height of audio List
				audioList.setVisibility(View.GONE);							// then set it to gone

				subtitleListHeight = subtitleList.getMeasuredHeight();		// Measure the expanded Height of subtitle list
				subtitleList.setVisibility(View.GONE);

				audioList.getViewTreeObserver().removeOnGlobalLayoutListener(this);
			}
		});


		resetCurrentStateDrawerList();

		getActivity().setTitle("<");
		resetDrawer();
		
		
	}
	
	@Override
	public void onActivityResult(int requestCode, int resultCode, final Intent data) {

	}

	@Override
	public void onClick(View v){
		if (v.getId() == R.id.drawer_audio_row) {
			final ScaleAnimation growAnim = new ScaleAnimation(1.0f, 1.15f, 1.0f, 1.15f);
			final ScaleAnimation shrinkAnim = new ScaleAnimation(1.15f, 1.0f, 1.15f, 1.0f);

			growAnim.setDuration(2000);
			shrinkAnim.setDuration(2000);

			if (audioList.getVisibility() == View.GONE || audioList.getHeight() == 0) {

				audioList.setVisibility(View.VISIBLE);

				new ExpandShrinkRunnable(audioList, true, 0, audioListHeight).run();
			} else {
				new ExpandShrinkRunnable(audioList, false, 0, audioListHeight).run();
				AudSubLang lang = audioSelectionAdaptor.getSelectedLanguage();
				if (lang != null)
					audioSelectionText.setText(lang.displayName);
			}
		} else if (v.getId() == R.id.drawer_subtitle_row) {
			if (subtitleList.getVisibility() == View.GONE || subtitleList.getHeight() == 0) {
				subtitleList.setVisibility(View.VISIBLE );
				new ExpandShrinkRunnable(subtitleList, true, 0, subtitleListHeight).run();
			}else {
				new ExpandShrinkRunnable(subtitleList, false, 0, subtitleListHeight).run();
				AudSubLang lang = subtitleSelectionAdaptor.getSelectedLanguage();
				if (lang != null){
					subtitleSelectionText.setText(lang.displayName);
				}

			}

		}
	}



	private class ExpandShrinkRunnable implements Runnable {

		View targetView;
		boolean isExpand;
		int shrinkedHeight;
		int expandedHeight;

		ExpandShrinkRunnable(View v, boolean isExpand, int minHeight, int maxHeight){
			targetView = v;
			shrinkedHeight = minHeight;
			expandedHeight = maxHeight;
			this.isExpand = isExpand;
		}

		@Override
		public void run() {

			final View detail = targetView;
			ValueAnimator va = isExpand ? ValueAnimator.ofInt(shrinkedHeight, expandedHeight) : ValueAnimator.ofInt(expandedHeight, shrinkedHeight);
			va.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
				public void onAnimationUpdate(ValueAnimator animation) {
					Integer value = (Integer) animation.getAnimatedValue();
					detail.getLayoutParams().height = value.intValue();
					detail.requestLayout();
				}
			});
			va.setDuration(1000);
			va.start();
		}
	}


	private class AudioSubtitleListAdaptor extends ArrayAdapter<AudSubLang> implements OnItemClickListener, View.OnClickListener{

		int selectedIndex = -1;

		public AudioSubtitleListAdaptor(Context context, int resource,List<AudSubLang> audSubList, int initialSelection) {
			super(context, resource, audSubList);
			selectedIndex = initialSelection;
		}

		public View getView(int position, View convertView, ViewGroup parent) {

			if (convertView == null  ) {

				convertView = LayoutInflater.from(getActivity()).inflate(R.layout.drawer_aud_sub_row, parent, false);

			} else {

			}

			AudSubLang thisLang = (AudSubLang)getItem(position);

			RadioButton audSubButton = (RadioButton) convertView.findViewById(R.id.aud_sub_radio_button);
			audSubButton.setText(thisLang.displayName);
			audSubButton.setTag(new Integer(position));
			audSubButton.setOnClickListener(this);
			if (position == selectedIndex)
				audSubButton.setChecked(true);
			else
				audSubButton.setChecked(false);

			return convertView;
		}

		@Override
		public void onClick(View v){
			int position = (Integer)v.getTag();
			if (selectedIndex != position) {
				selectedIndex = position;
				notifyDataSetChanged();
			}
		}

		@Override
		public void onItemClick(AdapterView<?> parent, View view, int position, long id){
			if (selectedIndex != position) {
				selectedIndex = position;
				notifyDataSetChanged();
			}
		}

		public AudSubLang getSelectedLanguage(){
			if(selectedIndex != -1){
				return getItem(selectedIndex);
			}else
				return null;
		}
	}
	

	public void resetDrawer(){
		resetCurrentStateDrawerList();

	}
	
	private void resetCurrentStateDrawerList(){
	}



	public void onDestroyView(){
		super.onDestroyView();
		getActivity().setTitle(">");
	}
}
