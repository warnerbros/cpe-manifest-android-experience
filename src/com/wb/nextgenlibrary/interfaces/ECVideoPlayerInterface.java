package com.wb.nextgenlibrary.interfaces;

import com.wb.cpedata.data.manifest.AudioVisualItem;
import com.wb.nextgenlibrary.fragment.ECVideoViewFragment;

/**
 * Created by gzcheng on 1/27/17.
 */

public interface ECVideoPlayerInterface {

	void setEcsAdaptor(ECVideoViewFragment.ECVideoListAdaptor adaptor);

	void setShouldAutoPlay(boolean shouldAutoPlay);

	void stopPlayback();

	void setAudioVisualItem(AudioVisualItem avItem);

	void onFullScreenChange(boolean bFullscreen);

	void setResumeTimeMillisecond(int resumeTime);

	int getCurrentPlaybackTimeMillisecond();
}
