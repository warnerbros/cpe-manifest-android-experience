package com.wb.nextgenlibrary.activity;

import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.LinearLayout;

import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.SimpleTarget;
import com.davemorrissey.labs.subscaleview.ImageSource;
import com.davemorrissey.labs.subscaleview.SubsamplingScaleImageView;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.wb.nextgenlibrary.R;
import com.wb.nextgenlibrary.adapter.ActorDetailGalleryRecyclerAdapter;
import com.wb.nextgenlibrary.analytic.NGEAnalyticData;
import com.wb.nextgenlibrary.data.MovieMetaData.CastHeadShot;
import com.wb.nextgenlibrary.util.TabletUtils;
import com.wb.nextgenlibrary.util.utils.NextGenGlide;
import com.wb.nextgenlibrary.util.utils.StringHelper;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by gzcheng on 7/11/16.
 */
public class ActorGalleryActivity extends AbstractNGEActivity implements ActorDetailGalleryRecyclerAdapter.ActorGalleryRecyclerSelectionListener{

    public static String HEAD_SHOTS_KEY = "HEAD_SHOTS";
    public static String CURRENT_INDEX_KEY = "CURRENT_INDEX";
    public static String TALENT_ID = "TALENT_ID";

    RecyclerView actorGalleryRecycler;
    ViewPager actorGalleryViewPager;
    List<CastHeadShot> actorGalleryItems;
    ImageButton closeBtn;

    ActorDetailGalleryRecyclerAdapter galleryRecyclerAdapter;
    ActorGalleryPagerAdapter galleryPagerAdapter;
    LinearLayoutManager recyclerLayoutManager;

    int startUpSelectedIndex = 0;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.actor_gallery_view);
        actorGalleryViewPager = (ViewPager) findViewById(R.id.actor_gallery_viewpager);
        actorGalleryRecycler = (RecyclerView) findViewById(R.id.actor_gallery_view_recycler);

        Intent intent = getIntent();
        String headShots = intent.getStringExtra(HEAD_SHOTS_KEY);
        startUpSelectedIndex = intent.getIntExtra(CURRENT_INDEX_KEY, 0);
        if (!StringHelper.isEmpty(headShots)){
            Type listType = new TypeToken<ArrayList<CastHeadShot>>() {
            }.getType();

            Gson gson = new GsonBuilder().create();

            actorGalleryItems = gson.fromJson(headShots, listType);
        }

        closeBtn = (ImageButton) findViewById(R.id.close_button);
        if (closeBtn != null) {

            closeBtn.setVisibility(View.VISIBLE);

            closeBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //NGEAnalyticData.reportEvent(ActorGalleryActivity.this, null, "Back Button", NGEAnalyticData.AnalyticAction.ACTION_CLICK, null);
                    onBackPressed();
                }
            });
        }

        if (actorGalleryViewPager != null){
            galleryPagerAdapter = new ActorGalleryPagerAdapter(this);
            actorGalleryViewPager.setAdapter(galleryPagerAdapter);
            actorGalleryViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
                @Override
                public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

                }

                @Override
                public void onPageSelected(int position) {
                    scrollBottomPagerToShow(position);
                }

                @Override
                public void onPageScrollStateChanged(int state) {

                }
            });
            actorGalleryViewPager.setCurrentItem(startUpSelectedIndex);
        }

        if (actorGalleryRecycler != null){
            recyclerLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
            actorGalleryRecycler.setLayoutManager(recyclerLayoutManager);
            galleryRecyclerAdapter = new ActorDetailGalleryRecyclerAdapter(this, ActorDetailGalleryRecyclerAdapter.ImageRedition.LARGE, this);
            galleryRecyclerAdapter.setCastHeadShots(actorGalleryItems);
            galleryRecyclerAdapter.setSelectedIndex(startUpSelectedIndex);
            actorGalleryRecycler.setAdapter(galleryRecyclerAdapter);
            actorGalleryRecycler.scrollToPosition(startUpSelectedIndex);
        }
    }

    protected void scrollBottomPagerToShow(int position){
        if (actorGalleryRecycler != null && recyclerLayoutManager != null){
            int startIndex = recyclerLayoutManager.findFirstCompletelyVisibleItemPosition();
            int lastIndex = recyclerLayoutManager.findLastCompletelyVisibleItemPosition();
            int visibleCount = lastIndex - startIndex;

            if (position >= startIndex && position <= lastIndex){
                // do nothing
            } else if (position < startIndex) {
                int newPosition = Math.max(position - (visibleCount/2), 0);
                actorGalleryRecycler.smoothScrollToPosition(newPosition);
            } else if (position > lastIndex) {
                int newPosition = Math.min(position + (visibleCount/2), galleryPagerAdapter.getCount() -1 );
                actorGalleryRecycler.smoothScrollToPosition(newPosition);
            }

            if (galleryRecyclerAdapter != null) {
                galleryRecyclerAdapter.setSelectedIndex(position);
                galleryRecyclerAdapter.notifyDataSetChanged();
            }
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        if (TabletUtils.isTablet())
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_SENSOR_LANDSCAPE);
        else
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
    }

    @Override
    public void onDestroy(){
        galleryRecyclerAdapter = null;
        galleryPagerAdapter = null;
        if (actorGalleryRecycler != null)
            actorGalleryRecycler.setAdapter(null);
        if (actorGalleryRecycler != null)
            actorGalleryViewPager.setAdapter(null);
        super.onDestroy();
    }

    @Override
    public String getBackgroundImgUri(){
        return "";
    }
    public String getLeftButtonText(){
        return "";
    }
    public String getRightTitleImageUri(){
        return null;
    }
    public String getRightTitleText(){
        return "ACTOR GALLERY";
    }

    public void onItemSelected(int index){
        actorGalleryViewPager.setCurrentItem(index);
        NGEAnalyticData.reportEvent(this, null, NGEAnalyticData.AnalyticAction.ACTION_SELECT_IMAGE, actorGalleryItems.get(index).imageId, null);

    }


    class ActorGalleryPagerAdapter extends PagerAdapter {

        Context mContext;
        LayoutInflater mInflater;
        int currentSelectedIndex = -1;

        public ActorGalleryPagerAdapter(Context context) {
            mContext = context;
            mInflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        }

        @Override
        public int getCount() {
            if (actorGalleryItems != null)
                return actorGalleryItems.size();
            else
                return 0;
        }

        @Override
        public boolean isViewFromObject(View view, Object object) {
            return view == ((LinearLayout) object);
        }

        @Override
        public Object instantiateItem(ViewGroup container, final int position) {
            View itemView = mInflater.inflate(R.layout.pager_gallery_item, container, false);
            container.addView(itemView);

            CastHeadShot currentItem = actorGalleryItems.get(position);

            String useUrl = currentItem.fullSizeUrl;

            itemView.setTag(useUrl);

            // Get the border size to show around each image
            int borderSize = 0;//_thumbnails.getPaddingTop();


            final SubsamplingScaleImageView imageView =
                    (SubsamplingScaleImageView) itemView.findViewById(R.id.image);

            // Asynchronously load the image and set the thumbnail and pager view
            NextGenGlide.load((ActorGalleryActivity.this), useUrl)
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
}
