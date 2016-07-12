package com.wb.nextgen.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.SimpleTarget;
import com.davemorrissey.labs.subscaleview.ImageSource;
import com.davemorrissey.labs.subscaleview.SubsamplingScaleImageView;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.wb.nextgen.R;
import com.wb.nextgen.adapter.ActorDetailGalleryRecyclerAdapter;
import com.wb.nextgen.data.MovieMetaData;
import com.wb.nextgen.data.MovieMetaData.CastHeadShot;
import com.wb.nextgen.fragment.ECGalleryViewFragment;
import com.wb.nextgen.fragment.ECVideoViewFragment;
import com.wb.nextgen.util.utils.StringHelper;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by gzcheng on 7/11/16.
 */
public class ActorGalleryActivity extends AbstractNextGenActivity implements ActorDetailGalleryRecyclerAdapter.ActorGalleryRecyclerSelectionListener{

    public static String HEAD_SHOTS_KEY = "HEAD_SHOTS";
    public static String CURRENT_INDEX_KEY = "CURRENT_INDEX";

    RecyclerView actorGalleryRecycler;
    ViewPager actorGalleryViewPager;
    List<CastHeadShot> actorGalleryItems;

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

        if (actorGalleryViewPager != null){
            galleryPagerAdapter = new ActorGalleryPagerAdapter(this);
            actorGalleryViewPager.setAdapter(galleryPagerAdapter);
            actorGalleryViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
                @Override
                public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

                }

                @Override
                public void onPageSelected(int position) {
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
                public void onPageScrollStateChanged(int state) {

                }
            });
            actorGalleryViewPager.setCurrentItem(startUpSelectedIndex);
        }

        if (actorGalleryRecycler != null){
            recyclerLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
            actorGalleryRecycler.setLayoutManager(recyclerLayoutManager);
            galleryRecyclerAdapter = new ActorDetailGalleryRecyclerAdapter(this, this);
            galleryRecyclerAdapter.setCastHeadShots(actorGalleryItems);
            galleryRecyclerAdapter.setSelectedIndex(startUpSelectedIndex);
            actorGalleryRecycler.setAdapter(galleryRecyclerAdapter);
        }
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

    public void onItemSelected(MovieMetaData.CastHeadShot headShot, int index){
        actorGalleryViewPager.setCurrentItem(index);
        //galleryRecyclerAdapter.setSelectedIndex(index);
        //galleryRecyclerAdapter.notifyDataSetChanged();
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

            String useUrl = currentItem.largeUrl;

            itemView.setTag(useUrl);

            // Get the border size to show around each image
            int borderSize = 0;//_thumbnails.getPaddingTop();


            final SubsamplingScaleImageView imageView =
                    (SubsamplingScaleImageView) itemView.findViewById(R.id.image);

            // Asynchronously load the image and set the thumbnail and pager view
            Glide.with(mContext)
                    .load(useUrl)
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
