package com.wb.nextgen.fragment;

import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;

import com.wb.nextgen.NextGenApplication;
import com.wb.nextgen.R;
import com.wb.nextgen.data.DemoJSONData;
import com.wb.nextgen.data.MovieMetaData;
import com.wb.nextgen.interfaces.NextGenFragmentTransactionInterface;
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

    List<CastIMEEngine> castIMEEngines = new ArrayList<CastIMEEngine>();
    List<MovieMetaData.CastData> currentActiveActorList = new ArrayList<MovieMetaData.CastData>();

    boolean fullListEnabled = false;

    protected int getListItemViewId() {
        return R.layout.ime_actor_row;
    }

    @Override
    protected void onListItemClick(Object selectedObject){

        if (getActivity() instanceof NextGenFragmentTransactionInterface){
            NextGenActorDetailFragment target = new NextGenActorDetailFragment();

            target.setDetailObject((MovieMetaData.CastData) selectedObject);
            ((NextGenFragmentTransactionInterface)getActivity()).transitMainFragment( target);
            ((NextGenFragmentTransactionInterface)getActivity()).resetUI(false);

        }
        listAdaptor.notifyDataSetChanged();
    }

    private static class CastIMEEngine extends NextGenIMEEngine<MovieMetaData.IMEElement<MovieMetaData.CastData>>{
        public CastIMEEngine(List<MovieMetaData.IMEElement<MovieMetaData.CastData>> elements){
            imeElements = elements;
        }

        public int compareCurrentTimeWithItemAtIndex(long timecode, int index){
            return imeElements.get(index).compareTimeCode(timecode);
        }
    }

    @Override
    public List<MovieMetaData.CastData> getActorInfos(){
        if (fullListEnabled){
            currentActiveActorList = super.getActorInfos();
        }
        return currentActiveActorList;

    }

    @Override
    public void playbackStatusUpdate(NextGenPlaybackStatus playbackStatus, long timecode){
        if (fullListEnabled)
            return;
        currentActiveActorList.clear();
        if (timecode != -1){
            for(CastIMEEngine thisEngine: castIMEEngines){
                thisEngine.computeCurrentIMEElement(timecode);
                MovieMetaData.IMEElement<MovieMetaData.CastData> thisData = thisEngine.getCurrentIMEElement();
                if (thisData != null){
                    currentActiveActorList.add(thisData.imeObject);
                }
            }
            listAdaptor.notifyDataSetChanged();
        }

    }

    private void setCastIMEElementLists(List<List<MovieMetaData.IMEElement<MovieMetaData.CastData>>> castIMEElementLists){
        if (castIMEElementLists != null && castIMEElementLists.size() > 0){
            for(List<MovieMetaData.IMEElement<MovieMetaData.CastData>> imeList : castIMEElementLists){
                castIMEEngines.add(new CastIMEEngine(imeList));
            }
        }
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        setCastIMEElementLists(NextGenApplication.getMovieMetaData().getCastIMEElements());

    }

}
