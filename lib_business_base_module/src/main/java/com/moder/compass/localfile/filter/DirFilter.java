package com.moder.compass.localfile.filter;

import java.io.File;
import java.io.FileFilter;

/**
 * Created by LJ on 2015/3/6.
 */
public class DirFilter implements FileFilter {
    private static final String TAG = "DirFilter";

    /**
     * @param file
     *
     * @return
     *
     * @see java.io.FileFilter#accept(java.io.File)
     */
    @Override
    public boolean accept(File file) {
        if (file.isDirectory()) {
            return true;
        } else {
            return false;
        }
    }
}
