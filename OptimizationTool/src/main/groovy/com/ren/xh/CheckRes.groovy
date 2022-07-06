package com.ren.xh

import com.google.gson.Gson
import org.gradle.api.Project

class CheckRes {

    static def ma5List = new ArrayList<String>()
    static def map = new HashMap<String, String>()
    static def diffmap = new HashMap<String, String>()



    static void createResTask(Project project) {
        ma5List.clear()
        map.clear()
        diffmap.clear()
        Xh xh =  project.getExtensions().getByName("xh")
        TinifyFormt tinifyFormt = new TinifyFormt()
        tinifyFormt.init(xh.tynifyId)
        project.task("checkres").doFirst {
            def path = project.getRootDir().getAbsolutePath() + "/app/src/main/res/"
            def outPath = project.getRootDir().getAbsolutePath() + "/app/build/compress"
            def outPathFile = project.getRootDir().getAbsolutePath() + "/compress.json"
            def json = new File(outPathFile)
            CompressBean compressBean
            if (!json.exists()) {
                json.createNewFile()
                compressBean = new CompressBean()
            } else {
                def jsoncon = FileUtils.getFileContent(json)
                compressBean = new Gson().fromJson(jsoncon, CompressBean.class)
            }
            def out = new File(outPath)
            if (!out.exists()) {
                out.mkdirs()
            }
            File file = new File(path)
            if (compressBean == null) {
                compressBean = new CompressBean()
            }
            if (compressBean.data == null) {
                compressBean.data = new ArrayList<String>()
            }

            eachDictory(file, tinifyFormt, outPath, compressBean.data)

            def jsonResult = new Gson().toJson(compressBean)
            FileUtils.toFileContent(json, jsonResult)

            Utils.printDebugmm("此次共压缩 {$TinifyFormt.compressSize}")

            diffmap.each {
                Utils.printDebugmm("重复文件")
                Utils.printDebugmm(it.key)
                Utils.printDebugmm(it.value)
            }
            def size = diffmap.size()
            Utils.printDebugmm("此次共找到重复文件 {$size}")

        }
    }

    static void eachDictory(File file, TinifyFormt tinifyFormt, String outPath, ArrayList<String> list) {
        if (file.isDirectory()) {
            file.listFiles().each {
                eachDictory(it, tinifyFormt, outPath, list)
            }
        } else {
            def md5 = FileUtils.getMD5(file.absolutePath)
            if (ma5List.contains(md5)){
                diffmap.put(file.absolutePath,  map.get(md5))
            }else {
                ma5List.add(md5)
                map.put(md5, file.absolutePath)
            }

            if (file.name.endsWith(".png")) {
                int size = file.size() / 1024
                if (size > 10) {
                    if (list.contains(file.absolutePath)) {
                        Utils.printDebugmm(file.absolutePath + "  已经压缩过了")
                    } else {
                        Utils.printDebugmm(file.absolutePath + "  size = " + size + "KB")
                        tinifyFormt.commpress(file.absolutePath, outPath, file.name, list)
                    }
                }
            }
        }
    }
}