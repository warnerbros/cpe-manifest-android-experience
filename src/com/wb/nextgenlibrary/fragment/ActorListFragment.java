package com.wb.nextgenlibrary.fragment;

import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.PagerAdapter;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.ToggleButton;

import com.squareup.picasso.Picasso;
import com.wb.nextgenlibrary.NextGenExperience;
import com.wb.nextgenlibrary.R;

import com.wb.nextgenlibrary.analytic.NGEAnalyticData;
import com.wb.nextgenlibrary.data.MovieMetaData;
import com.wb.nextgenlibrary.interfaces.NGEFragmentTransactionInterface;
import com.wb.nextgenlibrary.interfaces.SensitiveFragmentInterface;
import com.wb.nextgenlibrary.network.BaselineApiDAO;

import com.wb.nextgenlibrary.data.MovieMetaData.CastData;
import com.wb.nextgenlibrary.util.concurrent.ResultListener;
import com.wb.nextgenlibrary.util.utils.StringHelper;
import com.wb.nextgenlibrary.widget.FontFitTextView;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by gzcheng on 1/13/16.
 */
public class ActorListFragment extends ExtraLeftListFragment<CastData> implements SensitiveFragmentInterface {

    protected String listMode = "";
    private boolean bFixedMode = false;
    private TabLayout castGroupTabLayout = null;

    public void setForcedMode(String mode){
        listMode = mode;
        bFixedMode = true;
        if (castGroupTabLayout != null)
            castGroupTabLayout.setVisibility(View.GONE);
    }


    protected int getLayoutId(){
        return R.layout.next_gen_actor_list_view;
    }

    protected void onModeChanged(){

    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        castGroupTabLayout = (TabLayout) view.findViewById(R.id.cast_group_tab);
        if (castGroupTabLayout != null) {
            if (StringHelper.isEmpty(listMode) && NextGenExperience.getMovieMetaData().getCastGroups().size() > 0){
                listMode = NextGenExperience.getMovieMetaData().getCastGroups().get(0).getGroupTitle();
            }
            if (NextGenExperience.getMovieMetaData().hasMultipleCastMode() && !bFixedMode) {
                castGroupTabLayout.setVisibility(View.VISIBLE);
                for (MovieMetaData.CastGroup group : NextGenExperience.getMovieMetaData().getCastGroups()){
                    TabLayout.Tab tab = castGroupTabLayout.newTab().setText(group.getGroupTitle());

                    castGroupTabLayout.addTab(tab);
                }

                for (int i = 0; i<NextGenExperience.getMovieMetaData().getCastGroups().size(); i++) {
                    LinearLayout layout = ((LinearLayout) ((LinearLayout) castGroupTabLayout.getChildAt(0)).getChildAt(i));
                    LinearLayout.LayoutParams layoutParams = (LinearLayout.LayoutParams) layout.getLayoutParams();
                    layoutParams.weight = 0.0f; // e.g. 0.5f
                    //layoutParams.height = LinearLayout.LayoutParams.WRAP_CONTENT;
                    layoutParams.width = LinearLayout.LayoutParams.WRAP_CONTENT;
                    layout.setLayoutParams(layoutParams);
                }


                castGroupTabLayout.setOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
                    @Override
                    public void onTabSelected(TabLayout.Tab tab) {
                        listMode = tab.getText().toString();
                        onModeChanged();
                        listAdaptor.notifyDataSetChanged();
                        if (listAdaptor.selectedIndex >= 0){
                            onItemClick(null,listView, listAdaptor.selectedIndex, 0);
                        }
                    }

                    @Override
                    public void onTabUnselected(TabLayout.Tab tab) {

                    }

                    @Override
                    public void onTabReselected(TabLayout.Tab tab) {

                    }
                });

            } else
                castGroupTabLayout.setVisibility(View.GONE);
        }



        BaselineApiDAO.getCastActorsImages(NextGenExperience.getMovieMetaData().getAllCastsList(), new ResultListener<Boolean>() {
            @Override
            public void onResult(Boolean result) {

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

    @Override
    protected int getPadding(){
        float density = NextGenExperience.getScreenDensity(getActivity());
        int spacing = (int)(5 *density);
        return 0;
    }


    public List<CastData> getActorInfos(){
        MovieMetaData.CastGroup group =  NextGenExperience.getMovieMetaData().getCastGroupByTitle(listMode);
        if (group != null)
            return group.getCastsList();
        else
            return new ArrayList<CastData>();
    }


    @Override
    protected void onListItemClick(int index, CastData selectedObject){

        if (getActivity() instanceof NGEFragmentTransactionInterface){
            ActorDetailFragment target = new ActorDetailFragment();
            target.setDetailObject(selectedObject);
            ((NGEFragmentTransactionInterface)getActivity()).transitRightFragment(target);
            ((NGEFragmentTransactionInterface)getActivity()).resetUI(false);

        }
        listAdaptor.notifyDataSetChanged();
        NGEAnalyticData.reportEvent(getActivity(), this, NGEAnalyticData.AnalyticAction.ACTION_SELECT_TALENT, selectedObject.getId(), null);
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

        FontFitTextView initialText = (FontFitTextView)rowView.findViewById(R.id.next_gen_actor_initial);
        initialText.setText(thisActor.displayName.substring(0,1).toUpperCase());


        if (thisActor.getBaselineCastData() != null && !StringHelper.isEmpty(thisActor.getBaselineCastData().getThumbnailImageUrl())){
            Picasso.with(getActivity()).load(thisActor.getBaselineCastData().getThumbnailImageUrl()).fit().centerCrop().into(avatarImg);
            initialText.setVisibility(View.GONE);
            // have to use picasso in this case because Glide won't do return any bitmap for centerCrop images.
        }else
            initialText.setVisibility(View.VISIBLE);


    }

    protected String getHeaderText(){
        MovieMetaData.CastGroup group =  NextGenExperience.getMovieMetaData().getCastGroupByTitle(listMode);
        if (group != null)
            return group.getGroupTitle();
        else
            return "Actors & Crews";
    }


    public void notifyCurrentSensitiveFragment(Fragment fragment){
        if (!(fragment instanceof ActorDetailFragment) ){
            resetSelectedItem();
            listAdaptor.notifyDataSetChanged();
            ((NGEFragmentTransactionInterface)getActivity()).resetUI(true);
        }
    }
}
