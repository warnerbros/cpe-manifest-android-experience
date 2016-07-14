package com.wb.nextgen.fragment;

import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.wb.nextgen.R;
import com.wb.nextgen.activity.AbstractECView;
import com.wb.nextgen.data.MovieMetaData;
import com.wb.nextgen.util.PicassoTrustAll;
import com.wb.nextgen.widget.SelectedOverlayImageView;

/**
 * Created by gzcheng on 3/7/16.
 */
public class ECViewLeftListFragment extends NextGenExtraLeftListFragment{
    MovieMetaData.ExperienceData listECGroupData;
    AbstractECView ecViewActivity;

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        init();
        super.onViewCreated(view, savedInstanceState);


    }
    public void onAttach(Context context) {
        init();
        super.onAttach(context);

    }

    void init(){
        if(ecViewActivity == null && getActivity() instanceof AbstractECView) {
            ecViewActivity = (AbstractECView) getActivity();
            listECGroupData = ecViewActivity.getECGroupData();
        }
    }

    @Override
    public void onListItemClick(int index, Object selectedObject){
        ecViewActivity.onLeftListItemSelected((MovieMetaData.ExperienceData) selectedObject);
    }

    protected int getListItemCount(){
        return listECGroupData.getChildrenContents().size();
    }

    protected Object getListItemAtPosition(int i){
        return listECGroupData.getChildrenContents().get(i);
    }

    protected int getListItemViewId(){
        if (getActivity() != null && getActivity() instanceof AbstractECView){
            return ((AbstractECView)getActivity()).getListItemViewLayoutId();
        }
        return R.layout.next_gen_ec_list_item;
    }

    protected void fillListRowWithObjectInfo(View rowView, Object item){
        MovieMetaData.ExperienceData thisEC = (MovieMetaData.ExperienceData) item;

        SelectedOverlayImageView imageView = (SelectedOverlayImageView)rowView.findViewById(R.id.ec_list_image);
        if (imageView != null){
            //ViewGroup.LayoutParams imageLayoutParams = imageView.getLayoutParams();
            imageView.setTag(thisEC.title);
            PicassoTrustAll.loadImageIntoView(getActivity(), thisEC.getPosterImgUrl(), imageView);
        }

        TextView ecNameText = (TextView)rowView.findViewById(R.id.ec_list_name_text);
        if (ecNameText != null){
            ecNameText.setText(thisEC.title);
        }

        TextView ecDurationText = (TextView)rowView.findViewById(R.id.ec_duration_text);
        if (ecDurationText != null && thisEC.getDuration() != null){
            ecDurationText.setText(thisEC.getDuration());
        }
        /*
        ImageView mask = (ImageView)rowView.findViewById(R.id.ec_inactive_mask);
        if (mask != null){
            if (mask.isActivated()){
                mask.setVisibility(View.INVISIBLE);
            }else
                mask.setVisibility(View.VISIBLE);
        }*/
    }

    protected String getHeaderText(){
        return listECGroupData.title;

    }

    public void scrollToTop(){
        if (listView != null)
            listView.smoothScrollToPosition(0);
    }

    protected int getStartupSelectedIndex(){
        return 0;
    }

    public void selectNextItem(){
        if (listAdaptor != null){
            int currentSelection = listAdaptor.selectedIndex;
            if (listAdaptor.getCount() -1  > currentSelection) {
                onItemClick(null, listView, currentSelection + 1, 0);
            }
        }
    }

    public boolean hasReachedLastItem(){
        if (listAdaptor != null){
            return listAdaptor.selectedIndex < listAdaptor.getCount() - 1 ;
        }
        return true;
    }
}