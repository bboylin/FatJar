package xyz.bboylin.task

import org.gradle.api.DefaultTask
import org.gradle.api.tasks.TaskAction

import java.util.jar.JarEntry
import java.util.jar.JarFile
import java.util.jar.JarOutputStream
import java.util.jar.Manifest
import java.util.zip.ZipException

class FatJarTask extends DefaultTask {
    def jarPaths
    def assetsPaths
    def outputPath
    def projectPath
    def sep
    def jarPathTail
    def assetsPathTail
    private static final String META_INF = "META-INF"
    private static final String ASSETS_PREFIX = "assets/"

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
            addAssets(file, assetsEntryPrefix + file.getName() + "/", jarOutputStream)
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
                jarPaths[i] = projectPath + sep + path.replace(":", sep)
                if (!isModule(jarPaths[i])) {
                    println(jarPaths[i] + " is not a module !!")
                    return false
                }
                jarPaths[i] += jarPathTail
            }
        }
        if (assetsPaths != null && assetsPaths.size() > 0) {
            for (int i = 0; i < assetsPaths.length; i++) {
                assetsPaths[i] = projectPath + sep + assetsPaths[i].replace(":", sep) + assetsPathTail
            }
        }
        return true
    }

/*    def getCompletePath(String module) {
        String path = projectPath
        File file = new File(path)
        String[] fileNames = file.list()
        LinkedList<String> queue = new LinkedList<>()
        for (String name : fileNames) {
            queue.add(path + sep + name)
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
                    queue.add(curPath + sep + name)
                }
            }
        }
        println("cannot find module : " + module)
        return null
    }*/

    def isModule(String path) {
        File file = new File(path + sep + "build.gradle")
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
        attribute.putValue("Manifest-Version", "1.0")
        attribute.putValue("Created-By", "FatJarTask@bboylin")
        return manifest
    }
}
