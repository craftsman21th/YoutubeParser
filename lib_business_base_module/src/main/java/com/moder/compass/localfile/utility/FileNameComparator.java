/*
 * FileNameComparator.java
 * classes : com.dubox.drive.localfilesystem.utility.FileNameComparator
 * @author 文超
 * V 1.0.0
 * Create at 2014-8-20 上午11:43:56
 */
package com.moder.compass.localfile.utility;

import java.io.File;
import java.text.Collator;
import java.util.Comparator;

import com.moder.compass.localfile.DownloadPath;

/**
 * com.dubox.drive.localfilesystem.utility.FileNameComparator
 *
 * @author 文超 <br/>
 *         create at 2014-8-20 上午11:43:56
 */
public class FileNameComparator implements Comparator<File> {
    public Collator cmp;

    public FileNameComparator() {
        cmp = Collator.getInstance(java.util.Locale.CHINA);
    }

    @Override
    public int compare(File file1, File file2) {
        String duboxDubox = DownloadPath.getDefaultDownloadDirName();
        String fileName1 = file1.getName();
        String fileName2 = file2.getName();
        if (fileName1.trim().equals(duboxDubox) && !fileName2.trim().equals(duboxDubox)) {
            return -1;
        } else if (!fileName1.trim().equals(duboxDubox) && fileName2.trim().equals(duboxDubox)) {
            return 1;
        } else {
            return cmp.compare(fileName1.toLowerCase(), fileName2.toLowerCase());
        }
    }
}
