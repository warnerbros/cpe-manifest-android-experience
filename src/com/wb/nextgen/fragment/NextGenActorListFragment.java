package com.wb.nextgen.fragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.wb.nextgen.R;

import com.wb.nextgen.NextGenExtraActivity;
import com.wb.nextgen.data.DemoData;
import com.wb.nextgen.data.DemoJSONData;
import com.wb.nextgen.data.DemoJSONData.ActorInfo;
import com.wb.nextgen.interfaces.NextGenFragmentTransactionInterface;
import com.wb.nextgen.interfaces.SensitiveFragmentInterface;
import com.wb.nextgen.util.PicassoTrustAll;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by gzcheng on 1/13/16.
 */
public class NextGenActorListFragment extends NextGenExtraLeftListFragment implements SensitiveFragmentInterface {


    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        //listView.setselect
    }

    public List<ActorInfo> getActorInfos(){
        if (DemoData.getDemoJSONData().movieMetaData.actorsInfo != null)
            return DemoData.getDemoJSONData().movieMetaData.actorsInfo;
        else
            return new ArrayList<ActorInfo>();
    }


    protected void onListItmeClick(View v, final int position, long id){
        listView.setSelection(position);
        v.setSelected(true);
        if (getActivity() instanceof NextGenFragmentTransactionInterface){
            NextGenActorDetailFragment target = new NextGenActorDetailFragment();
            NextGenActorDetailFragment.NextGenExtraDetialInterface obj = new NextGenActorDetailFragment.NextGenExtraDetialInterface() {
                @Override
                public String getThumbnailImageUrl() {
                    return ((ActorInfo)getListItemAtPosition(position)).getThumbnailUri();
                }

                @Override
                public String getDetailText() {
                    return ((ActorInfo)getListItemAtPosition(position)).biography;
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

    protected void fillListRowWithObjectInfo(View rowView, Object item, boolean isSelected) {


        ImageView avatarImg = (ImageView) rowView.findViewById(R.id.next_gen_actor_avatar);
        TextView realNameTxt = (TextView) rowView.findViewById(R.id.next_gen_actor_real_name);
        TextView characterNameTxt = (TextView) rowView.findViewById(R.id.next_gen_actor_character_name);
        ImageView selectedMaskImg = (ImageView) rowView.findViewById(R.id.actor_selected_mask);

        ActorInfo thisActor = (ActorInfo) item;
        if(!thisActor.realName.equals(realNameTxt.getText())) {
            realNameTxt.setText(thisActor.realName);
            characterNameTxt.setText(thisActor.character);
            PicassoTrustAll.loadImageIntoView(getActivity(), thisActor.getThumbnailUri(), avatarImg);
        }
        /*
        if (selectedIndex != -1 && selectedIndex < getListItemCount() && item.equals(getListItemAtPosition(selectedIndex)))
            rowView.setSelected(true);
        else
            rowView.setSelected(false);
        */
        if (selectedMaskImg != null){
            selectedMaskImg.setVisibility(isSelected ? View.VISIBLE : View.INVISIBLE);
        }

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

    public void notifyCurrentSensitiveFragment(Fragment fragment){
        if (!(fragment instanceof NextGenActorDetailFragment) ){
            resetSelectedItem();
            listAdaptor.notifyDataSetChanged();
        }
    }
}
