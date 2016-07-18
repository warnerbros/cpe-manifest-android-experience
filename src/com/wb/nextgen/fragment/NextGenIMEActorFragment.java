package com.wb.nextgen.fragment;

import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.TextView;

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
    TextView showMoreLessTextView = null;

    boolean fullListEnabled = false;

    protected int getListItemViewId() {
        return R.layout.ime_actor_row;
    }

    @Override
    protected void onListItemClick(int index, Object selectedObject){

        if (index >= getActorInfos().size() ){
            fullListEnabled = !fullListEnabled;
            // do this trick here
        } else if (getActivity() instanceof NextGenFragmentTransactionInterface){
            NextGenActorDetailFragment target = new NextGenActorDetailFragment();

            target.setDetailObject((MovieMetaData.CastData) selectedObject);
            ((NextGenFragmentTransactionInterface)getActivity()).transitMainFragment( target);
            ((NextGenFragmentTransactionInterface)getActivity()).resetUI(false);

        }

        if (getActivity() != null) {
            getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    listAdaptor.notifyDataSetChanged();
                }
            });
        }
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
        if (fullListEnabled ){
            return super.getActorInfos();
        }else
            return currentActiveActorList;

    }
    @Override
    public void playbackStatusUpdate(NextGenPlaybackStatus playbackStatus, long timecode){

        final List<MovieMetaData.CastData> newList = new ArrayList<MovieMetaData.CastData>();
        if (timecode != -1){
            for(CastIMEEngine thisEngine: castIMEEngines){
                thisEngine.computeCurrentIMEElement(timecode);
                MovieMetaData.IMEElement<MovieMetaData.CastData> thisData = thisEngine.getCurrentIMEElement();
                if (thisData != null){
                    newList.add(thisData.imeObject);
                }
            }
            if (!fullListEnabled && getActivity() != null) {
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        currentActiveActorList = newList;
                        listAdaptor.notifyDataSetChanged();
                    }
                });
            }
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



    @Override
    protected int getListItemCount() {

        return getActorInfos().size() + 1;
    }

    @Override
    protected Object getListItemAtPosition(int i) {
        if (i < getActorInfos().size())
            return getActorInfos().get(i);
        else
            return getResources().getString(fullListEnabled ? R.string.show_less_text : R.string.show_more_text);
    }

    protected int getListItemViewId(int row){
        if (row < getActorInfos().size())
            return getListItemViewId();
        else {

            return R.layout.next_gen_actors_more_less_row;
        }
    }

    protected void fillListRowWithObjectInfo(View rowView, Object item) {
        if (item instanceof String){
            TextView showTxt = (TextView)rowView.findViewById(R.id.show_more_less_text);
            if (showTxt != null)
                showTxt.setText((String)item);
        }else {
            super.fillListRowWithObjectInfo(rowView, item);
        }
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View v, int position, long id) {
        //listAdaptor.selectedIndex = position - listView.getHeaderViewsCount();
        onListItemClick(position, listAdaptor.getItem(position));
        listAdaptor.notifyDataSetChanged();
    }
}
