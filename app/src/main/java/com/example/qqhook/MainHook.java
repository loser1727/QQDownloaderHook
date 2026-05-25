package com.example.qqhook;

import android.app.Activity;
import android.view.KeyEvent;

import de.robv.android.xposed.IXposedHookLoadPackage;
import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XposedBridge;
import de.robv.android.xposed.XposedHelpers;
import de.robv.android.xposed.callbacks.XC_LoadPackage;

/**
 * LSPosed 模块 - 应用宝蓝牙手柄 B 键返回修复
 *
 * 目标：拦截应用宝内 KEYCODE_BACK，阻止手柄 B 键触发返回。
 *
 * 安全策略：
 * - 通过 lpparam.classLoader 严格限定 Hook 作用域在应用宝进程
 * - 不在热路径中写日志，零 I/O 零 IPC
 */
public class MainHook implements IXposedHookLoadPackage {

    private static final String TARGET_PACKAGE = "com.tencent.android.qqdownloader";

    @Override
    public void handleLoadPackage(XC_LoadPackage.LoadPackageParam lpparam) {
        if (!TARGET_PACKAGE.equals(lpparam.packageName)) {
            return;
        }

        XposedBridge.log("[QQHook] 注入应用宝 v1.1");

        // 用目标进程的 ClassLoader 加载 Activity ——
        // 注意：必须用 loadClass 而非 "android.app.Activity" 字符串，
        // 防止 LSPosed 回退到 boot classloader 做出全局 Hook
        try {
            Class<?> activityClass = lpparam.classLoader.loadClass("android.app.Activity");

            hookDispatchKeyEvent(activityClass);
            hookOnKeyDown(activityClass);

            XposedBridge.log("[QQHook] Hook 安装完成");
        } catch (Throwable e) {
            XposedBridge.log("[QQHook] 安装失败: " + e.getMessage());
        }
    }

    // -------------------------------------------------------------------------
    // dispatchKeyEvent —— 最早拦截，零 I/O 热路径
    // -------------------------------------------------------------------------
    private void hookDispatchKeyEvent(Class<?> activityClass) {
        XposedHelpers.findAndHookMethod(
            activityClass,
            "dispatchKeyEvent",
            KeyEvent.class,
            new XC_MethodHook() {
                @Override
                protected void beforeHookedMethod(MethodHookParam param) {
                    KeyEvent event = (KeyEvent) param.args[0];
                    if (event != null && event.getKeyCode() == KeyEvent.KEYCODE_BACK) {
                        param.setResult(true);
                    }
                }
            }
        );
    }

    // -------------------------------------------------------------------------
    // onKeyDown —— 兜底
    // -------------------------------------------------------------------------
    private void hookOnKeyDown(Class<?> activityClass) {
        XposedHelpers.findAndHookMethod(
            activityClass,
            "onKeyDown",
            int.class,
            KeyEvent.class,
            new XC_MethodHook() {
                @Override
                protected void beforeHookedMethod(MethodHookParam param) {
                    if ((int) param.args[0] == KeyEvent.KEYCODE_BACK) {
                        param.setResult(true);
                    }
                }
            }
        );
    }
}
