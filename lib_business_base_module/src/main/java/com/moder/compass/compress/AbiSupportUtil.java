package com.moder.compass.compress;

import android.os.Build;

/**
 * Created by wanghelong on 2019/3/19.<br/>
 * Email: wanghelong
 */
public class AbiSupportUtil {

    public static final String ARMEABI_V7A = "armeabi-v7a";
    public static final String ARM64_V8A = "arm64-v8a";
    public static final String ARMEABI = "armeabi";


    /**
     * 判断是否支持传入的cpu abi类型
     *
     * @param abi 要判断的类型
     */
    public static boolean isSupportAbi(String abi) {
        if (abi == null) {
            return false;
        }
        String[] supportAbis = getSupportAbi();
        for (String e : supportAbis) {
            if (abi.equals(e)) {
                return true;
            }
        }
        return false;
    }

    /**
     * 获取abi列表
     * @returnm abi列表
     */
    public static String[] getSupportAbi() {
        String[] supportAbis;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
            supportAbis = Build.SUPPORTED_ABIS;
        } else {
            supportAbis = new String[]{Build.CPU_ABI, Build.CPU_ABI2};
        }
        return supportAbis;
    }

    /**
     * 获取abi列表的名称
     */
    public static String getSupportAbiString() {
        StringBuilder stringBuilder = new StringBuilder();
        String[] supportAbis = getSupportAbi();
        for (String abi : supportAbis) {
            stringBuilder.append(abi).append(" ");
        }
        return stringBuilder.toString();
    }

}
