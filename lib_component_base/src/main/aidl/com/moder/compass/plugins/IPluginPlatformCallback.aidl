// IPluginPlatformCallback.aidl
package com.moder.compass.plugins;

// Declare any non-default types here with import statements

interface IPluginPlatformCallback {
    /**
     * Demonstrates some basic types that you can use as parameters
     * and return values in AIDL.
     */
    void onResult(String pluginId, int manager, int method, String params);
}
