package com.wb.nextgen.videoview;

import android.content.Context;
import android.net.Uri;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.widget.VideoView;

import com.wb.nextgen.interfaces.NextGenPlayerInterface;
import com.wb.nextgen.widget.CustomMediaController;
import com.wb.nextgen.widget.ECMediaController;


public final class ObservableVideoView extends VideoView implements NextGenPlayerInterface, CustomMediaController.MediaPlayerControl{
	
	private IVideoViewActionListener mVideoViewListener;
	private boolean mIsOnPauseMode = false;
	private CustomMediaController customMediaController;
	
	public void setVideoViewListener(IVideoViewActionListener listener) {
		mVideoViewListener = listener;
	}

	public void setCustomMediaController(CustomMediaController customMC){
		if (customMC == null)
			return;
		customMediaController = customMC;
		customMediaController.setMediaPlayer(this);

		ViewParent v = this.getParent();
		while (v != null && !(v instanceof ViewGroup)){
			v = v.getParent();
		}

		if (v != null)
			customMediaController.setAnchorView((ViewGroup)v);
	}

	@Override
	public void setVideoURI(Uri uri){
		super.setVideoURI(uri);
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

		if (customMediaController != null)
			customMediaController.reset();

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

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		if (customMediaController != null) {
			if (customMediaController.isShowing())
				customMediaController.hide();
			else
				customMediaController.show();
			return false;
		}else{
			return super.onTouchEvent(event);
		}
	}


	// End SurfaceHolder.Callback


	// Implement VideoMediaController.MediaPlayerControl
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


	@Override
	public boolean isFullScreen() {
		return false;
	}



}


