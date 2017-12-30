### FatJar:合并多个jar的gradle插件，适合多module打包

----

#### usage：

1.下载[FatJar-1.0.0.jar](https://github.com/bboylin/FatJar/blob/master/FatJar-1.0.0.jar)，放置于项目根目录

2.在project的build.gradle中添加依赖和配置：
```groovy
buildscript {
    ......
    dependencies {
        ......
        classpath files('FatJar-1.0.0.jar')
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

fatJarExt {
	//输入数组，可改成已有的jar路径或者需要打包的module名
	//对module会预先打release包，然后进行广度优先搜索匹配jar路径，最后所有jar合并成一个jar
    paths = ["D:\\github\\okhttp.jar",
             "libtwo",
             "commonlib"]
    //最后output的jar名
    jarName = "fat.jar"
    //是否是Unix系统，是的话改为true或者忽略此项
    isUnix = false
}
```

3.项目根目录下命令行执行`gradlew fatJar`即可。

![](https://github.com/bboylin/FatJar/blob/master/cmd.png)