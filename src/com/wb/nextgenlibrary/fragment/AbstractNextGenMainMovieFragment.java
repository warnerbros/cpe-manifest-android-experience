package com.wb.nextgenlibrary.fragment;

import android.app.ProgressDialog;
import android.media.MediaPlayer;
import android.support.v4.app.Fragment;

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

    public abstract void setPlaybackObject(Object playbackObject);
    public abstract void setCustomMediaController(CustomMediaController customMC);
    public abstract void setResumeTime(int resumeTime);
    public abstract int getCurrentPosition();

    public void setNextGenVideoViewListener(IVideoViewActionListener listener) {
        nextGenVideoViewListener = listener;
    }

    public void setOnCompletionLister(MediaPlayer.OnCompletionListener completionListener){
        this.completionListener = completionListener;
    }

    public abstract boolean isPlaying();

    public abstract void pause();
    public abstract void resumePlayback();

    public abstract void streamStartPreparations(ResultListener<Boolean> resultLister);

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


    public abstract void setProgressDialog(ProgressDialog dialog);   // do not use app's dialog, always use the NextGen Library one.
}
