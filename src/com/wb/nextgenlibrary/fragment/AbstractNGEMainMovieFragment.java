package com.wb.nextgenlibrary.fragment;

import android.media.MediaPlayer;
import android.support.v4.app.Fragment;
import android.view.View;

import com.wb.nextgenlibrary.interfaces.NGEPlaybackStatusListener;
import com.wb.nextgenlibrary.interfaces.NGEPlayerInterface;
import com.wb.nextgenlibrary.util.concurrent.ResultListener;
import com.wb.nextgenlibrary.videoview.IVideoViewActionListener;
import com.wb.nextgenlibrary.widget.CustomMediaController;

import java.util.List;


/**
 * Created by gzcheng on 8/15/16.
 */
public abstract class AbstractNGEMainMovieFragment extends Fragment implements NGEPlayerInterface {
    protected IVideoViewActionListener nextGenVideoViewListener;
    protected MediaPlayer.OnCompletionListener completionListener;
    private View loadingView;
    protected List<String> commentaryTrackUrls;

    protected int activeCommentaryTrack = -1;


    protected NGEPlaybackStatusListener.NextGenPlaybackStatus playbackStatus = NGEPlaybackStatusListener.NextGenPlaybackStatus.BUFFERING;

    public NGEPlaybackStatusListener.NextGenPlaybackStatus getPlaybackStatus(){     // only 1 of the 3 status will be returned;
        switch (playbackStatus){

            case PREPARED:
            case READY:
            case STARTED:
            case PAUSE:
            case STOP:
            case RESUME:
            case SEEK:
            case TIMESTAMP_UPDATE:
                return NGEPlaybackStatusListener.NextGenPlaybackStatus.READY;
            case COMPLETED:
                return NGEPlaybackStatusListener.NextGenPlaybackStatus.COMPLETED;
            case BUFFERING:
            default:
                return NGEPlaybackStatusListener.NextGenPlaybackStatus.BUFFERING;
        }

    }

    public abstract void setPlaybackObject(Object playbackObject);
    public abstract void setCustomMediaController(CustomMediaController customMC);
    public abstract void setResumeTime(int resumeTime);
    public abstract int getCurrentPosition();       // get current player position
    public abstract int getDuration();       // get current player position

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

    /*
        You will need to release the Exoplayer when some ime video is going to be played
     */
    public abstract void pauseForIME();
    /*
        this function will be called when the ime video finished playback, so if you are using exoplayer, you can recreate the player and resume its playback
     */
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

    @Override
    public void onDestroy(){
        super.onDestroy();
        nextGenVideoViewListener = null;
        completionListener = null;
        loadingView = null;
    }

    /*
        If your main movie fragment cannot handle switching commentary track.
        you must implement this function to mute/unmute main feature audio for NGE library to use
     */
    public  void switchMainFeatureAudio(boolean bOnOff){

    }

    /*
        Function to tell NGE Library that if the main movie fragment can handle commentary track switching.
        Default to be false;
        If this returns true, NGE will expect this fragmnent to implement setActiveCommentaryTrack(int) to handle commentary audio track switching
     */
    public boolean canHandleCommentaryAudioTrackSwitching(){
        return false;
    }

    /*
        Function for NGE to feed in commentary(s) tracks urls into the player fragment
     */
    public void setCommentaryTrackUrls(List<String> commentaryTrackUrls){
        this.commentaryTrackUrls = commentaryTrackUrls;
    }

    /*
        if tracknumber < 0 => turn off commentary.
        else select the commentary track from setCommentaryTrackUrls accordingly
     */
    public void setActiveCommentaryTrack(int tracknumber){
        activeCommentaryTrack = tracknumber;
    }
}
