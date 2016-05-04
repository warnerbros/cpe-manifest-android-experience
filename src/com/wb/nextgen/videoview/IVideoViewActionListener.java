package com.wb.nextgen.videoview;

public interface IVideoViewActionListener {
	void onPause();
	
	void onResume();
	
	void onTimeBarSeekChanged(int currentTime);
}
