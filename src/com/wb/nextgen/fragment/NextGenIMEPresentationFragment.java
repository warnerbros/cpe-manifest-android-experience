package com.wb.nextgen.fragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.wb.nextgen.R;
import com.wb.nextgen.data.MovieMetaData;
import com.wb.nextgen.interfaces.NextGenPlaybackStatusListener;
import com.wb.nextgen.model.NextGenIMEEngine;
import com.wb.nextgen.model.Presentation;
import com.wb.nextgen.util.PicassoTrustAll;
import com.wb.nextgen.util.utils.StringHelper;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by gzcheng on 1/27/16.
 */
public class NextGenIMEPresentationFragment extends Fragment implements NextGenPlaybackStatusListener {

    private ImageView thumbnailImg;
    private TextView titleTxt;
    //private Presentation

    NextGenIMEEngine<MovieMetaData.IMEElement<MovieMetaData.ExperienceData>> presentationIMEEngine = null;//new NextGenIMEEngine<MovieMetaData.ExperienceData>();

    MovieMetaData.IMEElementsGroup<MovieMetaData.ExperienceData> imeGroup;

    //List <NextGenIMEEngine<Presentation>.NextGenIMEElement> presentationElements = new ArrayList<NextGenIMEEngine<Presentation>.NextGenIMEElement>();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.next_gen_extra_right_item, container, false);
    }

    public void setIMEGroup(MovieMetaData.IMEElementsGroup imeGroup){
        this.imeGroup = imeGroup;
        presentationIMEEngine.setImeElements(this.imeGroup.getIMEElementesList());
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        thumbnailImg = (ImageView) view.findViewById(R.id.next_gen_extra_thumbnail);
        titleTxt = (TextView) view.findViewById(R.id.next_gen_extra_title);



        //listView.setselect
    }


    public void playbackStatusUpdate(NextGenPlaybackStatusListener.NextGenPlaybackStatus playbackStatus, long timecode){
        boolean bHasUpdated = presentationIMEEngine.computeCurrentIMEElement(timecode);
        if (bHasUpdated)
            handleIMEUpdate(timecode, presentationIMEEngine.getCurrentIMEElement().imeObject);
    }

    void handleIMEUpdate(long timecode, final MovieMetaData.ExperienceData imeElement){
        if (imeElement == null)
            return;
        else{
            getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if (imeElement != null && !StringHelper.isEmpty(imeElement.getPosterImgUrl())) {
                        if (thumbnailImg != null) {
                            PicassoTrustAll.loadImageIntoView(getActivity(), imeElement.getPosterImgUrl(), thumbnailImg);
                        }

                        if (titleTxt != null) {
                            titleTxt.setText(imeElement.title);
                        }
                    }
                }
            });
        }
    }
}
