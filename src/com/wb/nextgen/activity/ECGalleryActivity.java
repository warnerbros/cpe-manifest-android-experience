package com.wb.nextgen.activity;

import android.os.Bundle;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.widget.ListView;

import com.wb.nextgen.R;
import com.wb.nextgen.data.DemoData;

/**
 * Created by gzcheng on 3/7/16.
 */
public class ECGalleryActivity extends AbstractECView {

    private ViewPager galleryViewPager;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //galleryListView = ()

    }

    public int getContentViewId(){
        return R.layout.next_gen_gallery_view;
    }

    class GalleryPagerAdapter extends PagerAdapter {
        public int getCount(){
            return 0;
        }

        public boolean isViewFromObject(View view, Object object){
            return true;
        }
    }
    @Override
    public void onLeftListItemSelected(DemoData.ECContentData ec){
        if (ec != null){

        }
    }
}
