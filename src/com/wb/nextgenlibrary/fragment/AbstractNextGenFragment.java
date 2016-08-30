package com.wb.nextgenlibrary.fragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.InflateException;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;

import com.wb.nextgenlibrary.R;
import com.wb.nextgenlibrary.util.utils.F;
import com.wb.nextgenlibrary.util.utils.NextGenLogger;

/**
 * Created by gzcheng on 4/11/16.
 */
public abstract class AbstractNextGenFragment extends Fragment {
    View view;
    ImageButton closeBtn;
    boolean shouldShowCloseBtn = false;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        if (view != null) {
            ViewGroup parent = (ViewGroup) view.getParent();
            if (parent != null)
                parent.removeView(view);
        }
        try {
            view = inflater.inflate(getContentViewId(), container, false);
        } catch (InflateException e) {
            NextGenLogger.e(F.TAG, e.getLocalizedMessage());
        }
        return view;

    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        closeBtn = (ImageButton) view.findViewById(R.id.close_button);
        if (closeBtn != null) {
            if (shouldShowCloseBtn) {
                closeBtn.setVisibility(View.VISIBLE);

            }else {
                closeBtn.setVisibility(View.GONE);
            }
            closeBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (getActivity() != null)
                        getActivity().onBackPressed();
                }
            });
        }
    }

    public void setShouldShowCloseBtn(boolean bShow){
        shouldShowCloseBtn = bShow;
    }

    abstract int getContentViewId();

}
