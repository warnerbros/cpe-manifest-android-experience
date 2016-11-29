package com.wb.nextgenlibrary.fragment;

import android.app.ProgressDialog;
import android.media.MediaPlayer;
import android.support.v4.app.Fragment;
import android.view.View;

import com.wb.nextgenlibrary.interfaces.NextGenPlaybackStatusListener;
import com.wb.nextgenlibrary.interfaces.NextGenPlayerInterface;
import com.wb.nextgenlibrary.util.concurrent.ResultListener;
import com.wb.nextgenlibrary.videoview.IVideoViewActionListener;
import com.wb.nextgenlibrary.widget.CustomMediaController;


/**
 * Created by gzcheng on 8/15/16.
 */
public abstract class AbstractNextGenMainMovieFragment extends Fragment implements NextGenPlayerInterface{
    protected IVideoViewActionListener nextGenVideoViewListener;
    protected MediaPlayer.OnCompletionListener completionListener;
    private View loadingView;

    protected NextGenPlaybackStatusListener.NextGenPlaybackStatus playbackStatus = NextGenPlaybackStatusListener.NextGenPlaybackStatus.BUFFERING;

    public NextGenPlaybackStatusListener.NextGenPlaybackStatus getPlaybackStatus(){     // only 1 of the 3 status will be returned;
        switch (playbackStatus){

            case PREPARED:
            case READY:
            case STARTED:
            case PAUSE:
            case STOP:
            case RESUME:
            case SEEK:
            case TIMESTAMP_UPDATE:
                return NextGenPlaybackStatusListener.NextGenPlaybackStatus.READY;
            case COMPLETED:
                return NextGenPlaybackStatusListener.NextGenPlaybackStatus.COMPLETED;
            case BUFFERING:
            default:
                return NextGenPlaybackStatusListener.NextGenPlaybackStatus.BUFFERING;
        }

    }

    public abstract void setPlaybackObject(Object playbackObject);
    public abstract void setCustomMediaController(CustomMediaController customMC);
    public abstract void setResumeTime(int resumeTime);
    public abstract int getCurrentPosition();       // get current player position

    public int getMovieOffsetMilliSecond(){
        return 0;
    }

    public void setNextGenVideoViewListener(IVideoViewActionListener listener) {
        nextGenVideoViewListener = listener;
    }

    public void setOnCompletionLister(MediaPlayer.OnCompletionListener completionListener){
        this.completionListener = completionListener;
    }

    public abstract boolean isPlaying();

    public abstract void pause();
    public abstract void resumePlayback();


    public abstract void pauseForIME();
    public abstract void resumePlaybackFromIME();

    public abstract void streamStartPreparations(ResultListener<Boolean> resultLister);

    public void setLoadingView(View view){
        loadingView = view;
    }

    public void showLoadingView(){
        if (getActivity() != null && loadingView != null){
            getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    synchronized (loadingView) {
                        loadingView.setVisibility(View.VISIBLE);
                    }
                }
            });
        }
    }

    public void hideLoadingView(){
        if (getActivity() != null && loadingView != null){
            getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    synchronized (loadingView) {
                        loadingView.setVisibility(View.GONE);
                    }
                }
            });
        }
    }

    public void startIME(){
       streamStartPreparations(new ResultListener<Boolean>() {
           @Override
           public void onResult(Boolean result) {

           }

           @Override
           public <E extends Exception> void onException(E e) {

           }
       });
    }

    public  void switchMainFeatureAudio(boolean bOnOff){

    }

    //public abstract void setProgressDialog(ProgressDialog dialog);   // do not use app's dialog, always use the NextGen Library one.
}
