package com.wb.nextgen.fragment;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.wb.nextgen.NextGenApplication;
import com.wb.nextgen.R;
import com.wb.nextgen.data.MovieMetaData;

import java.util.List;

/**
 * Created by gzcheng on 3/28/16.
 */
public class IMEElementsGridFragment extends NextGenGridViewFragment {

    List<MovieMetaData.IMEElementsGroup> imeGroups;
    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        imeGroups = NextGenApplication.getMovieMetaData().getImeElementGroups();
    }

    protected void onListItmeClick(View v, int position, long id){

    }

    protected int getNumberOfColumns(){
        return 2;
    }

    protected int getListItemCount(){
        return imeGroups.size();
    }

    protected Object getListItemAtPosition(int i){
        return imeGroups.get(i);
    }

    protected int getListItemViewId(){
        return R.layout.ime_grid_item_view;
    }

    protected void fillListRowWithObjectInfo(View rowView, Object item, boolean isSelected){
        TextView titleText= (TextView)rowView.findViewById(R.id.ime_title);
        TextView subText1= (TextView)rowView.findViewById(R.id.ime_desc_text1);
        TextView subText2= (TextView)rowView.findViewById(R.id.ime_desc_text2);
        ImageView poster = (ImageView)rowView.findViewById(R.id.ime_image_poster);

        MovieMetaData.IMEElementsGroup group = (MovieMetaData.IMEElementsGroup)item;
        //titleText.setText(group.);

    }

    protected String getHeaderText(){
        return "";
    }

    protected int getHeaderChildenCount(int header){
        if (header == 0)
            return imeGroups.size();
        else
            return 0;
    }

    protected int getHeaderCount(){
        return 0;
    }

    protected int getStartupSelectedIndex(){
        return -1;
    }
}
