package com.llm.accessibilityservicedemo.util;

import android.app.KeyguardManager;
import android.content.Context;
import android.os.PowerManager;

import static android.content.Context.KEYGUARD_SERVICE;

/**
 * Created by Longmin Li on 2018/9/18.
 * <p>
 * 手机控制相关工具类
 */

public class PhoneController {
    private final static String TAG = PhoneController.class.getSimpleName();

    /**
     * 判断是否锁屏
     *
     * @param context
     * @return
     */
    public static boolean isLockScreen(Context context) {
        KeyguardManager km = (KeyguardManager) context.getSystemService(KEYGUARD_SERVICE);
        return km.inKeyguardRestrictedInputMode();
    }

    /**
     * 唤醒手机屏幕并解锁
     */
    public static void wakeUpAndUnlock(Context context) {
        // 获取电源管理器对象
        PowerManager pm = (PowerManager) context
                .getSystemService(Context.POWER_SERVICE);
        boolean screenOn = pm.isScreenOn();
        if (!screenOn) {
            // 获取PowerManager.WakeLock对象,后面的参数|表示同时传入两个值,最后的是LogCat里用的Tag
            PowerManager.WakeLock wl = pm.newWakeLock(
                    PowerManager.ACQUIRE_CAUSES_WAKEUP |
                            PowerManager.SCREEN_BRIGHT_WAKE_LOCK, "bright");
            wl.acquire(10000); // 点亮屏幕
            wl.release(); // 释放
        }
        // 屏幕解锁
        KeyguardManager keyguardManager = (KeyguardManager) context
                .getSystemService(KEYGUARD_SERVICE);
        KeyguardManager.KeyguardLock keyguardLock = keyguardManager.newKeyguardLock("unLock");
        // 屏幕锁定
        keyguardLock.reenableKeyguard();
        keyguardLock.disableKeyguard(); // 解锁
    }
}
