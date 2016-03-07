package com.wb.nextgen.interfaces;

import com.wb.nextgen.fragment.NextGenStreamAssetInfo;

/**
 * Created by gzcheng on 1/12/16.
 */
public interface NextGenCallBackInterface {
    NextGenStreamAssetInfo startStreaming(Object content);
    void stopStreaming(Object content);
    String errorCodeTranslate(int what, int extra);
    void playbackStatusUpdate(Object content, int status_code, long timecode);
    boolean acquireDRMLicense(Object content);
}
