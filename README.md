# 应用宝蓝牙手柄 B 键返回修复 - LSPosed / Xposed 模块

> 解决蓝牙手柄在应用宝（腾讯应用宝 `com.tencent.android.qqdownloader`）中 B 键误触发返回的问题
> 通过 Xposed Hook 全局拦截 `KEYCODE_BACK`，打开应用宝即生效

[![Build](https://github.com/loser1727/QQDownloaderHook/actions/workflows/build.yml/badge.svg)](https://github.com/loser1727/QQDownloaderHook/actions)
[![Release](https://img.shields.io/github/v/release/loser1727/QQDownloaderHook)](https://github.com/loser1727/QQDownloaderHook/releases)

## 适用场景

- 使用蓝牙手柄游玩应用宝内的游戏（云游戏、手游等）
- 手柄 B 键本应映射游戏内操作，却被应用宝拦截为"返回"
- Android 5.0+，已安装 LSPosed / EdXposed 框架

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

### GitHub Actions（自动）

推送代码或发布 tag 后自动构建并创建 Release。

### 本地

```bash
./gradlew assembleDebug
# APK: app/build/outputs/apk/debug/app-debug.apk
```

## 调试

```bash
adb logcat -s "QQHook"
```

期望日志：
```
[QQHook] 已注入应用宝
[QQHook] Hook dispatchKeyEvent 成功
[QQHook] Hook onKeyDown 成功
[QQHook] dispatchKeyEvent 拦截 KEYCODE_BACK
```

## 工程结构

```
├── build.gradle
├── gradlew / gradlew.bat
├── .github/workflows/build.yml
└── app/src/main/
    ├── AndroidManifest.xml
    ├── assets/
    │   ├── java_init.list
    │   ├── xposed_init
    │   └── module.prop
    ├── java/.../MainHook.java
    └── res/xml/module_scope.xml
```
