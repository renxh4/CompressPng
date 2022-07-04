package com.ren.xh

import org.gradle.api.Project
import org.gradle.api.artifacts.Configuration
import org.gradle.api.artifacts.DependencyResolutionListener
import org.gradle.api.artifacts.ResolvableDependencies
import org.gradle.api.internal.artifacts.dependencies.DefaultProjectDependency

class EmbedResolutionListener implements DependencyResolutionListener {
    private final Project project

    private final Configuration configuration
    public EmbedResolutionListener(Project project, Configuration configuration){
        this.project = project
        this.configuration = configuration
    }

    @Override
    void beforeResolve(ResolvableDependencies dependencies) {
        configuration.dependencies.each { dependency ->
            if (dependency instanceof DefaultProjectDependency) {
                if (dependency.targetConfiguration == null) {
                    dependency.targetConfiguration = "default"
                    println("EmbedResolutionListener beforeResolve1 = " +"default")
                }
            }
        }
        project.gradle.removeListener(this)
    }

    @Override
    void afterResolve(ResolvableDependencies dependencies) {

    }
}