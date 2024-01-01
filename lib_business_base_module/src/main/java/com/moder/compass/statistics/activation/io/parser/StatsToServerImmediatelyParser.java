package com.moder.compass.statistics.activation.io.parser;

import java.io.IOException;

import org.json.JSONException;

import com.dubox.drive.kernel.architecture.debug.DuboxLog;
import com.dubox.drive.kernel.architecture.net.HttpResponse;
import com.dubox.drive.network.base.Response;
import com.dubox.drive.kernel.architecture.net.exception.RemoteException;
import com.dubox.drive.kernel.architecture.net.parser.IApiResultParseable;
import com.google.gson.Gson;
import com.google.gson.JsonIOException;
import com.google.gson.JsonParseException;
import com.google.gson.JsonSyntaxException;

/**
 * Created by 魏铮铮 on 16/7/29.
 */
public class StatsToServerImmediatelyParser implements IApiResultParseable<Boolean> {
    private static final String TAG = "StatsToServerImmediatelyParser";

    /**
     * @param response
     * @return
     * @throws JSONException
     * @throws RemoteException
     * @throws IOException
     */
    @Override
    public Boolean parse(HttpResponse response) throws JSONException, RemoteException, IOException {
        Response result = null;

        try {
            String content = response.getContent();
            DuboxLog.d(TAG, "content = " + content);
            result = new Gson().fromJson(content, Response.class);
        } catch (JsonSyntaxException e) {
            throw new JSONException(e.getMessage());
        } catch (JsonIOException e) {
            throw new IOException(e.getMessage());
        } catch (JsonParseException e) {
            throw new JSONException(e.getMessage());
        } catch (IllegalArgumentException e) {
            throw new JSONException(e.getMessage());
        }

        DuboxLog.d(TAG, "StatsToServerImmediatelyResponse:" + result);
        if (result == null) {
            throw new JSONException(TAG + " JsonParser is null.");
        }
        if (result.getErrorNo() != 0) {
            return false;
        }

        return true;
    }
}
