/*
 * QuotaParser.java
 * classes : com.dubox.drive.io.parser.filesystem.QuotaParser
 * @author 
 * V 1.0.0
 * Create at 2012-10-23 下午2:14:51
 */
package com.moder.compass.statistics.activation.io.parser;

import java.io.IOException;

import org.json.JSONException;

import com.dubox.drive.kernel.architecture.debug.DuboxLog;
import com.dubox.drive.network.base.Response;
import com.dubox.drive.base.service.BaseServiceHelper;
import com.dubox.drive.kernel.architecture.net.HttpResponse;
import com.dubox.drive.kernel.architecture.net.exception.RemoteException;
import com.dubox.drive.kernel.architecture.net.parser.IApiResultParseable;
import com.google.gson.Gson;
import com.google.gson.JsonIOException;
import com.google.gson.JsonParseException;
import com.google.gson.JsonSyntaxException;

/**
 * com.dubox.drive.io.parser.filesystem.QuotaParser
 * 
 * @author <a href="mailto:">李彬</a><br/>
 *         用户容量配额的实体解析器<br/>
 *         create at 2012-10-23 下午2:14:51
 */
public class AppActivateParser implements IApiResultParseable<Boolean> {
    private static final String TAG = "AppActivateParser";

    @Override
    public Boolean parse(HttpResponse response) throws JSONException, RemoteException, IOException {

        final Response activateResponse;
        try {
            activateResponse = new Gson().fromJson(response.getContent(), Response.class);
            if (activateResponse != null) {
                activateResponse.setRequestUrl(response.getUrl());
            }
            DuboxLog.d(TAG, "activateResponse " + activateResponse);
        } catch (JsonSyntaxException e) {
            throw new JSONException(e.getMessage());
        } catch (JsonIOException e) {
            throw new IOException(e.getMessage());
        } catch (JsonParseException e) {
            throw new JSONException(e.getMessage());
        } catch (IllegalArgumentException e) {
            throw new JSONException(e.getMessage());
        }

        if (activateResponse == null) {
            throw new JSONException(TAG + " JsonParser is null.");
        }

        DuboxLog.d(TAG, "ActivateResponse:" + activateResponse);

        if (activateResponse.getErrorNo() != 0) {
            throw BaseServiceHelper.buildRemoteException(activateResponse.getErrorNo(), null, activateResponse);
        }

        return true;
    }
}
