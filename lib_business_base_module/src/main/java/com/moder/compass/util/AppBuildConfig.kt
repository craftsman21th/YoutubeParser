/*
 * Copyright (C) 2021 moder, Inc. All Rights Reserved.
 */
package com.moder.compass.util

import android.content.Context
import java.lang.reflect.Field

/**
 *
 */
fun getBuildConfigValue(context: Context, fieldName: String): Any? {
    try {
        val clazz = Class.forName("com.tube.box.BuildConfig")
        val field: Field = clazz.getField(fieldName)
        return field.get(null)
    } catch (e: ClassNotFoundException) {
        e.printStackTrace()
    } catch (e: NoSuchFieldException) {
        e.printStackTrace()
    } catch (e: IllegalAccessException) {
        e.printStackTrace()
    }
    return null
}