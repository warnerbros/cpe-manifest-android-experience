package com.wb.nextgenlibrary.interfaces;

/**
 * Created by gzcheng on 11/10/16.
 */

public interface IMEVideoStatusListener {
	void onVideoStartPlaying();
	void onFragmentDestroyed();
	void onVideoPause();
	void onVideoResume();
	void onVideoComplete();
}
