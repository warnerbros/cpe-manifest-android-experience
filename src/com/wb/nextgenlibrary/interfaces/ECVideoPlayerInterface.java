package com.wb.nextgenlibrary.interfaces;

import com.wb.nextgenlibrary.data.MovieMetaData;
import com.wb.nextgenlibrary.fragment.ECVideoViewFragment;

/**
 * Created by gzcheng on 1/27/17.
 */

public interface ECVideoPlayerInterface {

	void setEcsAdaptor(ECVideoViewFragment.ECVideoListAdaptor adaptor);

	void setShouldAutoPlay(boolean shouldAutoPlay);

	void stopPlayback();

	void setAudioVisualItem(MovieMetaData.AudioVisualItem avItem);

	void onFullScreenChange(boolean bFullscreen);

	void setResumeTimeMillisecond(int resumeTime);

	int getCurrentPlaybackTimeMillisecond();
}
