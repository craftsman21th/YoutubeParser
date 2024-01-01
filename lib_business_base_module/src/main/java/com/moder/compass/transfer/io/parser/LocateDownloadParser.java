package com.moder.compass.transfer.io.parser;

import java.io.IOException;

import org.json.JSONException;

import com.dubox.drive.kernel.architecture.debug.DuboxLog;
import com.moder.compass.transfer.io.model.LocateDownloadResponse;
import com.dubox.drive.kernel.architecture.net.exception.RemoteException;
import com.dubox.drive.kernel.architecture.net.parser.IApiResultParseable;
import com.dubox.drive.kernel.architecture.net.HttpResponse;
import com.google.gson.Gson;
import com.google.gson.JsonIOException;
import com.google.gson.JsonParseException;
import com.google.gson.JsonSyntaxException;

/**
 * locateDownload parser
 * 
 * @author 孙奇 <br/>
 *         create at 2013-7-22 下午05:07:15
 */
public class LocateDownloadParser implements IApiResultParseable<LocateDownloadResponse> {
    private static final String TAG = "LocateDownloadParser";

    @Override
    public LocateDownloadResponse parse(HttpResponse response) throws JSONException, RemoteException, IOException {
        final LocateDownloadResponse locateDownloadResponse;
        try {
            String content = response.getContent();
            DuboxLog.d(TAG, "content = " + content);
            locateDownloadResponse = new Gson().fromJson(content, LocateDownloadResponse.class);
            if (locateDownloadResponse != null) {
                locateDownloadResponse.setRequestUrl(response.getUrl());
            }
        } catch (JsonSyntaxException e) {
            throw new JSONException(e.getMessage());
        } catch (JsonIOException e) {
            throw new IOException(e.getMessage());
        } catch (JsonParseException e) {
            throw new JSONException(e.getMessage());
        } catch (IllegalArgumentException e) {
            throw new JSONException(e.getMessage());
        }

        DuboxLog.d(TAG, "resourceResponse:" + locateDownloadResponse);

        if (locateDownloadResponse == null) {
            throw new JSONException(TAG + " JsonParser is null.");
        }
        locateDownloadResponse.httpCode = response.getResponseCode();
        return locateDownloadResponse;
    }
}
