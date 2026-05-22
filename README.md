# 应用宝云游戏手柄修复 - LSPosed 模块

## 功能
拦截 `com.tencent.android.qqdownloader`（应用宝）云游戏界面中  
蓝牙手柄 **B 键** 触发 `KEYCODE_BACK`（返回键）的问题。

## 原理
```
手柄 B 键
   ↓ (KEYCODE_BACK)
[Android 输入系统]
   ↓
[Activity.dispatchKeyEvent()]  ← Hook 点1：直接返回 true，吃掉事件
   ↓（如未被拦截）
[Activity.onKeyDown()]         ← Hook 点2：兜底
   ↓（如未被拦截）
[应用宝返回逻辑]               ← 被吃掉，永远不会执行到这里
```

## 工程结构
```
QQDownloaderHook/
├── build.gradle                          # 项目级
├── settings.gradle
└── app/
    ├── build.gradle                      # 模块级（含 XposedBridge 依赖）
    └── src/main/
        ├── AndroidManifest.xml           # LSPosed 模块声明
        ├── assets/
        │   └── META-INF/xposed/
        │       ├── java_init.list        # 入口类声明
        │       └── module.prop           # 模块元信息
        ├── java/com/example/qqhook/
        │   └── MainHook.java             # 核心 Hook 逻辑
        └── res/
            ├── values/strings.xml
            └── xml/module_scope.xml      # LSPosed 注入作用域
```

## 编译方式

### 用 Android Studio（推荐）
1. 打开 `QQDownloaderHook/` 目录
2. 等待 Gradle 同步
3. `Build → Build Bundle(s) / APK(s) → Build APK(s)`
4. APK 在 `app/build/outputs/apk/debug/` 或 `release/`

### 用命令行
```bash
cd QQDownloaderHook
./gradlew assembleDebug
# APK: app/build/outputs/apk/debug/app-debug.apk
```

## 安装与激活
1. 安装 APK 到手机
2. 打开 **LSPosed Manager**
3. 在「模块」中找到「应用宝云游戏手柄修复」，**启用**
4. 作用域勾选 `com.tencent.android.qqdownloader`
5. **重启手机**（或强杀并重启应用宝）

## 调试
查看模块日志：
```bash
adb logcat -s "QQHook"
# 或在 LSPosed Manager → 日志 中查看
```

期望日志：
```
[QQHook] 已注入应用宝: com.tencent.android.qqdownloader
[QQHook] Hook dispatchKeyEvent 成功
[QQHook] Hook onKeyDown 成功
# 按下 B 键时：
[QQHook] dispatchKeyEvent: 拦截 KEYCODE_BACK，当前 Activity=com.tencent...CloudGameActivity
```

## 调整云游戏 Activity 关键字
如果进入云游戏后 B 键仍然触发返回，说明类名关键字未匹配。  
查询实际类名：
```bash
adb shell dumpsys activity top | grep "ACTIVITY"
```
找到云游戏界面的类名，将关键片段加入 `MainHook.java` 的 `CLOUD_GAME_KEYWORDS` 数组。

或者临时开启全局拦截（注释掉关键字判断，改为 `return true`）先验证 Hook 是否生效。
