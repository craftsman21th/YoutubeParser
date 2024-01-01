
package com.moder.compass.transfer.base;
import com.dubox.drive.cloudfile.base.IDownloadable;
/**
 * Created by liuliangping on 2015/1/30.
 */
public interface IFileInfoGenerator {
    FileInfo generate(IDownloadable downloadable);
}
