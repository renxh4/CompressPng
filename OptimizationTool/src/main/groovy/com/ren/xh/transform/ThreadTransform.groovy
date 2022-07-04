package com.ren.xh.transform

import com.android.build.api.transform.*
import com.android.build.gradle.internal.pipeline.TransformManager
import com.android.utils.FileUtils
import org.gradle.api.Project
import org.objectweb.asm.ClassReader
import org.objectweb.asm.ClassWriter

class ThreadTransform extends Transform {
    Project mProject

    public ThreadTransform(Project project) {
        mProject = project
    }

    @Override
    String getName() {
        return "MatchMakerThread"
    }

    @Override
    Set<QualifiedContent.ContentType> getInputTypes() {
        return TransformManager.CONTENT_CLASS
    }

    @Override
    Set<? super QualifiedContent.Scope> getScopes() {
        return TransformManager.SCOPE_FULL_PROJECT
    }

    @Override
    boolean isIncremental() {
        return false
    }

    @Override
    void transform(TransformInvocation transformInvocation) throws TransformException, InterruptedException, IOException {
        super.transform(transformInvocation)
        printCopyRight()

        TransformOutputProvider transformOutputProvider = transformInvocation.getOutputProvider()
        if (!transformInvocation.isIncremental()) {
            transformOutputProvider.deleteAll()
        }

        transformInvocation.getInputs().each { TransformInput transformInput ->

            transformInput.directoryInputs.each { DirectoryInput directoryInput ->

                if (directoryInput.file.isDirectory()) {
                    FileUtils.getAllFiles(directoryInput.file).each { File file ->
                        def classPath = file.parentFile.absolutePath + File.separator + file.name
                        def name = file.name
                        println("mmm" + file.absolutePath)
                        println("mmmclass" + classPath)
                        if (name.endsWith(".class") && name != ("R.class")
                                && !name.startsWith("R\$") && name != ("BuildConfig.class")) {
                            weave(file.absolutePath, classPath)
                        }
                    }
                }

                def dest = transformInvocation.outputProvider.getContentLocation(
                        directoryInput.name,
                        directoryInput.contentTypes,
                        directoryInput.scopes,
                        Format.DIRECTORY
                )


                FileUtils.copyDirectoryToDirectory(directoryInput.file, dest)
            }


            transformInput.jarInputs.each { JarInput jarInput ->

                println("jar=" + jarInput.name)

                processJarInput(jarInput, transformOutputProvider)
            }
        }
    }


    static void printCopyRight() {
        println()
        println("******************************************************************************")
        println("******                                                                  ******")
        println("******                欢迎使用 MatchMakerThread 编译插件                    ******")
        println("******                                                                  ******")
        println("******************************************************************************")
        println()
    }


    static void processJarInput(JarInput jarInput, TransformOutputProvider outputProvider) {
        File dest = outputProvider.getContentLocation(
                jarInput.getFile().getAbsolutePath(),
                jarInput.getContentTypes(),
                jarInput.getScopes(),
                Format.JAR)

        // to do some transform

        // 将修改过的字节码copy到dest，就可以实现编译期间干预字节码的目的了
        FileUtils.copyFile(jarInput.getFile(), dest)
    }

    private static void weave(String inputPath, String outputPath) {
        try {
            def file = new File(inputPath)
            ClassReader reader = new ClassReader(file.readBytes())
            def writer = new ClassWriter(reader, ClassWriter.COMPUTE_MAXS)
            TrackClassVisitor adapter = new TrackClassVisitor(writer)
            reader.accept(adapter, ClassReader.EXPAND_FRAMES)
            def code = writer.toByteArray()
            def fos = new FileOutputStream(outputPath)
            fos.write(code)
            fos.close()
        } catch (IOException e) {
            e.printStackTrace()
        }
    }

}