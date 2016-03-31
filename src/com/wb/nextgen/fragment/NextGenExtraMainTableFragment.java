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

import com.wb.nextgen.data.MovieMetaData;
import com.wb.nextgen.data.MovieMetaData.ExperienceData;
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
        Intent intent = new Intent(getActivity(), ECVideoActivity.class);
        if (selectedGroup.getECGroupType() == MovieMetaData.ECGroupType.GALLERY){
            intent = new Intent(getActivity(), ECGalleryActivity.class);
        }


        intent.setAction(android.content.Intent.ACTION_VIEW);
        intent.putExtra(F.ID, getListItemAtPosition(position).experienceId);
        //intent.setDataAndType(Uri.parse("http://cdn.theplatform.services/u/ContentServer/WarnerBros/Static/mos/NextGEN/feature/ManOfSteel_Clean.mp4"), "video/*");
        //intent.setDataAndType(Uri.parse("http://d1x310wzaeunai.cloudfront.net/video/man-of-steel-trailer3.mp4"), "video/*");
        //       intent.setDataAndType(Uri.parse("https://ia802304.us.archive.org/17/items/BigBuckBunny1280x720Stereo/big_buck_bunny_720_stereo.mp4"), "video/*");
        startActivity(intent);
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

        float density = NextGenApplication.getScreenDensity(getActivity());
        int spacing = (int) (GRID_SPACING_DP * density);
        int w = getActivity().getResources().getDisplayMetrics().widthPixels * 2 / 3 - spacing;

        ViewGroup.LayoutParams viewParams = this.getView().getLayoutParams();
        /*if (viewParams instanceof LinearLayout.LayoutParams){
            w = (int)(((float)getActivity().getResources().getDisplayMetrics().widthPixels) *((LinearLayout.LayoutParams)viewParams).weight) - spacing;
        }*/

        /*ViewGroup.LayoutParams layoutParams = (ViewGroup.LayoutParams) thumbnailImg.getLayoutParams();
        if (TabletUtils.isTablet()) {
            int width = (int) (w / 2) - spacing;
            int height = width /352 *198;
            layoutParams.width = width;
            layoutParams.height = height;


        } else {
            int width = w / 2 - spacing;
            layoutParams.width = width;
            layoutParams.height = (width - rowView.getPaddingRight()) / 2 * 3 + rowView.getPaddingBottom();
        }
        thumbnailImg.setLayoutParams(layoutParams);*/



        ExperienceData thisExtra = (ExperienceData)item;
        if(!thisExtra.title.equals(titleTxt.getText())){
            titleTxt.setText(thisExtra.title);
            PicassoTrustAll.loadImageIntoView(getActivity(), thisExtra.getPosterImgUrl(), thumbnailImg);
        }
        /*layoutParams.height = thumbnailImg.getHeight() + titleTxt.getHeight();
        rowView.setLayoutParams(layoutParams);*/
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
