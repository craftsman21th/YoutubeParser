package com.moder.compass.util

import com.dubox.drive.cloudfile.utils.FileType
import java.util.regex.Pattern

public fun FileType.isImage2(name: String): Boolean {
    return Pattern.compile(
        "\\b\\.( png|jpeg|jpg|gif|bmp|cur|svg|svgz|tif|tiff|ico|jpe|webp|heic|heif|avci|livp)\\b"
    ).matcher(name).find()
}