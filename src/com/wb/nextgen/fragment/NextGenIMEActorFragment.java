package com.wb.nextgen.fragment;

import android.os.Bundle;
import android.view.View;

import com.wb.nextgen.interfaces.NextGenPlaybackStatusListener;
import com.wb.nextgen.model.NextGenIMEEngine;
import com.wb.nextgen.model.Actor;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by gzcheng on 1/26/16.
 */
public class NextGenIMEActorFragment extends NextGenActorListFragment implements NextGenPlaybackStatusListener{

    NextGenIMEEngine<List<Actor>> actorIMEEngine = new NextGenIMEEngine<List<Actor>>();

    List <NextGenIMEEngine<List<Actor>>.NextGenIMEElement> actorElements = new ArrayList<NextGenIMEEngine<List<Actor>>.NextGenIMEElement>();

    void handleIMEUpdate(long timecode, final List<Actor> imeElement){

        if (getActivity() != null){
            getActivity().runOnUiThread(new Runnable() {

                @Override
                public void run() {
                    try {
                        if (actors.equals(imeElement) || (actors.size() == 0 && imeElement == null))
                            return;
                        if (imeElement != null)
                            actors = imeElement;
                        else
                            actors = new ArrayList<Actor>();

                        listAdaptor.notifyDataSetChanged();
                    }catch (Exception ex){

                    }
                }
            });
        }
    }

    public void playbackStatusUpdate(NextGenPlaybackStatusListener.NextGenPlaybackStatus playbackStatus, long timecode){
        handleIMEUpdate(timecode, actorIMEEngine.getCurrentIMEElement(timecode));
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        actors = new ArrayList<Actor>();
        actorElements.add(actorIMEEngine.createNextGenIMEElement(0, 9000, new ArrayList<Actor>(Arrays.asList(new Actor[]{Actor.RUSSELL_CROWE}))));

        actorElements.add(actorIMEEngine.createNextGenIMEElement(10000,20000, new ArrayList<Actor>(Arrays.asList(new Actor[]{Actor.RUSSELL_CROWE, Actor.AYELET_ZURER}))));
        actorElements.add(actorIMEEngine.createNextGenIMEElement(56000,68000, new ArrayList<Actor>(Arrays.asList(new Actor[]{Actor.KEVIN_COSTER}))));
        actorElements.add(actorIMEEngine.createNextGenIMEElement(69000,79000, new ArrayList<Actor>(Arrays.asList(new Actor[]{Actor.HENRY_CAVILL}))));
        actorElements.add(actorIMEEngine.createNextGenIMEElement(80000,82000, new ArrayList<Actor>(Arrays.asList(new Actor[]{Actor.AMY_ADAM}))));
        actorElements.add(actorIMEEngine.createNextGenIMEElement(83000,125000, new ArrayList<Actor>(Arrays.asList(new Actor[]{Actor.HENRY_CAVILL}))));
        actorElements.add(actorIMEEngine.createNextGenIMEElement(130000,137000, new ArrayList<Actor>(Arrays.asList(new Actor[]{Actor.HENRY_CAVILL, Actor.DIANE_LANE, Actor.MICHAEL_SHANNON}))));
        actorElements.add(actorIMEEngine.createNextGenIMEElement(155000,166000, new ArrayList<Actor>(Arrays.asList(new Actor[]{Actor.HENRY_CAVILL, Actor.AMY_ADAM}))));
        actorElements.add(actorIMEEngine.createNextGenIMEElement(167000,170000, new ArrayList<Actor>(Arrays.asList(new Actor[]{Actor.HENRY_CAVILL, Actor.MICHAEL_SHANNON}))));
        actorIMEEngine.setImeElements(actorElements);
    }
}
