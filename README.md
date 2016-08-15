# RobotCtrl - 南大电子服务型机器人端控制器

## 版本说明

### 版本规则：

RobotCtrl_vNUM1.NUM2.NUM3.NUM4_alpha（commit_id）

    1. 功能1添加NUM2++
    2. 功能2添加NUM2++
    1. BUG1修复NUM3++
    2. BUG2修复NUM3++

- NUM1代表大幅度框架升级
- NUM2代表新功能添加
- NUM3代表BUG修复个数
- NUM4代表发布月日
- commit_id代表发布时commit的id号

**注意首先填写功能添加列表和BUG修复列表，最后相应更新版本号。**

## 版本更新

1. RobotCtrl_v1.10.8.0811_alpha(5a356b2)
        1. 大幅度缩减功能以精益求精
        2. 左上1/4屏点击5次进入设置界面
        3. 右上1/4屏点击5次进入关于界面

2. RobotCtrl_v1.10.8.0812_alpha（1306930）
    1. 屏蔽音乐自动播放

3. RobotCtrl_v1.12.9.0812_alpha(5a97550)
    1. PC端指定视频文件进行播放
    2. PC端可进行单曲播放，单曲循环播放，从头多曲循环播放和从指定文件多曲循环播放

4. RobotCtrl_v1.13.9.0813_alpha(c862a13)
    1. 通过访问管理服务器，获得位置信息，并上报PC端

5. RobotCtrl_v1.15.9.0814_alpha
    1. 从FTP服务器下载文件（小size ok, 大size有问题）
    2. SettingActivity启动浏览器和南邮APK