package com.wb.nextgenlibrary.activity;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.wb.nextgenlibrary.NextGenExperience;
import com.wb.nextgenlibrary.R;
import com.wb.nextgenlibrary.data.MovieMetaData;
import com.wb.nextgenlibrary.util.TabletUtils;
import com.wb.nextgenlibrary.util.utils.F;
import com.wb.nextgenlibrary.util.utils.NextGenGlide;

/**
 * Created by gzcheng on 11/28/16.
 */

public class ECInteractiveActivity extends AbstractECView implements AdapterView.OnItemClickListener{


	protected GridView gridView;
	protected NextGenInteractiveGridViewAdapter listAdaptor;
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		float density = NextGenExperience.getScreenDensity(this);
		int spacing = (int)(10 *density);
		gridView = (GridView)findViewById(R.id.next_gen_grid);
		if (gridView != null){
			gridView.setNumColumns(TabletUtils.isTablet() ? 3:2);
			gridView.setHorizontalSpacing(spacing);
			gridView.setVerticalSpacing(spacing);
			gridView.setPadding(spacing, 0, spacing, spacing);
			//gridView.setHeadersIgnorePadding(true);
			listAdaptor = new NextGenInteractiveGridViewAdapter();
			gridView.setAdapter(listAdaptor);
			gridView.setOnItemClickListener(this);
		}
	}
	public void onStart() {
		super.onStart();
		if (!TabletUtils.isTablet())
			setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position, long id){
		Intent intent = new Intent(this, WebViewActivity.class);
		intent.putExtra(F.URL, ecGroupData.getChildrenContents().get(position).interactiveItems.get(0).assetLocation);
		intent.setAction(Intent.ACTION_VIEW);
		intent.putExtra(F.ID, ecGroupData.getChildrenContents().get(position).experienceId);
		//intent.putExtra(F.TITLE, selectedGroup.title);
		startActivity(intent);
	}


	public void onLeftListItemSelected(MovieMetaData.ExperienceData ecContentData){

	}

	public int getContentViewId(){
		return R.layout.next_gen_grid_view;
	}
	public int getListItemViewLayoutId(){
		return -1;
	}

	public void onFullScreenChange(boolean bFullscreen){}

	public class NextGenInteractiveGridViewAdapter extends BaseAdapter {

		//protected OutOfMovieActivity activity;


		private TextView headerTextView;

		NextGenInteractiveGridViewAdapter() {

		}

		public View getView(int position, View convertView, ViewGroup parent) {
			if (position >= getCount() || position < 0){
				return null;
			}

			if (convertView == null) {


				LayoutInflater inflater = getLayoutInflater();
				convertView = inflater.inflate(R.layout.next_gen_extra_right_item, parent, false);
				//setupNewContentView(convertView);

			} else {

			}
			ImageView thumbnailImg = (ImageView) convertView.findViewById(R.id.next_gen_extra_thumbnail);
			TextView titleTxt = (TextView) convertView.findViewById(R.id.next_gen_extra_title);

			MovieMetaData.ExperienceData thisExtra = getItem(position);
			if(!thisExtra.title.equals(titleTxt.getText())){
				titleTxt.setText(thisExtra.title.toUpperCase());
				NextGenGlide.load(ECInteractiveActivity.this, thisExtra.getPosterImgUrl()).fitCenter().into(thumbnailImg);
				//PicassoTrustAll.loadImageIntoView(getActivity(), thisExtra.getPosterImgUrl(), thumbnailImg);
			}


			return convertView;
		}

		public int getCount() {
			return ecGroupData.getChildrenContents().size();
		}

		public MovieMetaData.ExperienceData getItem(int position) {
			return  ecGroupData.getChildrenContents().get(position);
		}

		public long getItemId(int position) {
			return position;
		}

	}


}
