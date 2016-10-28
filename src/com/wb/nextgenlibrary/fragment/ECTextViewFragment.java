package com.wb.nextgenlibrary.fragment;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.wb.nextgenlibrary.R;
import com.wb.nextgenlibrary.data.MovieMetaData;
import com.wb.nextgenlibrary.model.AVGalleryIMEEngine.IMECombineItem;
import com.wb.nextgenlibrary.util.PicassoTrustAll;
import com.wb.nextgenlibrary.widget.FixedAspectRatioFrameLayout;
import com.wb.nextgenlibrary.widget.FontFitTextView;


public class ECTextViewFragment extends AbstractNextGenFragment {

	protected TextView triviaTitle;
	protected FontFitTextView triviaContent;
	protected FixedAspectRatioFrameLayout imageContainer;

	IMECombineItem selectedCombineItem = null;
	MovieMetaData.TextItem selectedTextItem = null;
	String title = null;

	public int getContentViewId(){
		return R.layout.ec_trivia_view;
	}

	@Override
	public void onViewCreated(View view, Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);
		imageContainer = (FixedAspectRatioFrameLayout) view.findViewById(R.id.image_container);
		if (imageContainer != null)
			imageContainer.setVisibility(View.GONE);
		triviaTitle = (TextView) view.findViewById(R.id.ec_title_name);
		triviaContent = (FontFitTextView) view.findViewById(R.id.ec_content_name);
		if (selectedCombineItem != null && title != null){
			setTextItem(title, selectedCombineItem);
		} else if (selectedTextItem != null && title != null){
			setTextItem(title, selectedTextItem);
		}

	}
	public String getReportContentName(){
		return title;
	}

	public void setTextItem(String textTitle, MovieMetaData.TextItem textItem){
		if (textItem != null) {
			title = textTitle;
			selectedTextItem = textItem;
			if (triviaContent != null) {
				triviaContent.setText(selectedTextItem.getTitle());
			}
			if (triviaTitle != null) {
				triviaTitle.setText(textTitle);
			}
		}
	}

	public void setTextItem(String textTitle, IMECombineItem triviaItem){
		if (triviaItem != null) {
			title = textTitle;
			selectedCombineItem = triviaItem;
			if (triviaContent != null) {
				triviaContent.setText(selectedCombineItem.getTextItem().getTitle());
			}
			if (triviaTitle != null) {
				triviaTitle.setText(textTitle);
			}

		}
	}

}
