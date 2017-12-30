package xyz.bboylin.plugin

import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.Task
import xyz.bboylin.task.FatJarTask

class FatJarPlugin implements Plugin<Project> {
    public static final String EXT_NAME = "fatJarExt"
    public static final String TASK_NAME = "fatJarTask"
    public static final String UNIX_SEP = "/"
    public static final String WINDOWS_SEP = "\\"
    public static final String WINDOWS_SUFFIX = "\\build\\intermediates\\bundles\\release\\classes.jar"
    public static final String UNIX_SUFFIX = "/build/intermediates/bundles/release/classes.jar"

    @Override
    void apply(Project project) {
        FatJarExtension ext = project.extensions.create(EXT_NAME, FatJarExtension)
        FatJarTask task = project.tasks.create(TASK_NAME, FatJarTask)
        task.doFirst {
            String rootPath = project.rootDir.absolutePath
            String sep = ext.isUnix ? UNIX_SEP : WINDOWS_SEP
            task.paths = ext.paths
            task.outputPath = rootPath + sep + ext.jarName
            task.projectPath = rootPath
            task.sep = sep
            task.pathSuffix = ext.isUnix ? UNIX_SUFFIX : WINDOWS_SUFFIX
        }
    }
}
