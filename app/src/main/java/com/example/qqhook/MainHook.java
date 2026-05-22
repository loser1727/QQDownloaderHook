package com.example.qqhook;

import android.view.KeyEvent;

import de.robv.android.xposed.IXposedHookLoadPackage;
import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XposedBridge;
import de.robv.android.xposed.XposedHelpers;
import de.robv.android.xposed.callbacks.XC_LoadPackage;

/**
 * LSPosed 模块主入口
 * 目标：只要应用宝 (com.tencent.android.qqdownloader) 运行，
 *       全局拦截 KEYCODE_BACK 事件，不做界面判断。
 *
 * 原理：
 *   - 手柄 B 键发出 KEYCODE_BACK
 *   - Hook Activity.dispatchKeyEvent() 和 onKeyDown()
 *   - 注入到应用宝后，应用内任何界面按 B 键均不触发返回
 */
public class MainHook implements IXposedHookLoadPackage {

    private static final String TARGET_PACKAGE = "com.tencent.android.qqdownloader";

    @Override
    public void handleLoadPackage(XC_LoadPackage.LoadPackageParam lpparam) throws Throwable {
        if (!TARGET_PACKAGE.equals(lpparam.packageName)) {
            return;
        }

        XposedBridge.log("[QQHook] 已注入应用宝: " + TARGET_PACKAGE);

        hookDispatchKeyEvent(lpparam.classLoader);
        hookOnKeyDown(lpparam.classLoader);
    }

    // -------------------------------------------------------------------------
    // Hook dispatchKeyEvent —— 最早拦截点
    // -------------------------------------------------------------------------
    private void hookDispatchKeyEvent(ClassLoader classLoader) {
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

                        if (event.getKeyCode() == KeyEvent.KEYCODE_BACK) {
                            XposedBridge.log("[QQHook] dispatchKeyEvent 拦截 KEYCODE_BACK, Activity="
                                    + param.thisObject.getClass().getName());
                            // 返回 true = 事件已消费，不再向下传递
                            param.setResult(true);
                        }
                    }
                }
            );
            XposedBridge.log("[QQHook] Hook dispatchKeyEvent 成功");
        } catch (Throwable e) {
            XposedBridge.log("[QQHook] Hook dispatchKeyEvent 失败: " + e.getMessage());
        }
    }

    // -------------------------------------------------------------------------
    // Hook onKeyDown —— 兜底
    // -------------------------------------------------------------------------
    private void hookOnKeyDown(ClassLoader classLoader) {
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
                            XposedBridge.log("[QQHook] onKeyDown 拦截 KEYCODE_BACK, Activity="
                                    + param.thisObject.getClass().getName());
                            param.setResult(true);
                        }
                    }
                }
            );
            XposedBridge.log("[QQHook] Hook onKeyDown 成功");
        } catch (Throwable e) {
            XposedBridge.log("[QQHook] Hook onKeyDown 失败: " + e.getMessage());
        }
    }
}
