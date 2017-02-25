package com.wb.nextgenlibrary.videoview;

import android.content.Context;
import android.net.Uri;
import android.util.AttributeSet;
import android.widget.VideoView;

import com.wb.nextgenlibrary.interfaces.NGEPlayerInterface;
import com.wb.nextgenlibrary.widget.CustomMediaController;


public final class ObservableVideoView extends VideoView implements NGEPlayerInterface, CustomMediaController.MediaPlayerControl{
	
	private IVideoViewActionListener mVideoViewListener;
	private boolean mIsOnPauseMode = false;
	
	public void setVideoViewListener(IVideoViewActionListener listener) {
		mVideoViewListener = listener;
	}

	@Override
	public void setVideoURI(Uri uri){
		super.setVideoURI(uri);
	}

	@Override
	public void pause() {
		super.pause();
		
		if (mVideoViewListener != null) {
			mVideoViewListener.onVideoPause();
		}
		
        if (mListener != null) {
            mListener.playerPaused(true);
        }
		
		mIsOnPauseMode = true;
	}
	
	@Override
	public void start() {
		super.start();
		if (mIsOnPauseMode) {
			if (mVideoViewListener != null) {
				mVideoViewListener.onVideoResume();
			}
			
			mIsOnPauseMode = false;
		}else {
			if (mVideoViewListener != null)
				mVideoViewListener.onVideoStart();
		}
		
        if (mListener != null) {
            mListener.playerPaused(false);
        }
	}
	
	@Override
	public void seekTo(int msec) {
		super.seekTo(msec);
		
		if (mVideoViewListener != null) {
			mVideoViewListener.onTimeBarSeekChanged(msec);
		}
	}
	
	public ObservableVideoView(Context context, AttributeSet attrs) {
		super(context, attrs);
	}
	
	public ObservableVideoView(Context context) {
		super(context);
	}
	
	public ObservableVideoView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
	}
	
	private PlayPauseListener mListener;
	 
     
    /// Register listener
    public void setPlayPauseListener(PlayPauseListener listener) {
        mListener = listener;
    }
 
    /// Interface for listener
    public interface PlayPauseListener {
        void playerPaused(boolean paused);
    }


	@Override
	public boolean canPause() {
		return true;
	}

	@Override
	public boolean canSeekBackward() {
		return true;
	}

	@Override
	public boolean canSeekForward() {
		return true;
	}

	@Override
	public int getBufferPercentage() {
		return 0;
	}



}


