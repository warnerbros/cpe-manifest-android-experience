package com.wb.nextgenlibrary.fragment;

import android.app.ActivityManager;
import android.content.Context;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.SeekBar;

import com.bumptech.glide.Glide;
import com.wb.nextgenlibrary.NextGenExperience;
import com.wb.nextgenlibrary.R;
import com.wb.nextgenlibrary.data.MovieMetaData;
import com.wb.nextgenlibrary.util.concurrent.ResultListener;
import com.wb.nextgenlibrary.util.concurrent.Worker;
import com.wb.nextgenlibrary.util.utils.F;
import com.wb.nextgenlibrary.util.utils.NextGenLogger;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;

/**
 * Created by gzcheng on 7/13/16.
 */
public class ECTurnTableViewFragment extends AbstractECGalleryViewFragment{

    SeekBar turnTableSeekBar;
    ImageView turnTableImageView;
    ProgressBar loadingProgressBar;

    private static int RESTRICTED_IMAGE_HEIGHT = 540;
    private static int RESTRICTED_IMAGE_WIDTH = 960;

    List<Bitmap> turntableBitmaps = new ArrayList<Bitmap>();
    @Override
    public int getContentViewId(){
        return R.layout.ec_turntable_frame_view;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);


        turnTableSeekBar = (SeekBar) view.findViewById(R.id.turn_table_seekbar);
        turnTableSeekBar.setOnSeekBarChangeListener(seekBarListener);
        loadingProgressBar = (ProgressBar)view.findViewById(R.id.loading_progress_bar);
        if (loadingProgressBar != null)
            loadingProgressBar.setMax(100);

        turnTableImageView = (ImageView) view.findViewById(R.id.turn_table_image_view);
        if (currentGallery != null){
            setCurrentGallery(currentGallery);
        }

