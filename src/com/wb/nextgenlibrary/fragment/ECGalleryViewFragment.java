package com.wb.nextgenlibrary.fragment;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.PointF;
import android.os.Bundle;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.SimpleTarget;
import com.davemorrissey.labs.subscaleview.ImageSource;
import com.davemorrissey.labs.subscaleview.SubsamplingScaleImageView;
import com.wb.nextgenlibrary.NextGenExperience;
import com.wb.nextgenlibrary.R;
import com.wb.nextgenlibrary.analytic.NGEAnalyticData;
import com.wb.nextgenlibrary.data.MovieMetaData;
import com.wb.nextgenlibrary.util.NGEUtils;
import com.wb.nextgenlibrary.util.utils.NextGenGlide;
import com.wb.nextgenlibrary.util.utils.NextGenLogger;
import com.wb.nextgenlibrary.util.utils.StringHelper;
import com.wb.nextgenlibrary.widget.FixedAspectRatioFrameLayout;

import java.util.HashMap;

/**
 * Created by gzcheng on 3/31/16.
 */
public class ECGalleryViewFragment extends AbstractECGalleryViewFragment implements View.OnClickListener {

	private ViewPager galleryViewPager;
    private GalleryPagerAdapter adapter;
    FixedAspectRatioFrameLayout aspectRatioFrame = null;
    ImageView bgImageView;
    Button shareImageButton;
    private TextView countText, galleryDescriptionText;
    boolean shouldShowShareBtn = true;
	ImageButton prevClipButton, nextClipButton;
	int itemIndex = 0;
	int bgColor = -1;

    String bgImageUrl = null;
    FixedAspectRatioFrameLayout.Priority aspectFramePriority = null;
	int aspectWidth=1, aspectHeight=1;

    boolean bSetOnResume= false;

    public void setBGImageUrl(String url){
        bgImageUrl = url;
    }

	public void setBGColor(int color){
		bgColor = color;
	}

	int contentViewId = R.layout.ec_gallery_frame_view;

	View.OnLayoutChangeListener onLayoutChangeListener = null;

    @Override
    public int getContentViewId(){
        return contentViewId;
    }

    public void setContentViewId(int contentViewId){
		this.contentViewId = contentViewId;
	}

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        galleryViewPager = (ViewPager) view.findViewById(R.id.next_gen_gallery_view_pager);
        adapter = new GalleryPagerAdapter(getActivity());

        galleryViewPager.setAdapter(adapter);

		galleryDescriptionText = (TextView)view.findViewById(R.id.gallery_description);

