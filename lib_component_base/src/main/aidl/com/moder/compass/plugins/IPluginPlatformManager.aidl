// IPluginPlatformManager.aidl
package com.moder.compass.plugins;
import com.moder.compass.plugins.IPluginPlatformCallback;

// Declare any non-default types here with import statements

interface IPluginPlatformManager {
    /**
     * Demonstrates some basic types that you can use as parameters
     * and return values in AIDL.
     */
    void call(int manager, int method, String params, IPluginPlatformCallback callback);

    String directCall(int manager, int method, String params);

}
