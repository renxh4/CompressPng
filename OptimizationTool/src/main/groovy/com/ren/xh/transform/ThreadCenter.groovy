package com.ren.xh.transform

import com.android.build.gradle.AppExtension
import org.gradle.api.Plugin
import org.gradle.api.Project

class ThreadCenter implements Plugin<Project> {

    @Override
    void apply(Project project) {
        project.task("thread").doFirst {
            println("dajdaj11")
        }
        AppExtension appExtension = project.getExtensions().findByType(AppExtension.class)
        appExtension.registerTransform(new ThreadTransform(project))
    }
}