package com.wb.nextgen.fragment.phone;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.support.annotation.CallSuper;
import android.view.View;

import com.wb.nextgen.NextGenExperience;
import com.wb.nextgen.R;
import com.wb.nextgen.activity.NextGenActionBarFragmentActivity;
import com.wb.nextgen.activity.NextGenHideStatusBarActivity;
import com.wb.nextgen.activity.phone.NextGenActorsActivity_Phone;
import com.wb.nextgen.data.MovieMetaData;
import com.wb.nextgen.fragment.NextGenExtraMainTableFragment;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by stomata on 8/24/16.
 */
public class NextGenExtraMainTableFragment_Phone extends NextGenExtraMainTableFragment {

    class ActorExperience extends MovieMetaData.ExperienceData{

        public ActorExperience(){
            super("ACTORS", "ACTORS", null);
            type = MovieMetaData.ECGroupType.ACTORS;
        }

        public String getPosterImgUrl(){
            if (NextGenExperience.getMovieMetaData().getActorsList() != null && NextGenExperience.getMovieMetaData().getActorsList().size() > 0){
                if (NextGenExperience.getMovieMetaData().getActorsList().get(0).getBaselineCastData() != null)
                    return NextGenExperience.getMovieMetaData().getActorsList().get(0).getBaselineCastData().getFullImageUrl();
            }
            return null;
        }
    }

    @Override
    protected int getNumberOfColumns(){
        if (NextGenHideStatusBarActivity.getCurrentScreenOrientation() == ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE)
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
            ecList.add(new ActorExperience());
        }
        ecList.addAll(NextGenExperience.getMovieMetaData().getExtraECGroups());
        return ecList;
    }

    @Override
    protected void onListItemClick(View v, int position, long id) {
        MovieMetaData.ExperienceData selectedGroup = ecGroups.get(position);
        if (selectedGroup instanceof ActorExperience){
            Intent intent = new Intent(getActivity(), NextGenActorsActivity_Phone.class);
            startActivity(intent);

        }else{
            super.onListItemClick(v, position, id);
        }
    }
}
