package com.moder.compass.transfer.io.parser;

import java.io.IOException;

import org.json.JSONException;

import com.dubox.drive.kernel.architecture.debug.DuboxLog;
import com.moder.compass.transfer.io.model.PreCreateFileResponse;
import com.dubox.drive.base.service.BaseServiceHelper;
import com.dubox.drive.kernel.architecture.net.HttpResponse;
import com.dubox.drive.kernel.architecture.net.exception.RemoteException;
import com.dubox.drive.kernel.architecture.net.parser.IApiResultParseable;
import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;

/**
 * Created by linchangxin on 16/4/1.
 */
public class PreCreateFileParser implements IApiResultParseable<PreCreateFileResponse> {
    private static final String TAG = "UploadPreCreateParser";

    @Override
    public PreCreateFileResponse parse(HttpResponse response) throws JSONException, RemoteException, IOException {
        final PreCreateFileResponse preCreateFileResponse;
        try {
            String content = response.getContent();
            DuboxLog.d(TAG, "content = " + content);
            preCreateFileResponse = new Gson().fromJson(content, PreCreateFileResponse.class);
            if (preCreateFileResponse != null) {
                preCreateFileResponse.mRawString = content;
                preCreateFileResponse.setRequestUrl(response.getUrl());
            }
        } catch (JsonSyntaxException e) {
            throw new JSONException(e.getMessage());
        } catch (IllegalArgumentException e) {
            throw new JSONException(e.getMessage());
        }

        DuboxLog.d(TAG, "resourceResponse:" + preCreateFileResponse);

        if (preCreateFileResponse == null) {
            throw new JSONException(TAG + " JsonParser is null.");
        }

        if (preCreateFileResponse.getErrorNo() != 0) {
            throw BaseServiceHelper
                .buildRemoteException(preCreateFileResponse.getErrorNo(), null, preCreateFileResponse);
        }

        return preCreateFileResponse;
    }
}
