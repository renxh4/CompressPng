package com.ren.xh

import org.gradle.api.ProjectConfigurationException
import org.gradle.api.artifacts.Configuration
import org.gradle.api.artifacts.ResolvedArtifact

class ResolveUtils{
    public static final String ARTIFACT_TYPE_AAR = 'aar'

    public static final String ARTIFACT_TYPE_JAR = 'jar'

    private static Collection<ResolvedArtifact> resolveArtifacts(Configuration configuration) {
        //这个方法的主要作用是，拿到embed引用的依赖，并判断他是aar或者jar包，然后放入集合
        def set = new ArrayList()
        if (configuration != null) {
            configuration.canBeResolved=true
            configuration.resolvedConfiguration.resolvedArtifacts.each { artifact ->
                if (ARTIFACT_TYPE_AAR == artifact.type || ARTIFACT_TYPE_JAR == artifact.type) {
                    //
                } else {
                    throw new ProjectConfigurationException('Only support embed aar and jar dependencies!', null)
                }
                //这里的 artifact 指的就是  如 ： okhttp3-integration-4.11.0.aar
                set.add(artifact)
            }
        }
        return set
    }

}