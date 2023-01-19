# Android Floating Watermark

[![](https://jitpack.io/v/nEdAy/Android-Floating-Watermark.svg)](https://jitpack.io/#nEdAy/Android-Floating-Watermark)  [![ktlint](https://img.shields.io/badge/code%20style-%E2%9D%A4-FF4081.svg)](https://ktlint.github.io/)

该项目 全部思想源自 [FloatingX](https://github.com/Petterpx/FloatingX)，只是针对 `全局水印` 这个特殊需求对 API 进行了删除与修改。

一个灵活且强大的 `免权限` 悬浮窗 `水印` 解决方案，支持 `点击事件穿透 ` 和 `可配置全局、局部、边界、黑/白名单页面等 `。

相较 `系统浮窗 `方案，该方案无需 `悬浮窗权限 `和 `点击穿透 Android 12 透明度要求`，相较 `Base 类添加水印布局 `方案，该方案无侵入性且覆盖全面（无源码部分页面也可全局覆盖到）。

[English Introduction](https://github.com/nEdAy/Android-Floating-Watermark/blob/main/README_EN.md)

[具体使用文档见这里](https://cskf7l0wab.feishu.cn/wiki/wikcnLLBCe3fIDUTAzrEg754tzc)


## 👏 特性

- ~~支持 **自定义隐藏显示动画**;（该功能已删除）~~
- ~~支持 **多指触摸**，精准决策触摸手势;（该功能已删除）~~
- 支持 自定义是否保存历史位置及还原;
- 支持 **越界回弹**，**边缘悬停**，**边界设置**;
- 支持 以 **layout**, **View**  的方式设置浮窗内容；
- 支持 自定义浮窗显示位置，**支持辅助定位**;
- 支持 **黑名单与白名单** 功能，指定页面禁止显示浮窗等;
- 支持 `kotlin` 构建扩展, 及对 `Java` 的友好兼容;
- 支持显示位置[强行修复],应对特殊机型(需要单独开启)
- 支持 **局部浮窗**，可在`ViewGroup` , `Fragment` , `Activity` 中进行显示；
- 完善的日志系统，打开即可看到不同级别的Fx运行过程,更利于发现问题


## 👨‍💻‍ 依赖方式

### 添加jitpack仓库

**build.gradle**

Gradle7.0 以下

```groovy
allprojects {
     repositories {
          // ...
          maven { url 'https://jitpack.io' }
     }
}
```

> AndroidStudio-Arctic Fox && Gradle7.0+,并且已经对依赖方式进行过调整，则可能需要添加到如下位置：
>
> **settings.gradle**
>
> ```groovy
> dependencyResolutionManagement {
> repositories {
> 
>      // ...
>      maven { url 'https://jitpack.io' }
>  }
> }
> ```

### Gradle

```groovy
dependencies {
     implementation 'com.github.nEdAy:Android-Floating-Watermark:1.0.0'
}
```


## 🏄‍♀️ 效果图



### 完善的日志-查看器

开启日志查看器，将看到Fx整个运行轨迹，更便于发现问题以及追踪解决。同时支持自定义日志tag

| App                                                          | Activity                                                     | ViewGroup                                                    |
| ------------------------------------------------------------ | ------------------------------------------------------------ | ------------------------------------------------------------ |
| ![image-20210808123000851](https://tva1.sinaimg.cn/large/008i3skNly1gtbk1ujkqfj31160s8444.jpg) | ![image-20210808123414921](https://tva1.sinaimg.cn/large/008i3skNly1gt99vralyqj313o0r4jwk.jpg) | ![image-20210808123553402](https://tva1.sinaimg.cn/large/008i3skNly1gt99xfpfwgj311y0jctc8.jpg) |


## 👨‍🔧‍ 使用方式

### 全局悬浮窗管理

**kt**

```kotlin
FloatingX.init {
        setLayout(R.layout.item_floating_new)
  	//只有调用了enableFx(),后续才会自动插入activity中
        enableFx()
}
```

**Java**

```java
AppHelper helper = AppHelper.builder()
        .setLayout(R.layout.item_floating)
	.enableFx()
        .build();
FloatingX.init(helper);
```

### 局部悬浮窗管理

#### 通用创建方式

**kt**

```kotlin
ScopeHelper.builder {
  setLayout(R.layout.item_floating)
}.toControl(activity)
```

**kt && java**

```kotlin
ScopeHelper.builder()
            .setLayout(R.layout.item_floating)
            .build()
            .toControl(activity)
            .toControl(fragment)
            .toControl(viewgroup)
```

#### 对kt的扩展支持

##### activity创建悬浮窗

```kotlin
private val scopeFx by createFx {
    setLayout(R.layout.item_floating)
    build().toControl(this/Activity)
}

```

##### fragment创建悬浮窗

```kotlin
private val activityFx by createFx {
    setLayout(R.layout.item_floating)
    build().toControl(this/Fragment)
}
```

##### viewGroup创建悬浮窗

```kotlin
private val activityFx by createFx {
    setLayout(R.layout.item_floating)
    build().toControl(this/Viewgroup)
}
```


## 🤔 技术实现

> **App** 级别悬浮窗 基于 `DecorView` 的的实现方案，全局持有一个单独的悬浮窗 `View` ,通过 `AppLifecycle` 监听 `Activity` 生命周期，并在相应时机 插入到 `DecorView` 上 ;
>
> **View** 级别悬浮窗，基于给定的 `ViewGroup` ;
>
> **Fragment** 级别，基于其对应的 `rootView` ;
>
> **Acrtivity** 级别,基于 `DecorView` 内部的 `R.id.content` ;

具体如下：

<img src="https://tva1.sinaimg.cn/large/008i3skNly1gr20ks7780j30rc0i5dim.jpg" alt="Activity-setContentView"  />

具体见我的博客：[源码分析 | Activity-setContentView](https://juejin.cn/post/6897453195342610445)

Ps: 为什么App级别悬浮窗 要插入到 `DecorView` ,而不是 **R.id.content** -> `FrameLayout` ?

> 插入到 `DecorView` 可以最大程度控制悬浮窗的自由度，即悬浮窗可以真正意义上[`全屏`]拖动。
>
> 插入到 `content` 中,其拖动范围其实为 **应用视图范围** ,即摆放位置 受到 **状态栏** 和 **底部导航栏** 以及 默认的 `AppBar` 影响, 比如当用户隐藏了状态栏或者导航栏，相对应的视图大小会发生改变，将影响悬浮窗的位置摆放。


## 👍 感谢

基础 **悬浮窗View** 的思想源自 [EnFloatingView](https://github.com/leotyndale/EnFloatingView) 的 [FloatingMagnetView](https://github.com/leotyndale/EnFloatingView/blob/master/floatingview/src/main/java/com/imuxuan/floatingview/FloatingMagnetView.java) 实现方式，并在其之上重新梳理手势事件、动画、及大部分功能。

对于导航栏的测量部分代码来自，wenlu,并在其之上增加了更多适配，已覆盖市场95%机型，可以说是目前能搜到的唯一可以准确测量的工具。
