/*
 * BucketColumns.java
 * classes : com.dubox.drive.localfilesystem.p2plocalfile.interfacepkg.BucketColumns
 * @author 文超
 * V 1.0.0
 * Create at 2014-8-4 下午3:03:53
 */
package com.moder.compass.localfile;

import android.provider.BaseColumns;

/**
 * com.dubox.drive.localfilesystem.p2plocalfile.interfacepkg.BucketColumns
 *
 * @author 文超 <br/>
 *         create at 2014-8-4 下午3:03:53
 */
public interface BucketColumns extends BaseColumns {

    String BUCKET_ID = "BUCKET_ID";
    String NAME = "NAME";
    String COVER_PHOTO_ID_0 = "COVER_PHOTO_ID_0";
    String COVER_PHOTO_PATH_0 = "COVER_PHOTO_PATH_0";
    String COVER_PHOTO_ID_1 = "COVER_PHOTO_ID_1";
    String COVER_PHOTO_PATH_1 = "COVER_PHOTO_PATH_1";
    String COVER_PHOTO_ID_2 = "COVER_PHOTO_ID_2";
    String COVER_PHOTO_PATH_2 = "COVER_PHOTO_PATH_2";
    String COVER_PHOTO_ID_3 = "COVER_PHOTO_ID_3";
    String COVER_PHOTO_PATH_3 = "COVER_PHOTO_PATH_3";

    String[] PROJECTION = { _ID, BUCKET_ID, NAME, _COUNT, COVER_PHOTO_ID_0, COVER_PHOTO_PATH_0, COVER_PHOTO_ID_1,
            COVER_PHOTO_PATH_1, COVER_PHOTO_ID_2, COVER_PHOTO_PATH_2, COVER_PHOTO_ID_3, COVER_PHOTO_PATH_3 };
}
