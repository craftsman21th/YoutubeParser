package com.mars.united.international.webplayer.account.model

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

/**
 * Cookie
 * @property name String
 * @property value String
 * @constructor
 */
@Parcelize
data class Cookie(
    val name: String,
    val value: String
) : Parcelable