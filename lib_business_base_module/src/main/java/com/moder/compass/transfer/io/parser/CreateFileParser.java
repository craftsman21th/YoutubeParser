package com.moder.compass.transfer.io.parser;

import java.io.IOException;

import org.json.JSONException;

import com.dubox.drive.kernel.architecture.debug.DuboxLog;
import com.moder.compass.transfer.io.model.CreateFileResponse;
import com.dubox.drive.base.service.BaseServiceHelper;
import com.dubox.drive.kernel.architecture.net.HttpResponse;
import com.dubox.drive.kernel.architecture.net.exception.RemoteException;
import com.dubox.drive.kernel.architecture.net.parser.IApiResultParseable;
import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;

/**
 * Created by linchangxin on 16/4/5.
 */
public class CreateFileParser implements IApiResultParseable<CreateFileResponse> {
    private static final String TAG = "CreateFileResponse";

    @Override
    public CreateFileResponse parse(HttpResponse response) throws JSONException, RemoteException, IOException {
        final CreateFileResponse createFileResponse;
        try {
            String content = response.getContent();
            DuboxLog.d(TAG, "content = " + content);
            createFileResponse = new Gson().fromJson(content, CreateFileResponse.class);
            if (createFileResponse != null) {
                createFileResponse.mRawString = content;
                createFileResponse.setRequestUrl(response.getUrl());
            }
        } catch (JsonSyntaxException e) {
            throw new JSONException(e.getMessage());
        } catch (IllegalArgumentException e) {
            throw new JSONException(e.getMessage());
        } catch (IncompatibleClassChangeError e) {
            throw new JSONException(e.getMessage());
        }

        DuboxLog.d(TAG, "resourceResponse:" + createFileResponse);

        if (createFileResponse == null) {
            throw new JSONException(TAG + " JsonParser is null.");
        }

        if (createFileResponse.getErrorNo() != 0) {
            throw BaseServiceHelper.buildRemoteException(createFileResponse.getErrorNo(), null, createFileResponse);
        }

        return createFileResponse;
    }
}
