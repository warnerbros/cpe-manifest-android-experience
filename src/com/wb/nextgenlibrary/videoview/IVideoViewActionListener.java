package com.wb.nextgenlibrary.videoview;

public interface IVideoViewActionListener {
	void onVideoPause();
	
	void onVideoResume();

	void onVideoStart();

	void onVideoComplete();
	
	void onTimeBarSeekChanged(int currentTime);
}
