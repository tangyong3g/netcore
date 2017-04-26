package com.net.core.unit;

import android.Manifest;
import android.annotation.TargetApi;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Build;

/**
 * Created by liwu.shu on 2016/7/12.
 */
class PermissionUtil {
    /**
     * 请求获取读取手机状态权限
     *
     * @return
     */
    @TargetApi(23)
    public static boolean requestPhonePermission(Context mc) {
        if (!needCheckPermission())
            return false;
        int hasPermission = mc.checkSelfPermission(Manifest.permission.READ_PHONE_STATE);
        if (hasPermission != PackageManager.PERMISSION_GRANTED) {
            return true;
        }
        return false;
    }

    /**
     * 检查是否需要权限检测
     *
     * @return
     */
    public static boolean needCheckPermission() {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            return false;
        } else {
            return true;
        }
    }

    public static boolean hasReadPhonePermission(Context mc) {
        if (!needCheckPermission())
            return true;

        int hasPermission = mc.checkSelfPermission(Manifest.permission.READ_PHONE_STATE);
        if (hasPermission != PackageManager.PERMISSION_GRANTED) {
            return false;
        }
        return true;
    }
}
