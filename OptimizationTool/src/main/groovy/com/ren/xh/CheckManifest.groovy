package com.ren.xh


import org.gradle.api.Project
import org.gradle.api.artifacts.Configuration
import org.gradle.api.artifacts.ResolvedArtifact

import java.util.zip.ZipEntry
import java.util.zip.ZipFile

class CheckManifest {
    public static ArrayList<String> rootPer;
    static void createManifestTask(Project project) {
        project.task("checkm").doFirst {
            rootPer = getRootPermisson(project)
            Configuration bb = project.configurations.getByName("implementation")
            bb.canBeResolved = true
            project.gradle.addListener(new EmbedResolutionListener(project, bb))
            def set = ResolveUtils.resolveArtifacts(bb)
            processArtifacts(set)
        }
    }


    private static void processArtifacts(Collection<ResolvedArtifact> artifacts) {
        HashMap<String, ArrayList<String>> permissionMap = new HashMap<String, ArrayList<String>>()
        for (final ResolvedArtifact artifact in artifacts) {
            String gp = artifact.getModuleVersion().getId().getGroup()
            String na = artifact.getModuleVersion().getId().getName()
//            Utils.printDebugmm("---------------[依赖产物开始] group=${gp}:${na}----------------")
            ZipFile zipFile = new ZipFile(artifact.file)
            Enumeration<?> entries = zipFile.entries()

            while (entries.hasMoreElements()) {
                ZipEntry entry = (ZipEntry) entries.nextElement();

                if (entry.isDirectory()) {

                } else {
                    if (entry.name.endsWith("AndroidManifest.xml")) {
                        InputStream inputStream = zipFile.getInputStream(entry)
                       ArrayList<String> permissionList = parseXml(inputStream)
                        if (permissionList!=null&&permissionList.size()>0) {
                            permissionMap.put("group=${gp}:${na}", permissionList)
                        }
                    }
                }
//            Utils.printDebugmm("---------------[依赖产物结束] group=${gp}:${na}----------------")
            }
        }

        permissionMap.each {
            def key = it.getKey()
            StringBuffer stringBuffer = new StringBuffer()
            it.getValue().each {
                stringBuffer.append(it + ",")
                if (!rootPer.contains(it)){
                    Utils.printDebugmm("root 不包含权限 key = {$key}  权限 = {$it}")
                }
            }
//            Utils.printDebugmm("key = {$key}   value = {$stringBuffer}")
        }
    }


   public static ArrayList<String> parseXml(InputStream inputStream) {
        Node node = new XmlParser(false, false).parse(inputStream)
        boolean have = false
        ArrayList<String> permissionList = new ArrayList<String>()
        node.children().each {
            if (it instanceof Node) {
                if (it.name().equals("uses-permission")) {
                    have = true
                    def per = it.attribute("android:name").toString()
                    def index = per.lastIndexOf(".")
                    def real = per.substring(index,per.length())
                    permissionList.add(real)
                }
            }
        }

        return permissionList
    }

    static ArrayList<String> getRootPermisson(Project project){
        def path = project.getRootDir().getAbsolutePath() + "/app/src/main/AndroidManifest.xml"
        Utils.printDebugmm(path)
        def input = new FileInputStream(path)
        return parseXml(input)

    }
}