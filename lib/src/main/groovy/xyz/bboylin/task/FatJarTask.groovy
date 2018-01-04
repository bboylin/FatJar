package xyz.bboylin.task

import org.gradle.api.DefaultTask
import org.gradle.api.tasks.TaskAction

import java.util.jar.JarEntry
import java.util.jar.JarFile
import java.util.jar.JarOutputStream
import java.util.jar.Manifest
import java.util.zip.ZipException

class FatJarTask extends DefaultTask {
    //manifest版本
    def version
    //manifest作者
    def owner
    //jar路径数组
    def jarPaths
    //assets路径数组
    def assetsPaths
    //输出文件路径
    def outputPath
    //project路径
    def projectPath
    //jar路径后缀
    def jarPathSuffix = "/build/intermediates/bundles/release/classes.jar"
    private static final String META_INF = "META-INF"
    private static final String ASSETS_PREFIX = "assets/"
    public static final String SEP = "/"
    public static final String ASSETS_SUFFIX = "/src/main/assets"

    @TaskAction
    def createFatJar() {
        def manifest = getManifest()
        JarOutputStream jarOutputStream = null
        try {
            if (!completePaths()) {
                println("build " + outputPath + " failed!!!")
                return
            }
            jarOutputStream = new JarOutputStream(new FileOutputStream(outputPath), manifest)
            if (assetsPaths != null && assetsPaths.size() > 0) {
                for (String assetPath : assetsPaths) {
                    addAssets(new File(assetPath), ASSETS_PREFIX, jarOutputStream)
                }
            }
            addFilesFromJars(jarPaths, jarOutputStream)
        } catch (Exception e) {
            e.printStackTrace()
            println("build " + outputPath + " failed!!!")
            return
        } finally {
            if (jarOutputStream != null) {
                jarOutputStream.close()
            }
        }
        println("build " + outputPath + " success!!!")
    }

    def addAssets(File file, String assetsEntryPrefix, JarOutputStream jarOutputStream) {
        if (!file.exists() || !file.isDirectory()) {
            println("assets does not exist or is not Directory!!!")
            return
        }
        File[] files = file.listFiles()
        for (File f : files) {
            addAssetsFile(f, assetsEntryPrefix, jarOutputStream)
        }
    }

    def addAssetsFile(File file, String assetsEntryPrefix, JarOutputStream jarOutputStream) {
        if (file.isDirectory()) {
            addAssets(file, assetsEntryPrefix + file.getName() + SEP, jarOutputStream)
        } else {
            InputStream inputStream = new FileInputStream(file);
            copyDataToJar(inputStream, jarOutputStream, assetsEntryPrefix + file.getName());
            println("added " + file.getAbsolutePath())
        }
    }

    def completePaths() {
        for (int i = 0; i < jarPaths.length; i++) {
            String path = jarPaths[i]
            if (!path.contains(".jar")) {
                jarPaths[i] = projectPath + SEP + path.replace(":", SEP)
                if (!isModule(jarPaths[i])) {
                    println(jarPaths[i] + " is not a module !!")
                    return false
                }
                jarPaths[i] += jarPathSuffix
            }
        }
        if (assetsPaths != null && assetsPaths.size() > 0) {
            for (int i = 0; i < assetsPaths.length; i++) {
                assetsPaths[i] = projectPath + SEP + assetsPaths[i].replace(":", SEP) + ASSETS_SUFFIX
            }
        }
        return true
    }

    def isModule(String path) {
        File file = new File(path + SEP + "build.gradle")
        return file.exists()
    }

    def addFilesFromJars(String[] paths, JarOutputStream jarOutputStream) {
        for (String path : paths) {
            if (!new File(path).exists()) {
                println("create fat jar failed,missing " + path)
                break
            }
            addFilesFromJar(path, jarOutputStream)
        }
    }

    def addFilesFromJar(String path, JarOutputStream jarOutputStream) {
        def jarFile = new JarFile(path)
        Enumeration<?> entries = jarFile.entries()
        while (entries.hasMoreElements()) {
            JarEntry entry = (JarEntry) entries.nextElement()
            if (entry.isDirectory() || entry.getName().toUpperCase().startsWith(META_INF)) {
                continue
            }

            InputStream inputStream = jarFile.getInputStream(entry)
            copyDataToJar(inputStream, jarOutputStream, entry.getName())
        }
        println("added " + path)
        jarFile.close()
    }

    def copyDataToJar(InputStream inputStream, JarOutputStream jarOutputStream, String entryName) {
        int bufferSize
        byte[] buffer = new byte[1024]

        try {
            jarOutputStream.putNextEntry(new JarEntry(entryName))
        } catch (ZipException e) {
            e.printStackTrace()
            inputStream.close()
            jarOutputStream.closeEntry()
            return
        }
        while ((bufferSize = inputStream.read(buffer, 0, buffer.length)) != -1) {
            jarOutputStream.write(buffer, 0, bufferSize)
        }

        inputStream.close()
        jarOutputStream.closeEntry()
    }

    def getManifest() {
        def manifest = new Manifest()
        def attribute = manifest.getMainAttributes()
        attribute.putValue("Manifest-Version", version)
        attribute.putValue("Created-By", owner)
        return manifest
    }

    //1.0.0里的广度优先搜索，1.0.2之后不需要了。
/*    def getCompletePath(String module) {
        String path = projectPath
        File file = new File(path)
        String[] fileNames = file.list()
        LinkedList<String> queue = new LinkedList<>()
        for (String name : fileNames) {
            queue.add(path + SEP + name)
        }
        while (queue.size() > 0) {
            String curPath = queue.removeFirst()
            if (isModule(curPath)) {
                String temp = new String(curPath)
                String[] names = temp.replaceAll("\\\\", "/").split("/")
                if (module.equals(names[names.length - 1])) {
                    return curPath + pathSuffix
                }
            } else {
                File file1 = new File(curPath)
                String[] names = file1.list()
                for (String name : names) {
                    queue.add(curPath + SEP + name)
                }
            }
        }
        println("cannot find module : " + module)
        return null
    }*/

}
