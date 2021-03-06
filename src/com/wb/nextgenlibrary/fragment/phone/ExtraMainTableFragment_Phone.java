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
        MovieMetaData.CastGroup castGroup = null;
        public ActorExperience(MovieMetaData.CastGroup castGroup){
            super(castGroup.getGroupTitle(), castGroup.groupJob,
                    null);
            this.castGroup = castGroup;
            type = MovieMetaData.ECGroupType.ACTORS;
        }

        public String getPosterImgUrl(){
            if (castGroup != null && castGroup.getCastsList().size() > 0) {
                if (castGroup.getCastsList().get(0).getBaselineCastData() != null)
                    return castGroup.getCastsList().get(0).getBaselineCastData().getFullImageUrl();
            }

            return null;
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

        if (NextGenExperience.getMovieMetaData().getCastGroups().size() > 0) {
            for (MovieMetaData.CastGroup group : NextGenExperience.getMovieMetaData().getCastGroups()){
                ecList.add(new ActorExperience(group));
            }
        }

        ecList.addAll(NextGenExperience.getMovieMetaData().getExtraECGroups());
        return ecList;
    }

    @Override
    protected void onListItemClick(View v, int position, long id) {
        MovieMetaData.ExperienceData selectedGroup = ecGroups.get(position);
        if (selectedGroup instanceof ActorExperience){
            Intent intent = new Intent(getActivity(), NGEActorsActivity_Phone.class);
            intent.putExtra(NGEActorsActivity_Phone.TALENT_LIST_MODE, ((ActorExperience)selectedGroup).castGroup.getGroupTitle());
            startActivity(intent);

        }else{
            super.onListItemClick(v, position, id);
        }
    }
}
