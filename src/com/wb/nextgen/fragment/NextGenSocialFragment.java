package com.wb.nextgen.fragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTabHost;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TabHost;

import com.wb.nextgen.R;
import com.wb.nextgen.interfaces.NextGenPlaybackStatusListener;

/**
 * Created by gzcheng on 2/17/16.
 */
public class NextGenSocialFragment extends Fragment implements NextGenPlaybackStatusListener{

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.next_gen_social, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);


    }

    public void playbackStatusUpdate(NextGenPlaybackStatusListener.NextGenPlaybackStatus playbackStatus, long timecode){
        //handleIMEUpdate(timecode, presentationIMEEngine.getCurrentIMEElement(timecode));
    }


}
