package net.flixster.android.drm;

public interface IVideoViewActionListener {
	void onPause();
	
	void onResume();
	
	void onTimeBarSeekChanged(int currentTime);
}
