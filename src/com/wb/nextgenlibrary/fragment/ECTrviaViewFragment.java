package com.wb.nextgenlibrary.fragment;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.wb.nextgenlibrary.R;
import com.wb.nextgenlibrary.data.MovieMetaData;
import com.wb.nextgenlibrary.model.AVGalleryIMEEngine.IMECombineItem;
import com.wb.nextgenlibrary.util.PicassoTrustAll;
import com.wb.nextgenlibrary.util.utils.NextGenGlide;
import com.wb.nextgenlibrary.widget.FontFitTextView;


/**
 * Created by gzcheng on 3/31/16.
 */
public class ECTrviaViewFragment extends AbstractNextGenFragment {
	protected ImageView posterImageView;

    protected TextView triviaTitle;
    protected FontFitTextView triviaContent;


    IMECombineItem selectedCombineItem = null;
    MovieMetaData.TriviaItem selectedTriviaItem = null;
    String title = null;

    public int getContentViewId(){
        return R.layout.ec_trivia_view;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        posterImageView = (ImageView) view.findViewById(R.id.ec_trivia_image);
        triviaTitle = (TextView) view.findViewById(R.id.ec_title_name);
        triviaContent = (FontFitTextView) view.findViewById(R.id.ec_content_name);
        if (selectedCombineItem != null && title != null){
            setTextItem(title, selectedCombineItem);
        } else if (selectedTriviaItem != null && title != null){
            setTriviaItem(title, selectedTriviaItem);
        }

    }
    public String getReportContentName(){
        return title;
    }

    public void setTriviaItem(String textTitle, MovieMetaData.TriviaItem triviaItem){
        if (triviaItem != null) {
            title = textTitle;
            selectedTriviaItem = triviaItem;
            if (triviaContent != null) {
                triviaContent.setText(selectedTriviaItem.getTitle());
            }
            if (triviaTitle != null) {
                triviaTitle.setText(textTitle);
            }
            if (posterImageView != null) {
                NextGenGlide.load(getActivity(), triviaItem.pictureItem.fullImage.url).into(posterImageView);
                //PicassoTrustAll.getInstance(getActivity()).load(triviaItem.pictureItem.fullImage.url).into(posterImageView);
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
            if (posterImageView != null) {
                NextGenGlide.load(getActivity(), triviaItem.getPictureItem().fullImage.url).into(posterImageView);
                //PicassoTrustAll.getInstance(getActivity()).load(triviaItem.getPictureItem().fullImage.url).into(posterImageView);
            }

        }
    }

}
