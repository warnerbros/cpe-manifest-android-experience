package com.wb.nextgen.fragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.InflateException;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.wb.nextgen.R;

/**
 * Created by gzcheng on 4/11/16.
 */
public abstract class AbstractNextGenFragment extends Fragment {
    View view;
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
        /* map is already there, just return view as it is */
        }
        return view;

    }

    abstract int getContentViewId();

}
