# CompressPng

# 概述
平时开发遇到一些问题，比如
* `怎么确认引入的第三方so是否适配32/64位`
* `怎么知道第三方库申请了那些权限`
* `每次开发引入的图片怎么一键压缩并自动引入项目内，并且每次使用不会重复压缩`
* `每次写项目都会创建shape,而这个shape很可能同事写过我不知道又写了一遍，很可能一个项目中存在很多重复的shape，只是命名不同，该如何找到这些重复的shape`

针对上方问题，再结合gradle插件开发了一个插件工具，可以解决这些问题，在这里分享出来，希望可以帮助到更多人
# 使用

## 引入库

* 在根目录的`build.gradle` 引入

```gradle
buildscript {
    repositories {
        google()
        mavenCentral()
    }
    dependencies {
        classpath "com.android.tools.build:gradle:4.2.2"
        //引入这个库
        classpath "io.githunb.renxh4:compress_png:0.0.5"
    }
}
```

* 在app的`build.gradle`引入插件

```
apply plugin: 'com.ren.xh.plugin'

xh{
    tinifyId = "wQdHz6dkJhtSCt0sHwCmvQh5lpSMcyL1"
}
```
引入插件后，需要配置`tinifyId`,因为这里使用的是https://tinify.cn/ 进行压缩的图片，需要到这个网站申请一个开发者id，仅需要俩步就可以轻松申请

