# 应用宝手柄修复 - LSPosed 模块

拦截 `com.tencent.android.qqdownloader`（应用宝）中  
蓝牙手柄 **B 键** 触发 `KEYCODE_BACK`（返回键）的问题。

**全局生效**：打开应用宝即生效，无需进入云游戏界面。

## 原理

```
手柄 B 键
   ↓ (KEYCODE_BACK)
[Android 输入系统]
   ↓
[Activity.dispatchKeyEvent()]  ← Hook 点1：返回 true，吃掉事件
   ↓（如未被拦截）
[Activity.onKeyDown()]         ← Hook 点2：兜底
   ↓（如未被拦截）
[应用宝返回逻辑]               ← 被吃掉，永不触发
```

## 下载

前往 [Releases](https://github.com/loser1727/QQDownloaderHook/releases) 下载最新 APK。

## 安装与激活

1. 安装 APK 到手机
2. 打开 **LSPosed Manager**
3. 在「模块」中找到 **「应用宝手柄修复」**，启用
4. 作用域勾选 `com.tencent.android.qqdownloader`（应用宝）
5. 强制停止并重启应用宝即可生效

## 编译

### GitHub Actions（自动，推荐）

推送代码 `git push` 后自动构建，产物在 [Actions](https://github.com/loser1727/QQDownloaderHook/actions) 页面下载。

### 本地

```bash
cd QQDownloaderHook
./gradlew assembleDebug
# APK: app/build/outputs/apk/debug/app-debug.apk
```

### Android Studio

打开项目目录，`Build → Build APK(s)`。

## 调试

```bash
adb logcat -s "QQHook"
```

期望日志：
```
[QQHook] 已注入应用宝: com.tencent.android.qqdownloader
[QQHook] Hook dispatchKeyEvent 成功
[QQHook] Hook onKeyDown 成功
# 按下 B 键时：
[QQHook] dispatchKeyEvent 拦截 KEYCODE_BACK
```

## 工程结构

```
QQDownloaderHook/
├── build.gradle
├── gradlew / gradlew.bat
├── gradle/wrapper/
├── .github/workflows/build.yml   # GitHub Actions 自动构建
└── app/
    ├── build.gradle
    └── src/main/
        ├── AndroidManifest.xml
        ├── assets/
        │   ├── java_init.list     # LSPosed Hook 入口
        │   ├── xposed_init        # EdXposed 兼容
        │   └── module.prop
        ├── java/.../MainHook.java # 核心 Hook 逻辑
        └── res/xml/module_scope.xml
```
