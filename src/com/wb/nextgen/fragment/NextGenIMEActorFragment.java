package com.wb.nextgen.fragment;

import android.os.Bundle;
import android.view.View;

import com.wb.nextgen.data.DemoJSONData;
import com.wb.nextgen.interfaces.NextGenPlaybackStatusListener;
import com.wb.nextgen.model.NextGenIMEEngine;
import com.wb.nextgen.data.DemoJSONData.ActorInfo;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by gzcheng on 1/26/16.
 */
public class NextGenIMEActorFragment extends NextGenActorListFragment implements NextGenPlaybackStatusListener{

    NextGenIMEEngine<List<DemoJSONData.ActorInfo>> actorIMEEngine = new NextGenIMEEngine<List<ActorInfo>>();

    List <NextGenIMEEngine<List<ActorInfo>>.NextGenIMEElement> actorElements = new ArrayList<NextGenIMEEngine<List<ActorInfo>>.NextGenIMEElement>();

    void handleIMEUpdate(long timecode, final List<ActorInfo> imeElement){

        if (getActivity() != null){
            getActivity().runOnUiThread(new Runnable() {

                @Override
                public void run() {
                    try {
                        if (getActorInfos().equals(imeElement) || (getActorInfos().size() == 0 && imeElement == null))
                            return;
                        if (imeElement != null)
                            setActorInfo(imeElement);
                        else
                            setActorInfo(new ArrayList<ActorInfo>());

                        listAdaptor.notifyDataSetChanged();
                    }catch (Exception ex){

                    }
                }
            });
        }
    }

    public void setActorInfo(List<ActorInfo> actorInfos){

    }

    public void playbackStatusUpdate(NextGenPlaybackStatusListener.NextGenPlaybackStatus playbackStatus, long timecode){
        handleIMEUpdate(timecode, actorIMEEngine.getCurrentIMEElement(timecode));
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        /*actors = new ArrayList<Actor>();
        actorElements.add(actorIMEEngine.createNextGenIMEElement(0, 9000, new ArrayList<Actor>(Arrays.asList(new Actor[]{Actor.RUSSELL_CROWE}))));

        actorElements.add(actorIMEEngine.createNextGenIMEElement(10000,20000, new ArrayList<Actor>(Arrays.asList(new Actor[]{Actor.RUSSELL_CROWE, Actor.AYELET_ZURER}))));
        actorElements.add(actorIMEEngine.createNextGenIMEElement(56000,68000, new ArrayList<Actor>(Arrays.asList(new Actor[]{Actor.KEVIN_COSTER}))));
        actorElements.add(actorIMEEngine.createNextGenIMEElement(69000,79000, new ArrayList<Actor>(Arrays.asList(new Actor[]{Actor.HENRY_CAVILL}))));
        actorElements.add(actorIMEEngine.createNextGenIMEElement(80000,82000, new ArrayList<Actor>(Arrays.asList(new Actor[]{Actor.AMY_ADAM}))));
        actorElements.add(actorIMEEngine.createNextGenIMEElement(83000,125000, new ArrayList<Actor>(Arrays.asList(new Actor[]{Actor.HENRY_CAVILL}))));
        actorElements.add(actorIMEEngine.createNextGenIMEElement(130000,137000, new ArrayList<Actor>(Arrays.asList(new Actor[]{Actor.HENRY_CAVILL, Actor.DIANE_LANE, Actor.MICHAEL_SHANNON}))));
        actorElements.add(actorIMEEngine.createNextGenIMEElement(155000,166000, new ArrayList<Actor>(Arrays.asList(new Actor[]{Actor.HENRY_CAVILL, Actor.AMY_ADAM}))));
        actorElements.add(actorIMEEngine.createNextGenIMEElement(167000,170000, new ArrayList<Actor>(Arrays.asList(new Actor[]{Actor.HENRY_CAVILL, Actor.MICHAEL_SHANNON}))));
        actorIMEEngine.setImeElements(actorElements);*/
    }
}
