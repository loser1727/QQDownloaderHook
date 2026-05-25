package com.example.qqhook;

import android.view.KeyEvent;

import de.robv.android.xposed.IXposedHookLoadPackage;
import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XposedBridge;
import de.robv.android.xposed.XposedHelpers;
import de.robv.android.xposed.callbacks.XC_LoadPackage;

/**
 * LSPosed 模块主入口
 * 目标：应用宝 (com.tencent.android.qqdownloader) 运行时全局拦截 KEYCODE_BACK
 *
 * 原理：
 *   - 手柄 B 键发出 KEYCODE_BACK
 *   - Hook Activity.dispatchKeyEvent() + onKeyDown()
 *   - 仅拦截 BACK，其他按键零开销直通
 *
 * 性能：Hook 回调中不做任何 I/O（日志），仅做整数比较，延迟可忽略。
 */
public class MainHook implements IXposedHookLoadPackage {

    private static final String TARGET_PACKAGE = "com.tencent.android.qqdownloader";

    @Override
    public void handleLoadPackage(XC_LoadPackage.LoadPackageParam lpparam) throws Throwable {
        if (!TARGET_PACKAGE.equals(lpparam.packageName)) {
            return;
        }

        XposedBridge.log("[QQHook] 已注入应用宝 v1.0");

        boolean ok1 = hookDispatchKeyEvent(lpparam.classLoader);
        boolean ok2 = hookOnKeyDown(lpparam.classLoader);

        XposedBridge.log("[QQHook] dispatchKeyEvent=" + ok1 + " onKeyDown=" + ok2);
    }

    // -------------------------------------------------------------------------
    // Hook dispatchKeyEvent —— 最轻量级拦截
    // -------------------------------------------------------------------------
    private boolean hookDispatchKeyEvent(ClassLoader classLoader) {
        try {
            XposedHelpers.findAndHookMethod(
                "android.app.Activity",
                classLoader,
                "dispatchKeyEvent",
                KeyEvent.class,
                new XC_MethodHook() {
                    @Override
                    protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                        KeyEvent event = (KeyEvent) param.args[0];
                        if (event == null) return;

                        // 只拦截 BACK，其他键直接放行，不做任何额外操作
                        if (event.getKeyCode() == KeyEvent.KEYCODE_BACK) {
                            param.setResult(true);
                        }
                    }
                }
            );
            return true;
        } catch (Throwable e) {
            XposedBridge.log("[QQHook] dispatchKeyEvent Hook 失败: " + e.getMessage());
            return false;
        }
    }

    // -------------------------------------------------------------------------
    // Hook onKeyDown —— 兜底，同样零 I/O
    // -------------------------------------------------------------------------
    private boolean hookOnKeyDown(ClassLoader classLoader) {
        try {
            XposedHelpers.findAndHookMethod(
                "android.app.Activity",
                classLoader,
                "onKeyDown",
                int.class,
                KeyEvent.class,
                new XC_MethodHook() {
                    @Override
                    protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                        int keyCode = (int) param.args[0];

                        if (keyCode == KeyEvent.KEYCODE_BACK) {
                            param.setResult(true);
                        }
                    }
                }
            );
            return true;
        } catch (Throwable e) {
            XposedBridge.log("[QQHook] onKeyDown Hook 失败: " + e.getMessage());
            return false;
        }
    }
}
