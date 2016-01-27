package com.wb.nextgen.fragment;

import android.os.Bundle;
import android.view.View;

import com.wb.nextgen.model.Actor;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by gzcheng on 1/26/16.
 */
public class NextGenIMEActorFragment extends AbstractIMEFragment<List<Actor>> {
    void handleIMEUpdate(long timecode, final List<Actor> imeElement){

        if (getActivity() != null){
            getActivity().runOnUiThread(new Runnable() {

                @Override
                public void run() {
                    if (actors.equals(imeElement) || (actors.size() == 0 && imeElement == null))
                        return;
                    if (imeElement != null)
                        actors = imeElement;
                    else
                        actors = new ArrayList<Actor>();

                    listAdaptor.notifyDataSetChanged();

                }
            });
        }
    }


    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        actors = new ArrayList<Actor>();
        imeElements.add(new NextGenIMEElement(0, 9000, new ArrayList<Actor>(Arrays.asList(new Actor[]{Actor.RUSSELL_CROWE}))));

        imeElements.add(new NextGenIMEElement(10000,20000, new ArrayList<Actor>(Arrays.asList(new Actor[]{Actor.RUSSELL_CROWE, Actor.AYELET_ZURER}))));
        imeElements.add(new NextGenIMEElement(50000,68000, new ArrayList<Actor>(Arrays.asList(new Actor[]{Actor.KEVIN_COSTER}))));
        imeElements.add(new NextGenIMEElement(69000,79000, new ArrayList<Actor>(Arrays.asList(new Actor[]{Actor.HENRY_CAVILL}))));
        imeElements.add(new NextGenIMEElement(80000,82000, new ArrayList<Actor>(Arrays.asList(new Actor[]{Actor.AMY_ADAM}))));
        imeElements.add(new NextGenIMEElement(83000,125000, new ArrayList<Actor>(Arrays.asList(new Actor[]{Actor.HENRY_CAVILL}))));
        imeElements.add(new NextGenIMEElement(130000,137000, new ArrayList<Actor>(Arrays.asList(new Actor[]{Actor.HENRY_CAVILL, Actor.DIANE_LANE, Actor.MICHAEL_SHANNON}))));
        imeElements.add(new NextGenIMEElement(155000,166000, new ArrayList<Actor>(Arrays.asList(new Actor[]{Actor.HENRY_CAVILL, Actor.AMY_ADAM}))));
        imeElements.add(new NextGenIMEElement(167000,170000, new ArrayList<Actor>(Arrays.asList(new Actor[]{Actor.HENRY_CAVILL, Actor.MICHAEL_SHANNON}))));
    }
}
