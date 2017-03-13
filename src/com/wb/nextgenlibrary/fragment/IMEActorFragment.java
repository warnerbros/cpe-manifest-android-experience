package com.wb.nextgenlibrary.fragment;

import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.TextView;

import com.wb.nextgenlibrary.NextGenExperience;
import com.wb.nextgenlibrary.R;
import com.wb.nextgenlibrary.analytic.NGEAnalyticData;
import com.wb.nextgenlibrary.data.MovieMetaData;
import com.wb.nextgenlibrary.data.MovieMetaData.CastData;
import com.wb.nextgenlibrary.interfaces.NGEFragmentTransactionInterface;
import com.wb.nextgenlibrary.interfaces.NGEPlaybackStatusListener;
import com.wb.nextgenlibrary.model.IMEEngine;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by gzcheng on 1/26/16.
 */
public class IMEActorFragment extends ActorListFragment implements NGEPlaybackStatusListener {

    List<CastIMEEngine> castIMEEngines = new ArrayList<CastIMEEngine>();
    List<CastData> currentActiveActorList = new ArrayList<CastData>();

    static final CastData showMoreLessDummyData = new CastData(null, NextGenExperience.getClientLocale());

    boolean fullListEnabled = false;

    protected int getListItemViewId() {
        return R.layout.ime_actor_row;
    }

    @Override
    protected void onListItemClick(int index, CastData selectedObject){

        if (index >= getActorInfos().size() ){
            fullListEnabled = !fullListEnabled;
            //NGEAnalyticData.reportEvent(getActivity(), this, "Show Less/More", NGEAnalyticData.AnalyticAction.ACTION_CLICK, Boolean.toString(fullListEnabled));
        } else if (getActivity() instanceof NGEFragmentTransactionInterface){
            ActorDetailFragment target = new ActorDetailFragment();
            target.setLayoutId(R.layout.nge_ime_actor_detail_view);
            target.setShouldShowCloseBtn(true);
            target.setDetailObject(selectedObject);
            ((NGEFragmentTransactionInterface)getActivity()).transitMainFragment( target);
            ((NGEFragmentTransactionInterface)getActivity()).resetUI(false);
            NGEAnalyticData.reportEvent(getActivity(), this, NGEAnalyticData.AnalyticAction.ACTION_SELECT_TALENT, selectedObject.getId(), null);

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

    private static class CastIMEEngine extends IMEEngine<MovieMetaData.IMEElement<CastData>> {
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
                thisEngine.linearSearch(timecode);
                List<MovieMetaData.IMEElement<MovieMetaData.CastData>> thisData = thisEngine.getCurrentIMEItems();
                if (thisData.size() > 0) {
                    for (MovieMetaData.IMEElement<MovieMetaData.CastData> imeObject : thisEngine.getCurrentIMEItems())
                        newList.add(imeObject.imeObject);
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
        setCastIMEElementLists(NextGenExperience.getMovieMetaData().getCastIMEElements());

    }



    @Override
    protected int getListItemCount() {

        return getActorInfos().size() + 1;
    }

    @Override
    protected CastData getListItemAtPosition(int i) {
        if (i < getActorInfos().size())
            return getActorInfos().get(i);
        else
            return showMoreLessDummyData;
    }

    protected int getListItemViewId(int row){
        if (row < getActorInfos().size())
            return getListItemViewId();
        else {

            return R.layout.next_gen_actors_more_less_row;
        }
    }

    protected void fillListRowWithObjectInfo(View rowView, CastData item) {
        if (item == showMoreLessDummyData){
            TextView showTxt = (TextView)rowView.findViewById(R.id.show_more_less_text);
            if (showTxt != null)
                showTxt.setText(getResources().getString(fullListEnabled ? R.string.show_less_text : R.string.show_more_text));
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
