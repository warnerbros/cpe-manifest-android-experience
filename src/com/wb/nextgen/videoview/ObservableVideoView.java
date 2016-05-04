package com.wb.nextgen.videoview;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.VideoView;


public final class ObservableVideoView extends VideoView {
	
	private IVideoViewActionListener mVideoViewListener;
	private boolean mIsOnPauseMode = false;
	
	public void setVideoViewListener(IVideoViewActionListener listener) {
		mVideoViewListener = listener;
	}
	
	@Override
	public void pause() {
		super.pause();
		
		if (mVideoViewListener != null) {
			mVideoViewListener.onPause();
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
				mVideoViewListener.onResume();
			}
			
			mIsOnPauseMode = false;
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
}


