package com.wb.nextgenlibrary.interfaces;

/**
 * Created by gzcheng on 1/19/16.
 */
public interface NGEPlaybackStatusListener {
    public static enum NextGenPlaybackStatus{
        PREPARED, STARTED, STOP, PAUSE, RESUME, SEEK, TIMESTAMP_UPDATE, BUFFERING, COMPLETED, READY
    }

    public void playbackStatusUpdate(NextGenPlaybackStatus playbackStatus, long timecode);
}
