
package com.ren.xh

import com.tinify.AccountException
import com.tinify.Tinify

import java.nio.channels.FileChannel

class TinifyFormt {
    public static int compressSize  = 0
    void init(String id){
        if (id ==null || id.equals("")){
            throw new XhException("请传入tinify id")
        }
        try {
            compressSize=0
            //测试key值的正确性
            //"wQdHz6dkJhtSCt0sHwCmvQh5lpSMcyL1"
            Tinify.setKey(id)
            Tinify.validate()
        } catch (AccountException ex) {
            println("TinyCompressor" + ex.printStackTrace())
        }
    }

    void commpress(String path,String outPath,String name,ArrayList<String> list){
        def qian = getSize(path)
        Utils.printDebugmm("压缩前 size =  "+ qian)
        def tSource = Tinify.fromFile(path)
        String outname = outPath+"/"+name
        tSource.toFile(outname)
        def hou = getSize(outname)
        Utils.printDebugmm("压缩后 size =  "+ hou)
        compressSize += qian-hou
        def file =  new File(path)
        if (file.exists()){
            file.delete()
        }
        copyFileUsingFileChannels(new File(outname),file)
        Utils.printDebugmm("copy 完成")
        list.add(path)
    }

    int getSize(String path){
        def file  = new File(path)
        int size  = file.size()/1024
        return size
    }

    private static void copyFileUsingFileChannels(File source, File dest) throws IOException {
        FileChannel inputChannel = null;
        FileChannel outputChannel = null;
        try {
            inputChannel = new FileInputStream(source).getChannel();
            outputChannel = new FileOutputStream(dest).getChannel();
            outputChannel.transferFrom(inputChannel, 0, inputChannel.size());
        } finally {
            inputChannel.close();
            outputChannel.close();
        }
    }
}