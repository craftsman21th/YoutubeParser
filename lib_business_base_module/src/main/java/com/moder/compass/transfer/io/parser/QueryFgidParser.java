package com.moder.compass.transfer.io.parser;

import java.io.IOException;

import org.json.JSONException;

import com.dubox.drive.base.service.BaseServiceHelper;
import com.dubox.drive.kernel.architecture.debug.DuboxLog;
import com.dubox.drive.kernel.architecture.net.HttpResponse;
import com.dubox.drive.kernel.architecture.net.exception.RemoteException;
import com.dubox.drive.kernel.architecture.net.parser.IApiResultParseable;
import com.moder.compass.transfer.io.model.QueryFgidResponse;
import com.google.gson.Gson;
import com.google.gson.JsonIOException;
import com.google.gson.JsonParseException;
import com.google.gson.JsonSyntaxException;

/**
 * 解析fgid
 *
 * @author  <br/>
 *         create at 2015-11-28 下午05:07:15
 */
public class QueryFgidParser implements IApiResultParseable<String> {
    private static final String TAG = "QueryFgidParser";

    @Override
    public String parse(HttpResponse response) throws JSONException, RemoteException, IOException {
        final QueryFgidResponse queryFgidResponse;
        try {
            String content = response.getContent();
            DuboxLog.d(TAG, "content = " + content);
            queryFgidResponse = new Gson().fromJson(content, QueryFgidResponse.class);
            if (queryFgidResponse != null) {
                queryFgidResponse.setRequestUrl(response.getUrl());
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

        DuboxLog.d(TAG, "queryFgidResponse:" + queryFgidResponse);

        if (queryFgidResponse == null) {
            throw new JSONException(TAG + " JsonParser is null.");
        }

        if (queryFgidResponse.getErrorNo() != 0) {
            throw BaseServiceHelper.buildRemoteException(queryFgidResponse.getErrorNo(), null, queryFgidResponse);
        }

        return queryFgidResponse.fgid;
    }
}
