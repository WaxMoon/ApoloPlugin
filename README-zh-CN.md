# ApoloPlugin

**ApoloPlugin** 是一个 Android轻量级的java hook 库，它支持 arm32 和 arm64两种架构。Apolo意为阿波罗，其为艺术之神，Art翻译过来也有艺术之意，故以此命名

## 特性

*  支持Android8.1 - Android12
*  支持armeabi-v7a以及arm64-v8a
*  不支持声明为abstract、interface、native等特殊函数hook
*  支持调用原方法
*  无需将ArtMethod解释执行，性能损耗较小
*  兼容JIT/AOT模式

## 了解接口函数并上手

接口函数可以在仓库[ApoloPlugin module](ApoloPlugin)找到，目前只提供了Java版本
示例代码可以在[app module](app)中找到

### 1.加载核心so库，初始化hook引擎

```Java
public class DemoApplication extends Application {
    private static Application sApp;
    @Override
    public void onCreate() {
        super.onCreate();
        //init ArtHook
        ArtHook.preLoad();
    }
}
```
hook引擎中会绕过hiddenApi检测，该处参考了[RePublic](https://github.com/whulzz1993/RePublic)。所以调用preLoad之后，您无须担心hiddenApi问题。

### 2.获取原函数Method以及代理函数Method

比如hook ApplicationPackageManager.getInstalledPackages函数， 您首先需要通过反射拿到该函数的Method实例，然后声明一个static函数作为代理函数。

**注意**: 1)原函数声明为static，那么代理函数的参数类型均与原函数保持一致，对象类型也可简单使用Object表示。2)原函数非static，那么代理函数的第一个参数类型必须是对象类型，其余参数与原函数保持一致

```Java
    public static void startHookOnlyOnce() {
        try {
            //1. get origin-method -> ApplicationPackageManager.getInstalledPackages(int flags)
            Class<?> class_ApplicationPackageManager = Class.forName("android.app.ApplicationPackageManager");
            Method method_getInstalledPackages = class_ApplicationPackageManager
                    .getDeclaredMethod("getInstalledPackages", int.class);

            //2. get proxy-method -> MainActivity.getInstalledPackages
            Method proxyMethod_getInstalledPackages = MainActivity.class.getDeclaredMethod("proxy_getInstalledPackages",
                    PackageManager.class, int.class);

            //3. startHook
            HashMap<Method, Method> proxyMethods = new HashMap<>();
            proxyMethods.put(method_getInstalledPackages, proxyMethod_getInstalledPackages);
            ArtHook.startHook(proxyMethods);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
```

### 3.一切就绪，开始hook

上述2中的代码示例已提及，非常简单。只需要将origin-method与proxy-method配对，并放入HashMap中，然后调用ArtHook.startHook即可

### 4.调用原函数

下面的示例函数分为三个步骤，第二步为调用原函数。第一步以及第三步均调用了ArtHook.hookTransition，切记不能省略，否则会导致死循环。

**注意**: 如果您在第二步中，有大量逻辑代码，如果该处逻辑中有直接或者间接调用某一个被hook的函数，那么它将不会被代理，直到您调用hookTransition(false)为止。

所以，您可以简单的将hookTransition理解为hook状态切换:
1) true：状态切换为origin，该线程取消hook. 
2) false: 状态切换为hook，该线程将进行相关函数代理.

```Java
    public static List<PackageInfo> proxy_getInstalledPackages(PackageManager pm, int flags) {
        ...
        ...
        try {
            //1.disable proxy method so that you can call origin-method
            ArtHook.hookTransition(true);

            //2.call origin method
            List<PackageInfo> pkgInfos = pm.getInstalledPackages(flags);

            ...
            ...

            return Collections.emptyList();

        } finally {
            //3.enable proxy method
            ArtHook.hookTransition(false);
        }
    }
```

## 声明

由于各种原因，ApoloPlugin只开放出来了armeabi-v7a版本，后续会将该仓库持续优化，稳定后开放核心源码

## 愿景

### 1.广大需求爱好者可以尽情提出想法，热烈欢迎
### 2.愿大家多提issue
### 3.欢迎提交代码

## 个人简介
### @WaxMoon android framework/hook爱好者，2015年入坑
### 个人QQ3403281183

### ArtHook交流群(加群能获取到完整版插件，并且群消息会及时同步最新feature)
![ArtHook交流群](raw/qq_scan.png)