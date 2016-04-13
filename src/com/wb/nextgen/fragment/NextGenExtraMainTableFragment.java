package com.wb.nextgen.fragment;

import android.content.Intent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.wb.nextgen.NextGenApplication;
import com.wb.nextgen.activity.ECGalleryActivity;
import com.wb.nextgen.activity.ECVideoActivity;
import com.wb.nextgen.R;

import com.wb.nextgen.activity.TheTakeShopCategoryActivity;
import com.wb.nextgen.data.MovieMetaData;
import com.wb.nextgen.data.MovieMetaData.ExperienceData;
import com.wb.nextgen.network.TheTakeApiDAO;
import com.wb.nextgen.util.PicassoTrustAll;
import com.wb.nextgen.util.utils.F;

import java.util.List;

/**
 * Created by gzcheng on 1/13/16.
 */
public class NextGenExtraMainTableFragment extends NextGenGridViewFragment {


    public final static int GRID_SPACING_DP = 10;
    private static List<ExperienceData> ecGroups = NextGenApplication.getMovieMetaData().getExtraECGroups();

    protected void onListItmeClick(View v, int position, long id){
        ExperienceData selectedGroup = ecGroups.get(position);
        Intent intent = null;
        if (selectedGroup.getECGroupType() == MovieMetaData.ECGroupType.FEATURETTES){
            intent = new Intent(getActivity(), ECVideoActivity.class);
        }else if (selectedGroup.getECGroupType() == MovieMetaData.ECGroupType.EXTERNAL_APP){
            if (selectedGroup.getExternalApp() != null){
                if (selectedGroup.getExternalApp().externalApiName.equals(MovieMetaData.THE_TAKE_MANIFEST_IDENTIFIER)){
                    intent = new Intent(getActivity(), TheTakeShopCategoryActivity.class);

                }
            }
        }else if (selectedGroup.getECGroupType() == MovieMetaData.ECGroupType.GALLERY){
            intent = new Intent(getActivity(), ECGalleryActivity.class);
        }

        if (intent != null) {
            intent.setAction(android.content.Intent.ACTION_VIEW);
            intent.putExtra(F.ID, getListItemAtPosition(position).experienceId);
            startActivity(intent);
        }
    }

    protected int getNumberOfColumns(){
        return 2;
    }

    protected int getListItemCount() {
        return ecGroups.size();
    }

    protected ExperienceData getListItemAtPosition(int i) {
        return ecGroups.get(i);
    }

    protected int getListItemViewId() {
        return R.layout.next_gen_extra_right_item;
    }

    protected int getStartupSelectedIndex(){
        return -1;
    }

    protected void fillListRowWithObjectInfo(int position, View rowView, Object item, boolean isSelected) {


        ImageView thumbnailImg = (ImageView) rowView.findViewById(R.id.next_gen_extra_thumbnail);
        TextView titleTxt = (TextView) rowView.findViewById(R.id.next_gen_extra_title);

        ExperienceData thisExtra = (ExperienceData)item;
        if(!thisExtra.title.equals(titleTxt.getText())){
            titleTxt.setText(thisExtra.title);
            PicassoTrustAll.loadImageIntoView(getActivity(), thisExtra.getPosterImgUrl(), thumbnailImg);
        }
    }

    protected String getHeaderText(){
        return "";
    }

    protected int getHeaderChildenCount(int header){
        return getListItemCount();
    }

    protected int getHeaderCount(){
        return 0;
    }
}
