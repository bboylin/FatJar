### FatJar:适用于多module打包和合并多个jar的gradle插件
----

#### usage：

1.下载[FatJar-1.0.1.jar](https://github.com/bboylin/FatJar/blob/master/FatJar-1.0.1.jar)，放置于项目根目录

2.在project的build.gradle中添加依赖和配置：
```groovy
buildscript {
    ......
    dependencies {
        ......
        classpath files('FatJar-1.0.1.jar')
    }
}

apply plugin: 'FatJarPlugin'
fatJarTask.dependsOn({
    def tasks = new ArrayList<>()
    fatJarExt.paths.each {
        if (!it.contains(".jar")) {
            tasks.add(":" + it + ":assembleRelease")
        }
    }
    return tasks.toArray()
})

//可配置项
fatJarExt {
	//这里可配置需要打包的module名和需要加入的第三方jar
    paths = ["D:\\github\\okhttp.jar",
             "libtwo",
             "commonlib"]
    //最后output的jar名
    jarName = "fat.jar"
    //是否是Unix系统，是的话改为true或者删掉此项
    isUnix = false
}
```
注意：如果你的module不在项目根目录下，比如`D:\MyApplication\components\libone`和`D:\MyApplication\components\player\bdplayer`，`D:\MyApplication`是我的项目根目录，那你不能直接写"libone"，要改为"components:libone"，即module用相对路径。示例：
```groovy
fatJarExt {
    paths = ["D:\\github\\okhttp.jar"
             ,"libtwo",
             ,"commonlib"
             ,"components:libone"
             ,"components:player:bdplayer"]
    //最后output的jar名
    jarName = "fat.jar"
    //是否是Unix系统，是的话改为true或者忽略此项
    isUnix = false
}
```
3.项目根目录下命令行执行`gradlew fatJar`即可。

![](https://github.com/bboylin/FatJar/blob/master/cmd.png)