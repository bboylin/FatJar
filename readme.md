### FatJar:适用于sdk多module打包和合并多个jar的gradle插件
----

[ ![Download](https://api.bintray.com/packages/bboylin/FatJarPlugin/FatJarPlugin/images/download.svg) ](https://bintray.com/bboylin/FatJarPlugin/FatJarPlugin/_latestVersion)
#### usage：

1.下载`fatJar.gradle`放置于project根目录
2.在project的build.gradle中添加依赖和配置：
```groovy
apply from: 'fatJar.gradle'
buildscript {
    repositories {
        ......
        maven { url 'https://dl.bintray.com/bboylin/FatJarPlugin/' }
    }
    dependencies {
        classpath 'xyz.bboylin:FatJarPlugin:1.0.4'
    }
}

//可配置项
fatJarExt {
	//这里可配置需要打包的module名和需要加入的第三方jar
    jarPaths = ["D:\\github\\okhttp.jar",
             "libtwo",
             "commonlib"]
    //配置需要添加assets的module名，没有可删掉此项
    assetsPaths = ["libtwo","commonlib"]
    //最后output的jar名
    output = "fat.jar"
    //windows系统请加上这句，不加默认是Unix系
    isUnix = false
    //manifest中created-by的值
    owner = "your name or your organization"
    //manifest中version的值
    version = 'your sdk version'
    //只打debug包的话加上这句，不加默认只打release包
    isDebug = true
}
```
3.项目根目录下命令行执行`gradlew fatJar`即可。

![](https://github.com/bboylin/FatJar/blob/master/cmd.png)

### 注意：
如果你的module不在项目根目录下，比如`D:\MyApplication\components\libone`和`D:\MyApplication\components\player\bdplayer`，`D:\MyApplication`是我的项目根目录，那你不能直接写"libone"，要改为"components:libone"，即module用相对路径。示例：
```groovy
fatJarExt {
    jarPaths = ["D:\\github\\okhttp.jar"
             ,"libtwo",
             ,"commonlib"
             ,"components:libone"
             ,"components:player:bdplayer"]
    assetsPaths = ["libtwo"
                    ,"components:libone"
                    ,"components:player:bdplayer"]
    //最后output的jar名
    output = "fat.jar"
    //windows系统请加上这句，不加默认是Unix系
    isUnix = false
    //manifest中created-by的值
    owner = "your name or your organization"
    //manifest中version的值
    version = 'your sdk version'
    //只打debug包的话加上这句，不加默认只打release包
    isDebug = true
}
```