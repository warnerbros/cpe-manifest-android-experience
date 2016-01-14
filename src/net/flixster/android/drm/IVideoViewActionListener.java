package net.flixster.android.drm;

interface IVideoViewActionListener {
	void onPause();
	
	void onResume();
	
	void onTimeBarSeekChanged(int currentTime);
}
