/*
 * CheckUpgrade.java
 * classes : com.dubox.drive.io.parser.filesystem.CheckUpgrade
 * @author 
 * V 1.0.0
 * Create at 2012-10-22 上午11:55:57
 */
package com.moder.compass.versionupdate.io.parser;

import java.io.IOException;

import org.json.JSONException;

import com.dubox.drive.base.service.BaseServiceHelper;
import com.dubox.drive.kernel.architecture.debug.DuboxLog;
import com.dubox.drive.kernel.architecture.net.HttpResponse;
import com.dubox.drive.kernel.architecture.net.exception.RemoteException;
import com.dubox.drive.kernel.architecture.net.parser.IApiResultParseable;
import com.moder.compass.versionupdate.io.model.Version;
import com.google.gson.Gson;
import com.google.gson.JsonIOException;
import com.google.gson.JsonParseException;
import com.google.gson.JsonSyntaxException;

/**
 * VersionParser
 * 
 * @author <a href="mailto:">李彬</a> <br/>
 *         解析版本升级的响应<br/>
 *         create at 2012-10-22 上午11:55:57
 */
public class VersionParser implements IApiResultParseable<Version> {
    private static final String TAG = "VersionParser";

    @Override
    public Version parse(HttpResponse response) throws JSONException, RemoteException, IOException {

        final Version version;
        try {
            version = new Gson().fromJson(response.getContent(), Version.class);
            if (version != null) {
                version.setRequestUrl(response.getUrl());
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

        DuboxLog.d(TAG, "version:" + version);

        if (version == null) {
            throw new JSONException(TAG + " JsonParser is null.");
        }

        if (version.getErrorNo() != 0) {
            throw BaseServiceHelper.buildRemoteException(version.getErrorNo(), null, version);
        }

        return version;
    }
}
