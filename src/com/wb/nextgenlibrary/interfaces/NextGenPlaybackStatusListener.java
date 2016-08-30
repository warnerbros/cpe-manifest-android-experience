package com.wb.nextgenlibrary.interfaces;

/**
 * Created by gzcheng on 1/19/16.
 */
public interface NextGenPlaybackStatusListener {
    public static enum NextGenPlaybackStatus{
        PREPARED, STARTED, STOP, PAUSE, RESUME, SEEK, TIMESTAMP_UPDATE
    }
    public void playbackStatusUpdate(NextGenPlaybackStatus playbackStatus, long timecode);
}
