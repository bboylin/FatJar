package xyz.bboylin.plugin

import org.gradle.api.Plugin
import org.gradle.api.Project
import xyz.bboylin.task.FatJarTask

class FatJarPlugin implements Plugin<Project> {
    public static final String EXT_NAME = "fatJarExt"
    public static final String TASK_NAME = "fatJarTask"
    public static final String UNIX_SEP = "/"
    public static final String WINDOWS_SEP = "\\"
    public static final String WINDOWS_JAR_SUFFIX = "\\build\\intermediates\\bundles\\release\\classes.jar"
    public static final String UNIX_JAR_SUFFIX = "/build/intermediates/bundles/release/classes.jar"
    public static final String UNIX_ASSETS_SUFFIX = "/src/main/assets"
    public static final String WINDOWS_ASSETS_SUFFIX = "\\src\\main\\assets"

    @Override
    void apply(Project project) {
        FatJarExtension ext = project.extensions.create(EXT_NAME, FatJarExtension)
        FatJarTask task = project.tasks.create(TASK_NAME, FatJarTask)
        task.doFirst {
            String rootPath = project.rootDir.absolutePath
            String sep = ext.isUnix ? UNIX_SEP : WINDOWS_SEP
            task.jarPaths = ext.jarPaths
            task.outputPath = rootPath + sep + ext.output
            task.projectPath = rootPath
            task.sep = sep
            task.jarPathSuffix = ext.isUnix ? UNIX_JAR_SUFFIX : WINDOWS_JAR_SUFFIX
            task.assetsPaths = ext.assetsPaths
            task.assetsPathSuffix = ext.isUnix ? UNIX_ASSETS_SUFFIX : WINDOWS_ASSETS_SUFFIX
            task.owner = ext.owner
            task.version = ext.version
            if (ext.isDebug) {
                task.jarPathSuffix = task.jarPathSuffix.replace("release", "debug")
            }
        }
    }
}
