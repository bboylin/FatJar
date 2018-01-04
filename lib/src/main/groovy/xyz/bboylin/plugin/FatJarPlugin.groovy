package xyz.bboylin.plugin

import org.gradle.api.Plugin
import org.gradle.api.Project
import xyz.bboylin.task.FatJarTask

class FatJarPlugin implements Plugin<Project> {
    public static final String EXT_NAME = "fatJarExt"
    public static final String TASK_NAME = "fatJarTask"

    @Override
    void apply(Project project) {
        FatJarExtension ext = project.extensions.create(EXT_NAME, FatJarExtension)
        FatJarTask task = project.tasks.create(TASK_NAME, FatJarTask)
        task.doFirst {
            String rootPath = project.rootDir.absolutePath
            task.jarPaths = ext.jarPaths
            task.outputPath = rootPath + "/" + ext.output
            task.projectPath = rootPath
            task.assetsPaths = ext.assetsPaths
            task.owner = ext.owner
            task.version = ext.version
            if (ext.isDebug) {
                task.jarPathSuffix = task.jarPathSuffix.replace("release", "debug")
            }
        }
    }
}
