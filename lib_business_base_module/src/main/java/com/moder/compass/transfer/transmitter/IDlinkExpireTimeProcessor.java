package com.moder.compass.transfer.transmitter;

import com.moder.compass.transfer.transmitter.throwable.StopRequestException;

import java.util.Map;

/**
 * dlink过期处理器
 * 
 * @author sunqi01
 */
public interface IDlinkExpireTimeProcessor {
    String getDlink() throws StopRequestException;

    void delDlinkRecord();

    long getUK();

    String getDuboxPath();

    Map<String, String> getDlinkParameters() throws StopRequestException;

    String getFsid();
}
