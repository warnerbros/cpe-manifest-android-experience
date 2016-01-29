package com.wb.nextgen.fragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.wb.nextgen.R;
import com.wb.nextgen.interfaces.NextGenPlaybackStatusListener;
import com.wb.nextgen.model.NextGenIMEEngine;
import com.wb.nextgen.model.Presentation;
import com.wb.nextgen.util.PicassoTrustAll;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by gzcheng on 1/27/16.
 */
public class NextGenIMEPresentationFragment extends Fragment implements NextGenPlaybackStatusListener {

    private ImageView thumbnailImg;
    private TextView titleTxt;
    //private Presentation

    NextGenIMEEngine<Presentation> presentationIMEEngine = new NextGenIMEEngine<Presentation>();

    List <NextGenIMEEngine<Presentation>.NextGenIMEElement> presentationElements = new ArrayList<NextGenIMEEngine<Presentation>.NextGenIMEElement>();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.next_gen_extra_right_item, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        thumbnailImg = (ImageView) view.findViewById(R.id.next_gen_extra_thumbnail);
        titleTxt = (TextView) view.findViewById(R.id.next_gen_extra_title);

        presentationElements.add(presentationIMEEngine.createNextGenIMEElement(1000, 11000, Presentation.PRESENTATION_A));
        presentationElements.add(presentationIMEEngine.createNextGenIMEElement(12000, 22000, Presentation.PRESENTATION_B));
        presentationElements.add(presentationIMEEngine.createNextGenIMEElement(23000, 33000, Presentation.PRESENTATION_C));
        presentationElements.add(presentationIMEEngine.createNextGenIMEElement(34000, 44000, Presentation.PRESENTATION_A));
        presentationElements.add(presentationIMEEngine.createNextGenIMEElement(45000, 55000, Presentation.PRESENTATION_B));
        presentationElements.add(presentationIMEEngine.createNextGenIMEElement(56000, 66000, Presentation.PRESENTATION_C));
        presentationElements.add(presentationIMEEngine.createNextGenIMEElement(67000, 77000, Presentation.PRESENTATION_A));
        presentationElements.add(presentationIMEEngine.createNextGenIMEElement(78000, 88000, Presentation.PRESENTATION_B));
        presentationElements.add(presentationIMEEngine.createNextGenIMEElement(89000, 99000, Presentation.PRESENTATION_C));

        presentationIMEEngine.setImeElements(presentationElements);
        //listView.setselect
    }


    public void playbackStatusUpdate(NextGenPlaybackStatusListener.NextGenPlaybackStatus playbackStatus, long timecode){
        handleIMEUpdate(timecode, presentationIMEEngine.getCurrentIMEElement(timecode));
    }

    void handleIMEUpdate(long timecode, final Presentation imeElement){
        if (imeElement == null)
            return;
        else{
            getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if (thumbnailImg != null){
                        PicassoTrustAll.loadImageIntoView(getActivity(), imeElement.imageURL, thumbnailImg);
                    }

                    if (titleTxt != null){
                        titleTxt.setText(imeElement.title);
                    }
                }
            });
        }
    }
}
