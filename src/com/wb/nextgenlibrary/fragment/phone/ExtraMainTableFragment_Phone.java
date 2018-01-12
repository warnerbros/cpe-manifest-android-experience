package com.wb.nextgenlibrary.fragment.phone;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.view.View;

import com.wb.nextgenlibrary.NextGenExperience;
import com.wb.nextgenlibrary.activity.NGEHideStatusBarActivity;
import com.wb.nextgenlibrary.activity.phone.NGEActorsActivity_Phone;
import com.wb.nextgenlibrary.data.MovieMetaData;
import com.wb.nextgenlibrary.fragment.ActorListFragment;
import com.wb.nextgenlibrary.fragment.ExtraMainTableFragment;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by stomata on 8/24/16.
 */
public class ExtraMainTableFragment_Phone extends ExtraMainTableFragment {

    class ActorExperience extends MovieMetaData.ExperienceData{
        ActorListFragment.TalentListMode mode = ActorListFragment.TalentListMode.ACTOR_MODE;

        public ActorExperience(){
            this (ActorListFragment.TalentListMode.ACTOR_MODE);
        }
        public ActorExperience(ActorListFragment.TalentListMode mode){
            super(
                    mode == ActorListFragment.TalentListMode.ACTOR_MODE ? NextGenExperience.getMovieMetaData().getActorGroupText() : NextGenExperience.getMovieMetaData().getCharacterGroupText(),
                    mode == ActorListFragment.TalentListMode.ACTOR_MODE ? NextGenExperience.getMovieMetaData().getActorGroupText() : NextGenExperience.getMovieMetaData().getCharacterGroupText(),
                    null);

            type = MovieMetaData.ECGroupType.ACTORS;
            this.mode = mode;
        }

        public String getPosterImgUrl(){
            if (mode == ActorListFragment.TalentListMode.ACTOR_MODE) {
                if (NextGenExperience.getMovieMetaData().getActorsList() != null && NextGenExperience.getMovieMetaData().getActorsList().size() > 0) {
                    if (NextGenExperience.getMovieMetaData().getActorsList().get(0).getBaselineCastData() != null)
                        return NextGenExperience.getMovieMetaData().getActorsList().get(0).getBaselineCastData().getFullImageUrl();
                }
            } else if (mode == ActorListFragment.TalentListMode.CHARACTER_MODE) {
                if (NextGenExperience.getMovieMetaData().getCharactersList() != null && NextGenExperience.getMovieMetaData().getCharactersList().size() > 0) {
                    if (NextGenExperience.getMovieMetaData().getCharactersList().get(0).getBaselineCastData() != null)
                        return NextGenExperience.getMovieMetaData().getCharactersList().get(0).getBaselineCastData().getFullImageUrl();
                }
            }
            return null;
        }

        public ActorListFragment.TalentListMode getTalentListMode(){
            return mode;
        }
    }

    @Override
    protected int getNumberOfColumns(){
        if (NGEHideStatusBarActivity.getCurrentScreenOrientation() == ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE)
            return 3;
        else
            return 2;
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        gridView.setNumColumns(getNumberOfColumns());
        listAdaptor.notifyDataSetChanged();
    }


    @Override
    protected List<MovieMetaData.ExperienceData> getSourceData(){
        List<MovieMetaData.ExperienceData> ecList = new ArrayList<MovieMetaData.ExperienceData>();
        if (NextGenExperience.getMovieMetaData().getActorsList() != null && NextGenExperience.getMovieMetaData().getActorsList().size() > 0){
            ecList.add(new ActorExperience(ActorListFragment.TalentListMode.ACTOR_MODE));
        }
        if (NextGenExperience.getMovieMetaData().getCharactersList() != null && NextGenExperience.getMovieMetaData().getCharactersList().size() > 0){
            ecList.add(new ActorExperience(ActorListFragment.TalentListMode.CHARACTER_MODE));
        }

        ecList.addAll(NextGenExperience.getMovieMetaData().getExtraECGroups());
        return ecList;
    }

    @Override
    protected void onListItemClick(View v, int position, long id) {
        MovieMetaData.ExperienceData selectedGroup = ecGroups.get(position);
        if (selectedGroup instanceof ActorExperience){
            Intent intent = new Intent(getActivity(), NGEActorsActivity_Phone.class);
            intent.putExtra(NGEActorsActivity_Phone.TALENT_LIST_MODE, ((ActorExperience)selectedGroup).getTalentListMode().toString());
            startActivity(intent);

        }else{
            super.onListItemClick(v, position, id);
        }
    }
}
