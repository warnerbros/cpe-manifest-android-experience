package com.wb.nextgen.fragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.wb.nextgen.R;

import com.wb.nextgen.util.PicassoTrustAll;

/**
 * Created by gzcheng on 1/13/16.
 */
public class NextGenActorDetailFragment extends Fragment{

    NextGenExtraDetialInterface detailObject;
    ImageView thumbnailImageView;
    TextView detailTextView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.next_gen_extra_detail_view, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        thumbnailImageView = (ImageView)view.findViewById(R.id.next_gen_detail_thumbnail_image);
        detailTextView = (TextView)view.findViewById(R.id.next_gen_detail_text);
        if (detailObject != null){
            PicassoTrustAll.loadImageIntoView(getActivity(), detailObject.getThumbnailImageUrl(), thumbnailImageView);
            detailTextView.setText(detailObject.getDetailText());
        }
    }

    public void setDetailObject(NextGenExtraDetialInterface object){
        detailObject = object;
    }

    public void reloadDetail(NextGenExtraDetialInterface object){
        setDetailObject(object);
        if (object != null){
            PicassoTrustAll.loadImageIntoView(getActivity(), object.getThumbnailImageUrl(), thumbnailImageView);
            detailTextView.setText(object.getDetailText());
        }
    }

    public NextGenExtraDetialInterface getDetailObject(){
        return detailObject;
    }

    public static interface NextGenExtraDetialInterface{


        public String getThumbnailImageUrl();
        public String getDetailText();

    }
}
