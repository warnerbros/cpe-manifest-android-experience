package com.wb.nextgen.fragment;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.AttributeSet;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.SeekBar;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.squareup.picasso.Picasso;
import com.wb.nextgen.NextGenApplication;
import com.wb.nextgen.R;
import com.wb.nextgen.data.MovieMetaData;
import com.wb.nextgen.util.concurrent.ResultListener;
import com.wb.nextgen.util.concurrent.Worker;
import com.wb.nextgen.util.utils.F;
import com.wb.nextgen.util.utils.NextGenLogger;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
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
        boolean bNeedReloadBitmaps = gallery == null || currentGallery == null || !gallery.equals(currentGallery) || turntableBitmaps == null;

        super.setCurrentGallery(gallery);
        if (bNeedReloadBitmaps){
            if (turnTableSeekBar != null)
                turnTableSeekBar.setMax(0);
            if (turnTableImageView != null)
                turnTableImageView.setImageBitmap(null);

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

                double fractionNumber = currentGallery.galleryImages.size() > 50 ? (double)currentGallery.galleryImages.size() / 50.0 : currentGallery.galleryImages.size();

                List<Bitmap> result = new ArrayList<Bitmap>();

                for (int i = 0 ; i * fractionNumber < currentGallery.galleryImages.size(); i++ ) {
                    MovieMetaData.PictureItem item = currentGallery.galleryImages.get((int)(i * fractionNumber));

                    Bitmap theBitmap = Glide.
                            with(NextGenApplication.getContext()).
                            load(item.fullImage.url).asBitmap().
                            into(RESTRICTED_IMAGE_WIDTH, RESTRICTED_IMAGE_HEIGHT). // Width and height
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
