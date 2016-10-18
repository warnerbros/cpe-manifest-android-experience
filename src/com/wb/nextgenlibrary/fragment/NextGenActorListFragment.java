package com.wb.nextgenlibrary.fragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;
import com.wb.nextgenlibrary.NextGenExperience;
import com.wb.nextgenlibrary.R;

import com.wb.nextgenlibrary.analytic.NextGenAnalyticData;
import com.wb.nextgenlibrary.interfaces.NextGenFragmentTransactionInterface;
import com.wb.nextgenlibrary.interfaces.SensitiveFragmentInterface;
import com.wb.nextgenlibrary.network.BaselineApiDAO;

import com.wb.nextgenlibrary.data.MovieMetaData.CastData;
import com.wb.nextgenlibrary.util.concurrent.ResultListener;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by gzcheng on 1/13/16.
 */
public class NextGenActorListFragment extends NextGenExtraLeftListFragment<CastData> implements SensitiveFragmentInterface {


    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        if (!NextGenExperience.getMovieMetaData().isHasCalledBaselineAPI()) {

            BaselineApiDAO.getCastActorsData(NextGenExperience.getMovieMetaData().getActorsList(), new ResultListener<Boolean>() {
                @Override
                public void onResult(Boolean result) {
                    NextGenExperience.getMovieMetaData().setHasCalledBaselineAPI(result);

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
    protected void onListItemClick(int index, CastData selectedObject){

        if (getActivity() instanceof NextGenFragmentTransactionInterface){
            NextGenActorDetailFragment target = new NextGenActorDetailFragment();
            target.setbEnableActorGallery(true);
            target.setDetailObject(selectedObject);
            ((NextGenFragmentTransactionInterface)getActivity()).transitRightFragment(target);
            ((NextGenFragmentTransactionInterface)getActivity()).resetUI(false);

        }
        listAdaptor.notifyDataSetChanged();
        NextGenAnalyticData.reportEvent(getActivity(), this, "Actor", NextGenAnalyticData.AnalyticAction.ACTION_CLICK, selectedObject.getTitle());
    }

    protected int getListItemCount() {
        return getActorInfos().size();
    }

    protected CastData getListItemAtPosition(int i) {
        return getActorInfos().get(i);
    }

    protected int getListItemViewId() {
        return R.layout.next_gen_actors_row;
    }

    protected int getStartupSelectedIndex(){
        return -1;
    }

    protected void fillListRowWithObjectInfo(View rowView, final CastData thisActor) {


        final ImageView avatarImg = (ImageView) rowView.findViewById(R.id.next_gen_actor_avatar);
        TextView realNameTxt = (TextView) rowView.findViewById(R.id.next_gen_actor_real_name);
        TextView characterNameTxt = (TextView) rowView.findViewById(R.id.next_gen_actor_character_name);

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
