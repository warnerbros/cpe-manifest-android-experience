package com.wb.nextgen.activity;

import android.app.ActionBar;
import android.content.Context;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.LinearLayout;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.SimpleTarget;
import com.davemorrissey.labs.subscaleview.ImageSource;
import com.davemorrissey.labs.subscaleview.SubsamplingScaleImageView;
import com.wb.nextgen.R;
import com.wb.nextgen.data.DemoData;

/**
 * Created by gzcheng on 3/7/16.
 */
public class ECGalleryActivity extends AbstractECView {

    private ViewPager galleryViewPager;
    private DemoData.ECContentData currentGallery;
    private GalleryPagerAdapter adapter;
    private Button fullscreenToggleBtn;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        currentGallery = ecGroupData.ecContents.get(0);
        galleryViewPager = (ViewPager) findViewById(R.id.next_gen_gallery_view_pager);
        adapter = new GalleryPagerAdapter(this);
        fullscreenToggleBtn = (Button)findViewById(R.id.gallery_fullscreen_toggle);
        if (fullscreenToggleBtn != null){
            fullscreenToggleBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onRequestToggleFullscreen();
                }
            });
        }
        galleryViewPager.setAdapter(adapter);
    }

    public int getContentViewId(){
        return R.layout.next_gen_gallery_view;
    }

    @Override
    public int getListItemViewLayoutId(){
        return R.layout.ec_gallery_list_item;
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        if (newConfig.orientation == Configuration.ORIENTATION_PORTRAIT && isContentFullScreen){
            newConfig.orientation = Configuration.ORIENTATION_LANDSCAPE;
            super.onConfigurationChanged(newConfig);
        }else{
            super.onConfigurationChanged(newConfig);
        }
    }
    @Override
    public void onRequestToggleFullscreen(){
/*
        if (!isContentFullScreen){    // make it full screen
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_SENSOR);
        } else {                     // shrink it
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_SENSOR_LANDSCAPE);
        }*/
        super.onRequestToggleFullscreen();
        adapter.notifyDataSetChanged();

    }


    class GalleryPagerAdapter extends PagerAdapter {

        Context mContext;
        LayoutInflater mInflater;

        public GalleryPagerAdapter(Context context) {
            mContext = context;
            mInflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        }

        @Override
        public int getCount() {
            return currentGallery.galleryItems.size();
        }

        @Override
        public boolean isViewFromObject(View view, Object object) {
            return view == ((LinearLayout) object);
        }

        @Override
        public Object instantiateItem(ViewGroup container, final int position) {
            View itemView = mInflater.inflate(R.layout.pager_gallery_item, container, false);
            container.addView(itemView);

            DemoData.ECGalleryImageItem currentItem = currentGallery.galleryItems.get(position);

            itemView.setTag(currentItem.name);

            // Get the border size to show around each image
            int borderSize = 0;//_thumbnails.getPaddingTop();

            // Get the size of the actual thumbnail image
            int thumbnailSize = ((FrameLayout.LayoutParams)
                    galleryViewPager.getLayoutParams()).bottomMargin - (borderSize*2);

            // Set the thumbnail layout parameters. Adjust as required
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(thumbnailSize, thumbnailSize);
            params.setMargins(0, 0, borderSize, 0);

            final SubsamplingScaleImageView imageView =
                    (SubsamplingScaleImageView) itemView.findViewById(R.id.image);

            // Asynchronously load the image and set the thumbnail and pager view
            Glide.with(mContext)
                    .load(currentItem.fullImage.url)
                    .asBitmap()
                    .into(new SimpleTarget<Bitmap>() {
                        @Override
                        public void onResourceReady(Bitmap bitmap, GlideAnimation anim) {
                            imageView.setImage(ImageSource.bitmap(bitmap));
                            //thumbView.setImageBitmap(bitmap);
                        }
                    });

            return itemView;
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            container.removeView((LinearLayout) object);
        }

        @Override
        public int getItemPosition(Object object) {
            int position = super.getItemPosition(object);
            if (position >= 0)
                return  position;
            else
                return POSITION_NONE;
        }
    }


    @Override
    public void onLeftListItemSelected(DemoData.ECContentData ec){
        if (ec != null){
            currentGallery = ec;
            selectedECNameTextView.setText(ec.title);
            if (adapter != null)
                adapter.notifyDataSetChanged();
        }
    }
}
