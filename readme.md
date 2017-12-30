### FatJar:合并多个jar的gradle插件

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

fatJarExt {
	//输入数组，改成你的jar路径或者module名
	//对module会进行广度优先搜索匹配jar路径
    paths = ["D:\\github\\MyApplication\\libone\\build\\intermediates\\bundles\\release\\classes.jar",
             "libtwo"]
    //最后output的jar名
    jarName = "fat.jar"
    //是否是Unix系统，是的话改为true或者忽略此项
    isUnix = false
}
```

3.项目根目录下命令行执行`gradlew fatJar`即可见到统计结果。

![](https://github.com/bboylin/FatJar/blob/master/cmd.png)