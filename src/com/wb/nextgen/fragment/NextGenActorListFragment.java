package com.wb.nextgen.fragment;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.wb.nextgen.R;

import com.wb.nextgen.NextGenExtraActivity;
import com.wb.nextgen.interfaces.NextGenFragmentTransactionInterface;
import com.wb.nextgen.model.Actor;
import com.wb.nextgen.util.PicassoTrustAll;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by gzcheng on 1/13/16.
 */
public class NextGenActorListFragment extends NextGenExtraLeftListFragment {

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        actors.add(Actor.HENRY_CAVILL);
        actors.add(Actor.AMY_ADAM);
        actors.add(Actor.MICHAEL_SHANNON);
        actors.add(Actor.RUSSELL_CROWE);
        actors.add(Actor.DIANE_LANE);
        actors.add(Actor.KEVIN_COSTER);
        actors.add(Actor.ANTJE_TRAUE);

        //listView.setselect
    }



    protected static List<Actor> actors = new ArrayList<Actor>();


    private int selectedIndex = -1;

    protected void onListItmeClick(View v, final int position, long id){
        listView.setSelection(position);
        v.setSelected(true);
        selectedIndex = position;
        if (getActivity() instanceof NextGenFragmentTransactionInterface){
            NextGenActorDetailFragment target = new NextGenActorDetailFragment();
            NextGenActorDetailFragment.NextGenExtraDetialInterface obj = new NextGenActorDetailFragment.NextGenExtraDetialInterface() {
                @Override
                public String getThumbnailImageUrl() {
                    return ((Actor)getListItemAtPosition(position)).avatarURL;
                }

                @Override
                public String getDetailText() {
                    return ((Actor)getListItemAtPosition(position)).biograph;
                }
            };
            target.setDetailObject(obj);
            ((NextGenFragmentTransactionInterface)getActivity()).transitRightFragment(target);
        }
        listAdaptor.notifyDataSetChanged();
    }

    protected int getNumberOfColumns(){
        return 1;
    }

    protected int getListItemCount() {
        return actors.size();
    }

    protected Object getListItemAtPosition(int i) {
        return actors.get(i);
    }

    protected int getListItemViewId() {
        return R.layout.next_gen_actors_row;
    }

    protected void fillListRowWithObjectInfo(View rowView, Object item) {


        ImageView avatarImg = (ImageView) rowView.findViewById(R.id.next_gen_actor_avatar);
        TextView realNameTxt = (TextView) rowView.findViewById(R.id.next_gen_actor_real_name);
        TextView characterNameTxt = (TextView) rowView.findViewById(R.id.next_gen_actor_character_name);

        Actor thisActor = (Actor) item;
        if(!thisActor.realName.equals(realNameTxt.getText())) {
            realNameTxt.setText(thisActor.realName);
            characterNameTxt.setText(thisActor.characterName);
            PicassoTrustAll.loadImageIntoView(getActivity(), thisActor.avatarURL, avatarImg);
        }
        if (selectedIndex != -1 && selectedIndex < getListItemCount() && item.equals(getListItemAtPosition(selectedIndex)))
            rowView.setSelected(true);
        else
            rowView.setSelected(false);

    }

    protected String getHeaderText(){
        return "Actors";
    }

    protected int getHeaderChildenCount(int header){
        return getListItemCount();
    }

    protected int getHeaderCount(){
        return 1;
    }
}
