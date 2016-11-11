package com.wb.nextgenlibrary.videoview;

public interface IVideoViewActionListener {
	void onPause();
	
	void onResume();

	void onStart();
	
	void onTimeBarSeekChanged(int currentTime);
}
