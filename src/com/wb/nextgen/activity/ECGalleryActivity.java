package com.wb.nextgen.activity;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        currentGallery = ecGroupData.ecContents.get(0);
        galleryViewPager = (ViewPager) findViewById(R.id.next_gen_gallery_view_pager);
        adapter = new GalleryPagerAdapter(this);
        galleryViewPager.setAdapter(adapter);
    }

    public int getContentViewId(){
        return R.layout.next_gen_gallery_view;
    }

    @Override
    public int getListItemViewLayoutId(){
        return R.layout.ec_gallery_list_item;
    }

/*
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
    }*/
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
            int thumbnailSize = ((LinearLayout.LayoutParams)
                    galleryViewPager.getLayoutParams()).bottomMargin - (borderSize*2);

            // Set the thumbnail layout parameters. Adjust as required
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(thumbnailSize, thumbnailSize);
            params.setMargins(0, 0, borderSize, 0);

            // You could also set like so to remove borders
            //ViewGroup.LayoutParams params = new ViewGroup.LayoutParams(
            //        ViewGroup.LayoutParams.WRAP_CONTENT,
            //        ViewGroup.LayoutParams.WRAP_CONTENT);
            /*
            final ImageView thumbView = new ImageView(mContext);
            thumbView.setScaleType(ImageView.ScaleType.CENTER_CROP);
            thumbView.setLayoutParams(params);
            thumbView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Log.d(TAG, "Thumbnail clicked");

                    // Set the pager position when thumbnail clicked
                    galleryViewPager.setCurrentItem(position);
                }
            });
            _thumbnails.addView(thumbView);*/

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
            if (adapter != null)
                adapter.notifyDataSetChanged();
        }
    }
}