		countText = (TextView) view.findViewById(R.id.count_text);
		countText.setVisibility(View.GONE);
		galleryViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
			@Override
			public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

			}

			@Override
			public void onPageSelected(int position) {
				itemIndex = position;
				updateUI(true);
			}

			@Override
			public void onPageScrollStateChanged(int state) {
				if (state == ViewPager.SCROLL_STATE_SETTLING) {

				} else if (state == ViewPager.SCROLL_STATE_DRAGGING) {

				} else if (state == ViewPager.SCROLL_STATE_IDLE) {
                    resetImageSizeActivities();
				}
			}
		});

		onLayoutChangeListener = new View.OnLayoutChangeListener() {
			@Override
			public void onLayoutChange(View v, int left, int top, int right, int bottom, int oldLeft, int oldTop, int oldRight, int oldBottom) {
				if (v instanceof SubsamplingScaleImageView){
					((SubsamplingScaleImageView)v).setScaleAndCenter(0, new PointF(0f, 0f));
				}
			}
		};

		aspectRatioFrame = (FixedAspectRatioFrameLayout) view.findViewById(R.id.gallery_aspect_ratio_frame);
        if (aspectRatioFrame != null){
			aspectWidth = aspectRatioFrame.getAspectRatioWidth();
			aspectHeight = aspectRatioFrame.getAspectRatioHeight();
            aspectRatioFrame.setAspectRatioPriority(aspectFramePriority);
        }

        bgImageView = (ImageView)view.findViewById(R.id.ec_gallery_frame_bg);

        if (bgImageView != null && !StringHelper.isEmpty(bgImageUrl)){
			NextGenGlide.load(getActivity(), bgImageUrl).fitCenter().into(bgImageView);
        }
		if (bgColor != -1){
			bgImageView.setBackgroundColor(bgColor);
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

					String shareString = imageUrl;
					if (NextGenExperience.getManifestItem() != null){
						shareString = getResources().getString(R.string.share_image_text,NextGenExperience.getManifestItem().movieName) + imageUrl;
					}

					NextGenExperience.launchSocialSharingWithUrl(getActivity(), shareString);
                }
            });
        }

		prevClipButton = (ImageButton) view.findViewById(R.id.prev_clip_btn);
		if (prevClipButton != null){
			prevClipButton.setOnClickListener(this);
		}
		nextClipButton = (ImageButton) view.findViewById(R.id.next_clip_btn);
		if (nextClipButton != null){
			nextClipButton.setOnClickListener(this);
		}
		updateUI(false);
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
        if (galleryViewPager != null) {
			galleryViewPager.setAdapter(null);
		}
        super.onDestroy();
    }

    public void setCurrentGallery(MovieMetaData.ECGalleryItem gallery){
        super.setCurrentGallery(gallery);
		itemIndex = 0;
        if (adapter != null) {
            adapter.notifyDataSetChanged();
            galleryViewPager.setCurrentItem(0);
			updateUI(false);
        }else{
            bSetOnResume = true;
        }

        if (galleryDescriptionText != null && gallery != null && !StringHelper.isEmpty(gallery.getSummary()) ){
			galleryDescriptionText.setText(gallery.getSummary());
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
		if (adapter != null)
	        adapter.notifyDataSetChanged();
		if (aspectRatioFrame != null){
			if (isContentFullScreen){
				aspectRatioFrame.setAspectRatio(getActivity().getWindowManager().getDefaultDisplay().getWidth(), getActivity().getWindowManager().getDefaultDisplay().getHeight());
			}else
				aspectRatioFrame.setAspectRatio(aspectWidth, aspectHeight);
		}

    }

	@Override
	public void onClick(View v) {
		if (v.getId() == R.id.prev_clip_btn){
			if (itemIndex > 0) {
				itemIndex--;
			}
		} else if (v.getId() == R.id.next_clip_btn){
			if (itemIndex < adapter.getCount() - 1){
				itemIndex++;
			}
		}
        galleryViewPager.setCurrentItem(itemIndex, true);	// use smooth scrolling
	}

	public void updateUI(boolean userTriggerd) {
		if (adapter != null && adapter.getCount() > 1) {
			countText.setText((itemIndex + 1) + "/" + adapter.getCount());
			if (userTriggerd) {
				countText.clearAnimation();
				if (countText.getVisibility() == View.GONE) {
					countText.setVisibility(View.VISIBLE);
				}
			}

			int prevBtnVisibility = View.VISIBLE;
			int nextBtnVisibility = View.VISIBLE;
			if (itemIndex == 0) {
				prevBtnVisibility = View.INVISIBLE;
			}
			if (itemIndex == adapter.getCount() - 1) {
				nextBtnVisibility = View.INVISIBLE;
			}

			prevClipButton.setVisibility(prevBtnVisibility);
			nextClipButton.setVisibility(nextBtnVisibility);
		}
	}

	public void fadeOutCountText() {
		if (countText != null && countText.getVisibility() == View.VISIBLE) {
			ECGalleryViewFragment.this.getActivity().runOnUiThread(new Runnable() {
				@Override
				public void run() {
					Animation fade_out = AnimationUtils.loadAnimation(ECGalleryViewFragment.this.getActivity(), R.anim.fade_out);
					fade_out.setStartOffset(3000);		// start after 3 secs
					countText.startAnimation(fade_out);
					countText.setVisibility(View.GONE);
				}
			});
		}
	}

    public void resetImageSizeActivities(){
        adapter.notifyDataSetChanged();
        updateUI(true);
        fadeOutCountText();
    }

    class GalleryPagerAdapter extends PagerAdapter {

        LayoutInflater mInflater;
		HashMap<String, View> viewHashMap = new HashMap<>();

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
			View itemView = viewHashMap.get(object);
			if (itemView != null && itemView.equals(view)){
				return true;
			}
            return false;
        }

        @Override
        public Object instantiateItem(ViewGroup container, final int position) {
			View itemView = mInflater.inflate(R.layout.pager_gallery_item, container, false);
			container.addView(itemView);

            MovieMetaData.ECGalleryItem currentItem = currentGallery;

            // Get the border size to show around each image
            int borderSize = 0;//_thumbnails.getPaddingTop();

            // Get the size of the actual thumbnail image
			int bottomMargin = 0;
			if (galleryViewPager.getLayoutParams() instanceof FrameLayout.LayoutParams){
				bottomMargin = ((FrameLayout.LayoutParams) galleryViewPager.getLayoutParams()).bottomMargin;
			} else if (galleryViewPager.getLayoutParams() instanceof RelativeLayout.LayoutParams){
				bottomMargin = ((RelativeLayout.LayoutParams) galleryViewPager.getLayoutParams()).bottomMargin;

			} else if (galleryViewPager.getLayoutParams() instanceof LinearLayout.LayoutParams){
				bottomMargin = ((LinearLayout.LayoutParams) galleryViewPager.getLayoutParams()).bottomMargin;

			}

            int thumbnailSize = bottomMargin - (borderSize*2);

            // Set the thumbnail layout parameters. Adjust as required
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(thumbnailSize, thumbnailSize);
            params.setMargins(0, 0, borderSize, 0);

            final SubsamplingScaleImageView imageView =
                    (SubsamplingScaleImageView) itemView.findViewById(R.id.image);

			imageView.addOnLayoutChangeListener(onLayoutChangeListener);

            // Asynchronously load the image and set the thumbnail and pager view
			final String imageUrl = currentItem.galleryImages.get(position).fullImage.url;

			NextGenLogger.d("ImageUrl: ", imageUrl);
			viewHashMap.put(imageUrl, itemView);
			NextGenGlide.load(getActivity(), imageUrl)
					.asBitmap()
					.into(new SimpleTarget<Bitmap>() {
						@Override
						public void onResourceReady(Bitmap bitmap, GlideAnimation anim) {
							if (imageView != null && !ECGalleryViewFragment.this.isDetached() && getActivity() != null && viewHashMap.containsKey(imageUrl)) {
								imageView.setImage(ImageSource.cachedBitmap(bitmap));
							}else {
								NextGenLogger.d("this image is being ignored: ", imageUrl);
							}
							//thumbView.setImageBitmap(bitmap);
						}
					});


            return imageUrl;
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
			if (viewHashMap != null && object != null && viewHashMap.containsKey(object)) {
				View itemView = viewHashMap.get(object);
				viewHashMap.remove(object);
				container.removeView(itemView);
				View imageView = itemView.findViewById(R.id.image);
				if (imageView  != null)
					imageView.removeOnLayoutChangeListener(onLayoutChangeListener);
			}
        }

        @Override
        public int getItemPosition(Object object) {

            for (int i = 0; i< currentGallery.galleryImages.size(); i ++){
				if (object.equals(currentGallery.galleryImages.get(i).fullImage.url)){
					return i;
				}
			}
            return POSITION_NONE;
        }
    }
}