        turnTableImageView.setOnTouchListener(new OnSwipeTouchListener());
    }

    void nextPic(){
        int currentIndex = turnTableSeekBar.getProgress();
        if (currentIndex < 49)
            turnTableSeekBar.setProgress(currentIndex + 1);
        else{
            turnTableSeekBar.setProgress(0);
        }
    }

    void prevPic(){
        int currentIndex = turnTableSeekBar.getProgress();
        if (currentIndex > 0)
            turnTableSeekBar.setProgress(currentIndex - 1);
        else
            turnTableSeekBar.setProgress(50);
    }

    SeekBar.OnSeekBarChangeListener seekBarListener = new SeekBar.OnSeekBarChangeListener() {
        @Override
        public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
            if (turntableBitmaps != null && turnTableImageView != null && progress < turntableBitmaps.size()){
                turnTableImageView.setImageBitmap(turntableBitmaps.get(progress));

            }

        }

        @Override
        public void onStartTrackingTouch(SeekBar seekBar) {

        }

        @Override
        public void onStopTrackingTouch(SeekBar seekBar) {

        }
    };

    @Override
    public void onDestroy(){
        super.onDestroy();
        turnTableImageView.setImageDrawable(null);
        turntableBitmaps = null;
    }


    public void setCurrentGallery(MovieMetaData.ECGalleryItem gallery){
        boolean bNeedReloadBitmaps = gallery == null || currentGallery == null || !gallery.equals(currentGallery) || turntableBitmaps == null || turntableBitmaps.size() == 0;

        super.setCurrentGallery(gallery);
        if (getActivity() == null)
            return;
        if (bNeedReloadBitmaps){
            if (turnTableSeekBar != null)
                turnTableSeekBar.setMax(0);
            if (turnTableImageView != null)
                turnTableImageView.setImageBitmap(null);

            if (turntableBitmaps != null){
                turntableBitmaps.clear();
            }

            turntableBitmaps = null;
            if (loadingProgressBar != null)
                loadingProgressBar.setVisibility(View.VISIBLE);

            getImages(new ResultListener<List<Bitmap>>() {
                @Override
                public void onResult(List<Bitmap> result) {
                    turntableBitmaps = result;
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            seekBarListener.onProgressChanged(turnTableSeekBar, 0, false);
                            turnTableSeekBar.setMax(turntableBitmaps.size());
                            loadingProgressBar.setVisibility(View.GONE);
                            loadingProgressBar.setProgress(0);

                        }
                    });
                }

                @Override
                public <E extends Exception> void onException(E e) {
                    NextGenLogger.d(F.TAG, e.getLocalizedMessage());
                }
            });
        }
        /*
        if (turnTableSeekBar != null && turnTableImageView != null) {
            turnTableSeekBar.setMax(gallery.galleryImages.size()/4);
        }*/
    }



    public void getImages(final ResultListener<List<Bitmap>> l){
        Worker.execute(new Callable<List<Bitmap>>() {
            @Override
            public List<Bitmap> call() throws Exception {

                if (getActivity() == null)
                    return null;

                double fractionNumber = currentGallery.galleryImages.size() > 50 ? (double)currentGallery.galleryImages.size() / 50.0 : currentGallery.galleryImages.size();

                ActivityManager actManager = (ActivityManager) getActivity().getSystemService(Context.ACTIVITY_SERVICE);
                ActivityManager.MemoryInfo memInfo = new ActivityManager.MemoryInfo();
                actManager.getMemoryInfo(memInfo);
                long availMemory = memInfo.availMem / 2 ;

                long perPageSize = availMemory / 1024 / 50 ;
                long perUnitSize = perPageSize / (16 * 9);


                int targetWidth = (int)perUnitSize * 16;

                if (targetWidth > RESTRICTED_IMAGE_WIDTH)
                    targetWidth = RESTRICTED_IMAGE_WIDTH;

                int targetHeight = targetWidth * 9 / 16;

                NextGenLogger.d("TurnTable", "Image width " + targetWidth + " Image Height " + targetHeight);

                List<Bitmap> result = new ArrayList<Bitmap>();

                for (int i = 0 ; i * fractionNumber < currentGallery.galleryImages.size(); i++ ) {
                    MovieMetaData.PictureItem item = currentGallery.galleryImages.get((int)(i * fractionNumber));

                    Bitmap theBitmap = Glide.
                            with(NextGenExperience.getApplicationContext()).
                            load(item.fullImage.url).asBitmap().
                            into(targetWidth, targetHeight). // Width and height
                            get();
                    result.add(theBitmap);
                    final int progress = i*2;

                    if (getActivity() != null){
                        getActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                if (loadingProgressBar != null)
                                    loadingProgressBar.setProgress(progress);
                            }
                        });
                    }
                }


                return result;
            }
        }, l);
    }


    public class OnSwipeTouchListener implements View.OnTouchListener {

        @SuppressWarnings("deprecation")
        private final GestureDetector gestureDetector = new GestureDetector(new GestureListener());

        @Override
        public boolean onTouch(final View v, final MotionEvent event) {
            return gestureDetector.onTouchEvent(event);
        }

        private final class GestureListener extends GestureDetector.SimpleOnGestureListener {

            private static final int SCROLL_THRESHOLD = 5;

            @Override
            public boolean onDown(MotionEvent e) {
                return true;
            }
            @Override
            public boolean onSingleTapConfirmed(MotionEvent e) {
                onTouch(null, e);
                return true;
            }

            @Override
            public boolean onScroll(MotionEvent e1, MotionEvent e2,
                                    float distanceX, float distanceY) {
                NextGenLogger.d(F.TAG, "Moved: " + distanceX);

                int imageWidth = turnTableImageView.getWidth();
                int threshold = imageWidth / 200;

                if (distanceX > threshold) { //next page
                    nextPic();
                } else if (distanceX < -threshold) { //previous page
                    prevPic();
                }

                return false;
            }

            @Override
            public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
                boolean result = false;
                try {
                    float diffY = e2.getY() - e1.getY();
                    float diffX = e2.getX() - e1.getX();

                } catch (Exception exception) {
                    exception.printStackTrace();
                }
                return result;
            }
        }

    }
}
