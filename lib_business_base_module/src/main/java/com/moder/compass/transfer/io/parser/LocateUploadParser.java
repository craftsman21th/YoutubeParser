/*
 * LocateUploadParser.java
 * @author 文超
 * V 1.0.0
 * Create at 2013-6-9 上午11:49:55
 */
package com.moder.compass.transfer.io.parser;

import java.io.IOException;

import org.json.JSONException;

import com.dubox.drive.kernel.architecture.debug.DuboxLog;
import com.dubox.drive.kernel.architecture.net.exception.RemoteException;
import com.dubox.drive.kernel.architecture.net.parser.IApiResultParseable;
import com.dubox.drive.kernel.architecture.net.HttpResponse;
import com.moder.compass.transfer.io.model.LocateUploadResponse;
import com.google.gson.Gson;
import com.google.gson.JsonIOException;
import com.google.gson.JsonParseException;
import com.google.gson.JsonSyntaxException;

/**
 * 上传url获取解析器
 *
 * @author 文超 <br/>
 *         create at 2013-6-9 上午11:49:55
 */
public class LocateUploadParser implements IApiResultParseable<LocateUploadResponse> {
    private static final String TAG = "LocateUploadParser";

    /**
     * @param response
     * @return
     * @throws JSONException
     * @throws RemoteException
     * @throws IOException
     */
    @Override
    public LocateUploadResponse parse(HttpResponse response) throws JSONException, RemoteException, IOException {
        final LocateUploadResponse locateUploadResponse;
        try {
            String content = response.getContent();
            DuboxLog.d(TAG, "content = " + content);
            locateUploadResponse = new Gson().fromJson(content, LocateUploadResponse.class);
        } catch (JsonSyntaxException e) {
            throw new JSONException(e.getMessage());
        } catch (JsonIOException e) {
            throw new IOException(e.getMessage());
        } catch (JsonParseException e) {
            throw new JSONException(e.getMessage());
        } catch (IllegalArgumentException e) {
            throw new JSONException(e.getMessage());
        }

        DuboxLog.d(TAG, "resourceResponse:" + locateUploadResponse);

        if (locateUploadResponse == null) {
            throw new JSONException(TAG + " JsonParser is null.");
        }

        return locateUploadResponse;
    }
}
