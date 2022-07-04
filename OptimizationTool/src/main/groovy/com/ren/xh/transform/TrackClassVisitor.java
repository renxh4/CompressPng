package com.ren.xh.transform;

import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;


public class TrackClassVisitor extends ClassVisitor implements Opcodes {

    public TrackClassVisitor(ClassVisitor classVisitor) {
        super(ASM7, classVisitor);
    }


    @Override
    public MethodVisitor visitMethod(int access, String name, String descriptor, String signature, String[] exceptions) {
        MethodVisitor mv = super.visitMethod(access, name, descriptor, signature, exceptions);
        return new TextMethodVisitor(ASM7, mv);
    }


    public static class TextMethodVisitor extends MethodVisitor {

        public TextMethodVisitor(int api, MethodVisitor methodVisitor) {
            super(api, methodVisitor);
        }

        @Override
        public void visitMethodInsn(int opcode, String owner, String name, String descriptor, boolean isInterface) {
            if (name.equals("onCreate") && descriptor.equals("(Landroid/os/Bundle;)V")) {
                System.out.println("开始了" + opcode + "/" + owner + "/" + name + "/" + descriptor + "/" + isInterface);
                mv.visitLdcInsn("mmm1");
                mv.visitLdcInsn("hahaha123");
                mv.visitMethodInsn(INVOKESTATIC, "android/util/Log", "d", "(Ljava/lang/String;Ljava/lang/String;)I", false);
                mv.visitInsn(POP);
                super.visitMethodInsn(opcode, owner, name, descriptor, isInterface);

            } else {
                super.visitMethodInsn(opcode, owner, name, descriptor, isInterface);
            }


        }
    }

}
