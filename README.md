Sahara: 简单的垃圾短信拦截应用
==============================

**在Android 4.4下需要xposed KitKat SMS Patch才能工作**

**Android 5.x下无法工作**

**安装之前请务必阅读全部介绍**

## 免责声明
这个应用是我为了满足个人需要写的。满足个人需要之后就想也能为身边的朋友服务。为了方便分发才上传到Google Play. 我不会对使用该应用导致的任何损失承担任何责任。

## 简介
* 通过发信号码和关键词拦截涉嫌的垃圾短信
* 公共发信号码黑名单和关键词黑名单是自动更新的，会不断完善
* 来自联系人的短信豁免于关键词黑名单，不豁免于私人号码黑名单
* 可以自己加发信号码到黑名单 (但是删除黑名单里的号码的功能还没做...)
* 可以查看已经拦截下来短信

## TODO / 下一步
https://github.com/snow/sahara/issues

## 权限說明
### 读联系人
Sahara读取了所有联系人的电话号码，从而能够放过这些号码发来的短信。所以如果你特殊癖好，比如爱读[沃冲浪]，那把沃冲浪的号码添加为联系人好了。

### 监听短信(SMS, MMS)
不监听过滤个鸡巴呀！

### 读写外部存储
拦截下来的短信，以YAML格式存在了sdcard的`cc.firebloom.sahara`目录下。

### 读短信收件箱
Sahara可以读你的短信收件箱，然后让你从中选择要屏蔽号码。

### 监听网络状态改变
Sahara只是利用网络状态改变的回调来注册一天一次的更新黑名单的计划任务而已。详见 https://github.com/snow/sahara/blob/master/src/cc/firebloom/sahara/ConnectivityChangeReceiver.java

## 我为啥要相信你?
源代码在 https://github.com/snow/sahara ，自己读，自己编译自己装

## 其它
* 用了 org.damazio.notifier.event.receivers.mms 这个第三方的开源包，其实是从AOSP里萃取出来的，Apache License, 在这里感谢原作者和萃取者。

## 下载
### 市场
https://play.google.com/store/apps/details?id=cc.firebloom.sahara

### 测试版
市场里的版本会更新比较慢，因为我投入在这个项目上的时间不多，而且把一个新功能做得差不多才会一次市场。
而开发中的版本为了给朋友帮测试，就会在 http://code.google.com/p/sahara/downloads/list 放apk。
感谢google提供空间带宽。/sigh

## 以防万一的又一份免责声明
简单地说就是如果你装了这个应用就代表你同意因为使用这个应用而造成的任何损失，我都不用负任何责任。
啊好懒，有空再弄正式的。
