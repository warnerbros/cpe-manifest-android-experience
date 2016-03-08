package com.wb.nextgen.activity;

import android.os.Bundle;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.view.ViewGroup;
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
        galleryViewPager = (ViewPager) findViewById(R.id.next_gen_gallery_view_pager);
    }

    public int getContentViewId(){
        return R.layout.next_gen_gallery_view;
    }



    class GalleryPagerAdapter extends PagerAdapter {
        public int getCount(){
            return 0;
        }

        public boolean isViewFromObject(View view, Object object){
            return view == object;
        }

        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            ViewPager pager = (ViewPager) container;
            View view = getView(position, pager);

            pager.addView(view);

            return view;
        }

        public View getView(int position, ViewPager pager){


            return null;
        }
    }
    @Override
    public void onLeftListItemSelected(DemoData.ECContentData ec){
        if (ec != null){

        }
    }
}
