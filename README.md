# MixFlipTool

@酷安：[KuaKuaC](https://www.coolapk.com/u/9521915)

免root一键配置Mix Flip外屏 添加和使用任意应用 + 系统应用恢复默认样式

## 目录
- [功能效果展示](#功能效果展示)
   - [外屏添加和使用任意应用](#外屏添加和使用任意应用)
   - [自定义应用在外屏的缩放比例，可恢复系统应用在外屏的默认样式](#自定义应用在外屏的缩放比例可恢复系统应用在外屏的默认样式)
- [操作步骤](#操作步骤)
   - [授权（安装后只需执行一次）](#授权安装后只需执行一次)
   - [一键配置](#一键配置)
   - [刷新设置里的外屏应用列表](#刷新设置里的外屏应用列表)
   - [自定义应用在外屏的缩放比例](#自定义应用在外屏的缩放比例)
- [常见问题](#常见问题)
- [发布日志](#发布日志)

## 功能效果展示

### 外屏添加和使用任意应用

| 设置 | 文件管理 | 小米应用商店 |
|-----|-----|-----|
| <img src="/imgs/settings.jpg" width="100%" height="100%" alt="设置"> | <img src="/imgs/filemanager.jpg" width="100%" height="100%" alt="文件管理"> | <img src="/imgs/xiaomi_market.jpg" width="100%" height="100%" alt="小米应用商店"> |

| 小米社区 | 酷安 | 斗鱼 |
|-----|-----|-----|
| <img src="/imgs/mi_community.jpg" width="100%" height="100%" alt="小米社区"> | <img src="/imgs/coolapk.jpg" width="100%" height="100%" alt="酷安"> | <img src="/imgs/douyu.jpg" width="100%" height="100%" alt="斗鱼"> |

### 自定义应用在外屏的缩放比例，可恢复系统应用在外屏的默认样式

| 电话（恢复联系人） | 相册（恢复发送、编辑等）和短信（字体大小更合适） | 日历（恢复农历、节假日等） |
|-----|-----|-----|
| <img src="/imgs/phone.jpg" width="100%" height="100%" alt="电话"> | <img src="/imgs/photo_message.jpg" width="100%" height="100%" alt="相册和短信"> | <img src="/imgs/calendar.jpg" width="100%" height="100%" alt="日历"> |

| 计算器（恢复各种换算） | 录音机（恢复各种功能） |
|-----|-----|
| <img src="/imgs/calculator.jpg" width="100%" height="100%" alt="计算器"> | <img src="/imgs/recorder.jpg" width="100%" height="100%" alt="录音机"> |

## 操作步骤

### 授权（安装后只需执行一次）

相关功能依赖于DUMP权限，因此在安装后，需要向应用进行一次授权，方法有2种：

1. 通过ADB授权，手机开启USB调试和USB调试（安全设置）后，连接电脑，命令行里执行

```
adb shell pm grant com.parallelc.mixfliptool android.permission.DUMP 
```

2. 通过[Shizuku](https://github.com/RikkaApps/Shizuku)授权，启动Shizuku后，点击MixFlipTool，会出现弹窗，选择`始终允许`

<img src="/imgs/shizuku.jpg" width="33%" alt="Shizuku">

### 一键配置

点击MixFlipTool，就会进行配置，配置成功会有提示，如果出现获取应用信息的弹窗，选择 允许（第二行或第三行）

<p>
   <img src="/imgs/read_app_list.jpg" width="30%" alt="获取应用信息">
   <img src="/imgs/one_click_config.jpg" width="30%" alt="配置成功">
</p>

注意：

- 一键配置仅会对当前已安装的应用生效，且手机重启后就会失效，因此每次重启或安装新应用后，需要重新执行一键配置


- 一键配置之前已经打开的应用，需要关闭后才能在外屏使用，因此建议一键配置成功后，执行一下后台一键清理

### 刷新设置里的外屏应用列表

一键配置成功后，需要重启`外屏桌面`，才能使`设置-外屏-外屏应用`里出现最新配置的应用，重启方法：

进入`设置-应用设置`，右上角三个点选择`显示所有应用`，找到`外屏桌面`，点击`结束运行`

### 自定义应用在外屏的缩放比例

注意：

- 此功能并不适用于外屏全屏显示的应用，如`天气`


- 对`时钟`进行缩放比例设置，会导致闹钟在锁屏状态下不响

在内屏长按MixFlipTool，会出现`缩放比例设置`的菜单，打开后会显示当前的设置

默认对系统应用进行了一些设置，用于恢复系统应用在外屏的默认样式，可点击应用进行调整设置，设置完成后需要再回到桌面点击MixFlipTool，进行一键配置

<p>
   <img src="/imgs/scale_setting_menu.jpg" width="30%" alt="缩放比例设置-菜单">
   <img src="/imgs/scale_setting.jpg" width="30%" alt="缩放比例设置">
   <img src="/imgs/scale_setting_app.jpg" width="30%" alt="缩放比例设置-应用">
</p>

## 常见问题

### 外屏设置的应用列表不全

- 尝试重新执行`一键配置`和`刷新设置里的外屏应用列表`


- 个别应用确实无法出现在外屏应用列表里，如`Google`，可通过安装一个第三方启动器应用，间接在外屏启动

### 对米家、网易云设置缩放比例，无法恢复默认样式

米家、网易云的外屏样式，不是通过检测缩放比例来展示的，而是`外屏桌面`专门调用了它们特殊的启动页

如果不想进入它们的外屏启动页，可使用旧版本或通过第三方启动器应用启动

## 发布日志

### 1.1.2

[Github Releases page](https://github.com/parallelcc/MixFlipTool/releases)

### 1.1 & 1.1.1

https://www.coolapk.com/feed/58907782

### 1.0

https://www.coolapk.com/feed/58683936
