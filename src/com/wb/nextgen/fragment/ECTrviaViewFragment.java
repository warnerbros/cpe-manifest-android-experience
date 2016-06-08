package com.wb.nextgen.fragment;

import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.wb.nextgen.R;
import com.wb.nextgen.data.MovieMetaData;
import com.wb.nextgen.model.AVGalleryIMEEngine.IMECombineItem;
import com.wb.nextgen.util.PicassoTrustAll;
import com.wb.nextgen.util.utils.StringHelper;
import com.wb.nextgen.videoview.ObservableVideoView;
import com.wb.nextgen.widget.ECMediaController;


/**
 * Created by gzcheng on 3/31/16.
 */
public class ECTrviaViewFragment extends Fragment {
    protected ImageView posterImageView;

    protected TextView triviaTitle;
    protected TextView triviaContent;


    IMECombineItem selectedTriviaItem = null;
    String title = null;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.ec_trivia_view, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        posterImageView = (ImageView) view.findViewById(R.id.ec_trivia_image);
        triviaTitle = (TextView) view.findViewById(R.id.ec_title_name);
        triviaContent = (TextView) view.findViewById(R.id.ec_content_name);
        if (selectedTriviaItem != null && title != null){
            setTextItem(title, selectedTriviaItem);
        }

    }


    public void setTextItem(String textTitle, IMECombineItem triviaItem){
        if (triviaItem != null) {
            title = textTitle;
            selectedTriviaItem = triviaItem;
            if (triviaContent != null) {
                triviaContent.setText(selectedTriviaItem.getTextItem().getTitle());
            }
            if (triviaTitle != null) {
                triviaTitle.setText(textTitle);
            }
            if (posterImageView != null) {
                PicassoTrustAll.getInstance(getActivity()).load(triviaItem.getPictureItem().fullImage.url).into(posterImageView);
            }

        }
    }

}
