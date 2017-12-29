package xyz.bboylin.task

import org.gradle.api.DefaultTask
import org.gradle.api.tasks.TaskAction

import java.util.jar.JarEntry
import java.util.jar.JarFile
import java.util.jar.JarOutputStream
import java.util.jar.Manifest

class FatJarTask extends DefaultTask {
    def paths
    def outputPath
    def projectPath
    def sep
    def pathSuffix

    @TaskAction
    def createFatJar() {
        def manifest = getManifest()
        JarOutputStream jarOutputStream = null
        try {
            completePaths()
            for (String path : paths) {
                if (path == null) {
                    println("build " + outputPath + " failed!!!")
                    return
                }
            }
            jarOutputStream = new JarOutputStream(new FileOutputStream(outputPath), manifest)
            addFilesFromJars(paths, jarOutputStream)
        } catch (Exception e) {
            e.printStackTrace()
        } finally {
            if (jarOutputStream != null) {
                jarOutputStream.close()
            }
        }
        println("build " + outputPath + " success!!!")
    }

    def completePaths() {
        for (int i = 0; i < paths.length; i++) {
            String path = paths[i]
            if (!path.contains(".jar")) {
                paths[i] = getCompletePath(path)
            }
        }
    }

    def getCompletePath(String module) {
        String path = projectPath
        File file = new File(path)
        String[] fileNames = file.list()
        for (String name : fileNames) {
            if (name.equals(module)) {
                String curPath = path + sep + name
                if (isModule(curPath)) {
                    return curPath + pathSuffix
                }
            }
        }
        println("cannot find module : " + module)
        return null
    }

    def isModule(String path) {
        File file = new File(path)
        String[] names = file.list()
        for (String name : names) {
            if (name.equals("build.gradle")) {
                return true
            }
        }
        return false
    }

    def addFilesFromJars(String[] paths, JarOutputStream jarOutputStream) {
        for (String path : paths) {
            if (!new File(path).exists()) {
                println("create fat jar failed,missing " + path)
                break;
            }
            addFilesFromJar(path, jarOutputStream)
        }
    }

    def addFilesFromJar(String path, JarOutputStream jarOutputStream) {
        def jarFile = new JarFile(path)
        Enumeration<?> entries = jarFile.entries()
        while (entries.hasMoreElements()) {
            JarEntry entry = (JarEntry) entries.nextElement()
            if (entry.isDirectory() || entry.getName().toUpperCase().startsWith("META_INF")) {
                continue;
            }

            InputStream inputStream = jarFile.getInputStream(entry);
            copyDataToJar(inputStream, jarOutputStream, entry.getName());
        }
        println("added " + path)
        jarFile.close()
    }

    def copyDataToJar(InputStream inputStream, JarOutputStream jarOutputStream, String entryName) {
        int bufferSize;
        byte[] buffer = new byte[1024];

        jarOutputStream.putNextEntry(new JarEntry(entryName));
        while ((bufferSize = inputStream.read(buffer, 0, buffer.length)) != -1) {
            jarOutputStream.write(buffer, 0, bufferSize);
        }

        inputStream.close();
        jarOutputStream.closeEntry();
    }

    def getManifest() {
        def manifest = new Manifest();
        def attribute = manifest.getMainAttributes();
        attribute.putValue("Manifest-Version", "1.0");
        attribute.putValue("Created-By", "FatJarTask@bboylin");
        return manifest;
    }
}
