package com.wb.nextgenlibrary.fragment;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.SimpleTarget;
import com.davemorrissey.labs.subscaleview.ImageSource;
import com.davemorrissey.labs.subscaleview.SubsamplingScaleImageView;
import com.wb.nextgenlibrary.R;
import com.wb.nextgenlibrary.analytic.NextGenAnalyticData;
import com.wb.nextgenlibrary.data.MovieMetaData;
import com.wb.nextgenlibrary.util.utils.StringHelper;
import com.wb.nextgenlibrary.widget.FixedAspectRatioFrameLayout;

/**
 * Created by gzcheng on 3/31/16.
 */
public class ECGalleryViewFragment extends AbstractECGalleryViewFragment {
    private ViewPager galleryViewPager;
    private GalleryPagerAdapter adapter;
    FixedAspectRatioFrameLayout aspectRatioFrame = null;
    ImageView bgImageView;
    Button shareImageButton;
    boolean shouldShowShareBtn = true;

    String bgImageUrl = null;
    FixedAspectRatioFrameLayout.Priority aspectFramePriority = null;

    boolean bSetOnResume= false;

    public void setBGImageUrl(String url){
        bgImageUrl = url;
    }

    @Override
    public int getContentViewId(){
        return R.layout.ec_gallery_frame_view;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        galleryViewPager = (ViewPager) view.findViewById(R.id.next_gen_gallery_view_pager);
        adapter = new GalleryPagerAdapter(getActivity());

        galleryViewPager.setAdapter(adapter);

        aspectRatioFrame = (FixedAspectRatioFrameLayout) view.findViewById(R.id.gallery_aspect_ratio_frame);
        if (aspectRatioFrame != null){
            aspectRatioFrame.setAspectRatioPriority(aspectFramePriority);
        }

        bgImageView = (ImageView)view.findViewById(R.id.ec_gallery_frame_bg);

        if (bgImageView != null && !StringHelper.isEmpty(bgImageUrl)){
            Glide.with(getActivity()).load(bgImageUrl).fitCenter().into(bgImageView);
            //PicassoTrustAll.loadImageIntoView(getActivity(), bgImageUrl, bgImageView);
        }

        shareImageButton = (Button) view.findViewById(R.id.share_image_button);
        if (shareImageButton != null){
            shareImageButton.setVisibility(shouldShowShareBtn ? View.VISIBLE : View.GONE);
            shareImageButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int currentPos = galleryViewPager.getCurrentItem();

                    if (currentPos > currentGallery.galleryImages.size()){
                        return;
                    }

                    String imageUrl = currentGallery.galleryImages.get(currentPos).fullImage.url;
                    Intent share = new Intent(Intent.ACTION_SEND);
                    share.setType("text/plain");
                    share.addFlags(Intent.FLAG_ACTIVITY_CLEAR_WHEN_TASK_RESET);
                    share.putExtra(Intent.EXTRA_SUBJECT, "Next Gen Share");
                    share.putExtra(Intent.EXTRA_TEXT, imageUrl);

                    startActivity(Intent.createChooser(share, ""));
                    NextGenAnalyticData.reportEvent(getActivity(), ECGalleryViewFragment.this, "Share Image",
                            NextGenAnalyticData.AnalyticAction.ACTION_CLICK, imageUrl);
                }
            });
        }
    }

    public void setShouldShowShareBtn(boolean bShow){
        shouldShowShareBtn = bShow;
        if (shareImageButton != null){
            shareImageButton.setVisibility(bShow ? View.VISIBLE : View.GONE);
        }
    }

    @Override
    public void onDestroy(){
        adapter = null;
        if (galleryViewPager != null)
            galleryViewPager.setAdapter(null);
        super.onDestroy();
    }

    public void setCurrentGallery(MovieMetaData.ECGalleryItem gallery){
        super.setCurrentGallery(gallery);
        if (adapter != null) {

            adapter.notifyDataSetChanged();
            galleryViewPager.setCurrentItem(0);

        }else{
            bSetOnResume = true;
        }
    }

    public void setAspectRatioFramePriority(FixedAspectRatioFrameLayout.Priority priority){
        if (aspectRatioFrame != null)
            aspectRatioFrame.setAspectRatioPriority(priority);
        aspectFramePriority = priority;
    }


    @Override
    public void onResume(){
        super.onResume();
        if (bSetOnResume){
            bSetOnResume = false;
            setCurrentGallery(currentGallery);
        }
    }

    @Override
    public void onFullScreenChange(boolean isContentFullScreen){
        super.onFullScreenChange(isContentFullScreen);
        adapter.notifyDataSetChanged();

    }

    class GalleryPagerAdapter extends PagerAdapter {

        LayoutInflater mInflater;

        public GalleryPagerAdapter(Context context) {
            mInflater = (LayoutInflater) getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        }

        @Override
        public int getCount() {
            if (currentGallery != null)
                return currentGallery.galleryImages.size();
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

            MovieMetaData.ECGalleryItem currentItem = currentGallery;

            itemView.setTag(currentItem.getTitle());

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
            Glide.with(getActivity())
                    .load(currentItem.galleryImages.get(position).fullImage.url)
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
