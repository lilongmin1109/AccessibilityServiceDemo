package com.llm.accessibilityservicedemo.util;

import android.app.KeyguardManager;
import android.content.Context;
import android.os.PowerManager;
import android.util.Log;

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
    /**
     * 点亮亮屏,点亮屏幕要求很高,不能有手势锁,密码锁,指纹锁,还不能有屏保
     */
    public static void unlock(Context context) {
        PowerManager mPowerManager = (PowerManager) context.getSystemService(Context.POWER_SERVICE);
        // 点亮亮屏
        PowerManager.WakeLock mWakeLock = mPowerManager.newWakeLock
                (PowerManager.ACQUIRE_CAUSES_WAKEUP | PowerManager.SCREEN_DIM_WAKE_LOCK, "Tag");
        Log.e("px", "mWakeLock is lock:" + mWakeLock.isHeld());
        mWakeLock.acquire();

    }
}
