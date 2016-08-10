package com.wb.nextgen.fragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.squareup.picasso.Picasso;
import com.wb.nextgen.NextGenExperience;
import com.wb.nextgen.R;

import com.wb.nextgen.data.MovieMetaData;
import com.wb.nextgen.interfaces.NextGenFragmentTransactionInterface;
import com.wb.nextgen.interfaces.SensitiveFragmentInterface;
import com.wb.nextgen.network.BaselineApiDAO;
import com.wb.nextgen.util.PicassoTrustAll;

import com.wb.nextgen.data.MovieMetaData.CastData;
import com.wb.nextgen.util.concurrent.ResultListener;
import com.wb.nextgen.util.utils.StringHelper;
import com.wb.nextgen.widget.CircularClippedImageView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by gzcheng on 1/13/16.
 */
public class NextGenActorListFragment extends NextGenExtraLeftListFragment implements SensitiveFragmentInterface {


    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        List<String> castIds = new ArrayList<String>();
        for (CastData cast : NextGenExperience.getMovieMetaData().getActorsList()){
            if (!StringHelper.isEmpty(cast.getBaselineActorId()))
                castIds.add(cast.getBaselineActorId());
        }
        if (!NextGenExperience.getMovieMetaData().isHasCalledBaselineAPI()) {
            BaselineApiDAO.getCastActorsData(castIds, new ResultListener<HashMap<String, MovieMetaData.BaselineCastData>>() {
                @Override
                public void onResult(HashMap<String, MovieMetaData.BaselineCastData> resultMap) {
                    NextGenExperience.getMovieMetaData().setHasCalledBaselineAPI(true);
                    for (CastData cast : NextGenExperience.getMovieMetaData().getActorsList()) {
                        if (!StringHelper.isEmpty(cast.getBaselineActorId())) {
                            MovieMetaData.BaselineCastData baselineData = resultMap.get(cast.getBaselineActorId());
                            if (baselineData != null)
                                cast.baselineCastData = baselineData;
                        }
                    }
                    if (getActivity() != null) {
                        getActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                if (listAdaptor != null)
                                    listAdaptor.notifyDataSetChanged();
                            }
                        });
                    }
                }

                @Override
                public <E extends Exception> void onException(E e) {

                }
            });
        }
    }

    @Override
    protected int getPadding(){
        float density = NextGenExperience.getScreenDensity(getActivity());
        int spacing = (int)(5 *density);
        return 0;
    }


    public List<CastData> getActorInfos(){
        if (NextGenExperience.getMovieMetaData().getActorsList() != null)
            return NextGenExperience.getMovieMetaData().getActorsList();
        else
            return new ArrayList<CastData>();
    }


    @Override
    protected void onListItemClick(int index, Object selectedObject){

        if (getActivity() instanceof NextGenFragmentTransactionInterface){
            NextGenActorDetailFragment target = new NextGenActorDetailFragment();
            target.setbEnableActorGallery(true);
            target.setDetailObject((CastData) selectedObject);
            ((NextGenFragmentTransactionInterface)getActivity()).transitRightFragment(target);
            ((NextGenFragmentTransactionInterface)getActivity()).resetUI(false);

        }
        listAdaptor.notifyDataSetChanged();
    }

    protected int getListItemCount() {
        return getActorInfos().size();
    }

    protected Object getListItemAtPosition(int i) {
        return getActorInfos().get(i);
    }

    protected int getListItemViewId() {
        return R.layout.next_gen_actors_row;
    }

    protected int getStartupSelectedIndex(){
        return -1;
    }

    protected void fillListRowWithObjectInfo(View rowView, Object item) {


        final ImageView avatarImg = (ImageView) rowView.findViewById(R.id.next_gen_actor_avatar);
        TextView realNameTxt = (TextView) rowView.findViewById(R.id.next_gen_actor_real_name);
        TextView characterNameTxt = (TextView) rowView.findViewById(R.id.next_gen_actor_character_name);


        final CastData thisActor = (CastData) item;


        if(!thisActor.displayName.equals(realNameTxt.getText())) {
            realNameTxt.setText(thisActor.displayName.toUpperCase());
            characterNameTxt.setText(thisActor.charactorName);


        }


        if (thisActor.getBaselineCastData() != null){
            Picasso.with(getActivity()).load(thisActor.getBaselineCastData().getThumbnailImageUrl()).fit().centerCrop().into(avatarImg);
            // have to use picasso in this case because Glide won't do return any bitmap for centerCrop images.
        }




    }

    protected String getHeaderText(){
        return getResources().getString(R.string.actors);
    }


    public void notifyCurrentSensitiveFragment(Fragment fragment){
        if (!(fragment instanceof NextGenActorDetailFragment) ){
            resetSelectedItem();
            listAdaptor.notifyDataSetChanged();
            ((NextGenFragmentTransactionInterface)getActivity()).resetUI(true);
        }
    }
}
