package com.moder.compass.component.base;

/**
 * Simple to Introduction
 *
 * 通用的全局变量定义
 * @Author: guoqiqin
 * @CreateDate: 2019/09/02
 */
public final class CommonConstant {

    /**
     * 从ServerBanAppealActivity迁移
     */
    public static final int REQUEST_CODE_DOUBT_HACKING_APPEAL_DELETE_FILE = 351;
    /**
     * 从ServerBanAppealActivity迁移
     */
    public static final int REQUEST_CODE_DOUBT_HACKING_APPEAL_DELETE_RECYCLE_BIN_FILE = 352;
    /**
     * 从ServerBanAppealActivity迁移
     */
    public static final int REQUEST_CODE_DOUBT_HACKING_APPEAL_CLEAR_RECYCLE_BIN = 353;


    /**
     * 从MyNetdiskActivity迁移
     */
    public static final int RTN_CODE_PICK_DIRECTORY = 110;
    /**
     * 从HomeEntryFragment迁移
     * 传递创建文件夹目录变量
     */
    public static final  String CREATE_FOLDER_PATH = "create_folder_path";
    /**
     * 从NetdiskFileFragment迁移
     */
    public static final int DUBOXFILE_FRAGMENT_RTN_CODE_PICK_DIRECTORY = 101;

    /**
     * 从NetdiskFilePresenter迁移
     * 文件移动类型
     * 加入加密空间
     */
    public static final int MOVE_FILE_IN_SAFE_BOX = 1;
    /**
     * 从NetdiskFilePresenter迁移
     * 文件移动类型
     */
    public static final int MOVE_FILE_OUT_SAFE_BOX = 2;
    /**
     * 从NetdiskFilePresenter迁移
     * 文件移动类型
     * 加密空间内移动
     */
    public static final int MOVE_FILE_INSIDE_SAFE_BOX = 3;
    /**
     * 从NetdiskFilePresenter迁移
     * 文件移动类型
     */
    public static final int MOVE_FILE_INSIDE_DUBOX = 4;
    /**
     * 从MyNetdiskFragment迁移
     * 主界面刷新常量 YQH 20121030
     */
    public static final int MAIN_REFRESH = 1090;

    /**
     * 资源圈分享 RequestCode
     */
    public static final int SHARE_EMAIL_REQUEST_CODE = 201;
}
