package com.nzh.plugin.util;

import javassist.CannotCompileException;
import javassist.CtClass;
import javassist.CtMethod;
import javassist.CtNewMethod;
import javassist.bytecode.DuplicateMemberException;
import javassist.expr.ExprEditor;
import javassist.expr.MethodCall;

import static com.nzh.plugin.Const.METHOD_NAME;
import static com.nzh.plugin.Const.METHOD_SetContentView;

/**
 * Created by 31414 on 2019/4/12.
 */

public class SuperClassExprEditor extends ExprEditor {

    // 是否已经在 OnCreate方法中添加了  其他方法调用
    public static boolean isAlreadyAdd = false;

    CtClass ctClass;
    CtMethod ctMethod;
    String buildClasspath;

    public SuperClassExprEditor(CtClass ctClass, CtMethod ctMethod, String buildClasspath) {
        this.ctClass = ctClass;
        this.ctMethod = ctMethod;
        this.buildClasspath = buildClasspath;
    }

    @Override
    public void edit(MethodCall c) throws CannotCompileException {
        super.edit(c);

        if (isAlreadyAdd) {
            return;
        }
        if (METHOD_SetContentView.equals(c.getMethodName())) {
            // 生成方法
            genMethodByNewMethod(ctClass, "public void " + METHOD_NAME + "(){}");
            // 插入调用
            ctMethod.insertAt(c.getLineNumber() + 1, METHOD_NAME + "();");
            // 修改后不再编辑 ，直接退出。
            ctClass.debugWriteFile(buildClasspath);
            isAlreadyAdd = true;
        }
    }


    private void genMethodByNewMethod(CtClass cc, String method) {

        System.err.println("\r\n");
        System.err.println(method);
        try {
            CtMethod m3 = CtNewMethod.make(
                    method, cc);

            cc.addMethod(m3);
        } catch (CannotCompileException e) {
            if (e instanceof DuplicateMemberException) {
                System.err.println("------ignore------");
            } else {
                e.printStackTrace();
            }
        }
    }

}