![image.png](https://p1-juejin.byteimg.com/tos-cn-i-k3u1fbpfcp/1996fb6f2f914d0b858c45a0949af768~tplv-k3u1fbpfcp-watermark.image?)


## 使用库

* 检测so适配

 命令行执行命令 `./gradlew checkso`
 

 ```java

 ./gradlew checkso

...

[mmm] ---------------[依赖产物开始] group=com.immomo.momomedia:x264encoder----------------
[mmm] so文件 = jni/arm64-v8a/libx264encoder.so   size = 424KB
[mmm] so文件 = jni/armeabi/libx264encoder.so   size = 477KB
[mmm] ---------------[依赖产物结束] group=com.immomo.momomedia:x264encoder----------------
[mmm] ---------------[依赖产物开始] group=com.immomo.momomedia:voaac----------------
[mmm] so文件 = jni/arm64-v8a/libVoAACEncoder.so   size = 74KB
[mmm] so文件 = jni/armeabi/libVoAACEncoder.so   size = 78KB
[mmm] so文件 = jni/armeabi-v7a/libVoAACEncoder.so   size = 78KB
[mmm] ---------------[依赖产物结束] group=com.immomo.momomedia:voaac----------------

...

[mmm] v8a不包含 = libbsdiff.so   group=com.immomo.android.mklibrary:mk   size = 39KB
[mmm] v8a不包含 = libmkjni.so   group=com.immomo.android.mklibrary:mk   size = 7KB
[mmm] v8a不包含 = libsevenz.so   group=com.immomo.android.mklibrary:mk   size = 33KB
[mmm] v8a不包含 = libMOMOPitchShift.so   group=com.immomo.momomedia:mmaudio   size = 76KB
[mmm] v8a不包含 = libMOMOPitchShift.so   group=com.immomo.momomedia:mmaudio   size = 76KB
[mmm] v8a不包含 = libmjni.so   group=MatchMakerAndroid:momsecurity   size = 62KB

 ```
 首先会找出所有依赖三方的so，并列出名字和大小，然后判断对应文件夹是否是适配，目前判断的文件夹是`32指得是armeabi  64指的是arm64-v8a`,如果有需要判断其他文件夹可以下载源码自己改一下
 
 * 检测三方库权限

命令行执行 `./gradlew checkm`

```java

./gradlew checkm

[mmm] root 不包含权限 key = {group=com.huawei.hms:availableupdate}  权限 = {.GET_COMMON_DATA}
[mmm] root 不包含权限 key = {group=com.immomo.cosmos.photon:thirdpush-xiaomi}  权限 = {.MIPUSH_RECEIVE}
[mmm] root 不包含权限 key = {group=com.cosmos.radar:core}  权限 = {.FOREGROUND_SERVICE}
[mmm] root 不包含权限 key = {group=:SecurityGuardSDK-external-release-5.5.15071059}  权限 = {.WRITE_SETTINGS}
[mmm] root 不包含权限 key = {group=:SecurityGuardSDK-external-release-5.5.15071059}  权限 = {.READ_SETTINGS}
[mmm] root 不包含权限 key = {group=com.immomo.cosmos.auth:auth-cucc}  权限 = {.SYSTEM_ALERT_WINDOW}
[mmm] root 不包含权限 key = {group=com.immomo.cosmos.auth:auth-ctcc}  权限 = {.WRITE_SETTINGS}
[mmm] root 不包含权限 key = {group=com.huawei.hms:push}  权限 = {.PROCESS_PUSH_MSG}
[mmm] root 不包含权限 key = {group=com.huawei.hms:push}  权限 = {.PUSH_PROVIDER}
[mmm] root 不包含权限 key = {group=com.huawei.hms:push}  权限 = {.FOREGROUND_SERVICE}
[mmm] root 不包含权限 key = {group=com.liulishuo.filedownloader:library}  权限 = {.FOREGROUND_SERVICE}
[mmm] root 不包含权限 key = {group=com.immomo.android.mmhttp:mmhttp}  权限 = {.MOUNT_UNMOUNT_FILESYSTEMS}
[mmm] root 不包含权限 key = {group=com.immomo.cosmos.photon:thirdpush-oppo}  权限 = {.RECIEVE_MCS_MESSAGE}
[mmm] root 不包含权限 key = {group=com.immomo.cosmos.photon:thirdpush-oppo}  权限 = {.RECIEVE_MCS_MESSAGE}
[mmm] root 不包含权限 key = {group=com.huawei.hms:update}  权限 = {.GET_COMMON_DATA}

```
这个会检测，三方库包含那些，自己项目没有权限

* 检测res中重复文件（不止是shape），压缩图片

执行命令 `./gradlew checkres`

### （1） 自动压缩大于100k的图片

```java
...

[mmm] ""/app/src/main/res/drawable-xxhdpi/pay_success_bg.png  size = 262KB
[mmm] 压缩前 size =  262
[mmm] 压缩后 size =  56
[mmm] copy 完成
[mmm] ""/app/src/main/res/drawable-xxhdpi/bg_qr_share.png  size = 582KB
[mmm] 压缩前 size =  582
[mmm] 压缩后 size =  155
[mmm] copy 完成
[mmm] ""/app/src/main/res/drawable-xxhdpi/bg_every_day_pick.png  size = 119KB
[mmm] 压缩前 size =  119
[mmm] 压缩后 size =  32
[mmm] copy 完成
[mmm] ""/app/src/main/res/drawable-xxhdpi/bg_secretary_dialog.png  size = 216KB
[mmm] 压缩前 size =  216
[mmm] 压缩后 size =  43
[mmm] copy 完成
[mmm] 此次共压缩 {7706}

```

可以看到压缩的很客观，直接压缩了将近8m的图片，只需要执行一次，会自动压缩，并把压缩的图片自动替换到项目内，很方便，而且记录已经压缩过的图片，不会重复压缩

### （2） 检测是否有重复资源（比如多个shape.xml文件名字不同，但是内容相同）

```java

...

[mmm] 重复文件
[mmm] ""/app/src/main/res/drawable/bg_10dp_chat_white.xml
[mmm] ""/app/src/main/res/drawable/bg_luck_telephone_part2.xml
[mmm] 重复文件
[mmm] ""/app/src/main/res/drawable-xhdpi/random_match_avatar_12.png
[mmm] ""/app/src/main/res/drawable-xhdpi/random_match_avatar_15.png
[mmm] 重复文件
[mmm] ""/app/src/main/res/drawable/bg_dialog_bublegumpink.xml
[mmm] ""/app/src/main/res/drawable/bg_dialog_negtive_two.xml
[mmm] 重复文件
[mmm] ""/app/src/main/res/drawable/bg_button_redcommon.xml
[mmm] ""/app/src/main/res/drawable/bg_button_enable.xml
[mmm] 重复文件
[mmm] ""/app/src/main/res/drawable/bg_fe377f_radius_29dp.xml
[mmm] ""/app/src/main/res/drawable/bg_29_shape_fe377f.xml
[mmm] 重复文件
[mmm] ""/app/src/main/res/drawable-xxhdpi/single_chat_input_ic_audio_unlock.png
[mmm] ""/app/src/main/res/drawable-xxhdpi/single_chat_input_ic_audio.png
[mmm] 此次共找到重复文件 {15}

```

这个不仅可以找到重复的`shape`文件，还可以找到重复的`png`，重复的`xml,layout`等文件，其实就是找到的`res`中的重复文件


# 原理

## 检测so适配

```gradle
  project.task("checkso").doFirst {
            Configuration bb= project.configurations.getByName("implementation")
            bb.canBeResolved=true
            project.gradle.addListener(new EmbedResolutionListener(project, bb))
            def set  = ResolveUtils.resolveArtifacts(bb)
            processArtifacts(set)
        }
```
解析`implementation`引用的依赖，然后解析依赖文件，拿到so文件，并判断大小及是否适配32/64位

## 检测三方库权限

其实和so原理一样，解析`implementation`引用的依赖，然后解析依赖文件，拿到`AndroidManifest.xml`文件，解析xml，拿到权限并和主项目权限进行判断


## 检测res中重复文件（不止是shape），压缩图片


（1）其原理就是拿到项目的资源文件，首先判断png图片大小，大于100k 进行压缩，用的是Tinify 进行压缩数据
（2）拿到资源文件后，对每个文件进行MD5储存，如果有相同的MD5说明有相同文件（如果xml文件内容相同，但是换行，空格，也可以准确判断是重复文件）

# 最后

## 开源地址，如果有帮助希望给些start
https://github.com/renxh4/CompressPng

