package com.ren.xh

import org.gradle.api.Project
import org.gradle.api.ProjectConfigurationException
import org.gradle.api.artifacts.Configuration
import org.gradle.api.artifacts.ResolvedArtifact

import java.util.zip.ZipEntry
import java.util.zip.ZipFile

class CheckSo{

    void createTask(Project project){
        project.task("checkso").doFirst {
            Configuration bb= project.configurations.getByName("implementation")
            bb.canBeResolved=true
            project.gradle.addListener(new EmbedResolutionListener(project, bb))
            def set  = ResolveUtils.resolveArtifacts(bb)
            processArtifacts(set)
        }
    }

    private static void processArtifacts(Collection<ResolvedArtifact> artifacts) {
        ArrayList<String>  v8a =  new ArrayList<String>()
        ArrayList<String>  arm =  new ArrayList<String>()
        HashMap<String,String>  armMap = new HashMap<String,String>()
        HashMap<String,String>  v8aMap = new HashMap<String,String>()
        for (final ResolvedArtifact artifact in artifacts) {
            String gp = artifact.getModuleVersion().getId().getGroup()
            String na = artifact.getModuleVersion().getId().getName()
            Utils.printDebugmm("---------------[依赖产物开始] group=${gp}:${na}----------------")
            ZipFile zipFile =  new ZipFile(artifact.file)
            Enumeration<?> entries =  zipFile.entries()

            while (entries.hasMoreElements()){
                ZipEntry entry = (ZipEntry) entries.nextElement();

                if (entry.isDirectory()){

                }else {
                    def lastIndex = entry.name.lastIndexOf("/") + 1
                    def name =  entry.name.substring(lastIndex)
                    if (name.endsWith(".so")){
                        Utils.printDebugmm("so文件 = "+ entry.name+"   size = "+(entry.getCompressedSize()/1024/1024))
                        if (entry.name.contains("arm64-v8a")){
                            v8a.add(name)
                            v8aMap.put(name,"group=${gp}:${na}"+"   size = "+(entry.getCompressedSize()/1024/1024))
                        }
                        if (entry.name.contains("armeabi")){
                            arm.add(name)
                            armMap.put(name,"group=${gp}:${na}"+"   size = "+(entry.getCompressedSize()/1024/1024))
                        }
                    }
                }
            }
            Utils.printDebugmm("---------------[依赖产物结束] group=${gp}:${na}----------------")
        }

        v8a.each {
            if (!arm.contains(it)){
                Utils.printDebugmm("arm不包含 = "+ it+"   "+ armMap.get(it))
            }
        }

        arm.each {
            if (!v8a.contains(it)){
                Utils.printDebugmm("v8a不包含 = "+ it+"   "+armMap.get(it))
            }
        }
    }


}