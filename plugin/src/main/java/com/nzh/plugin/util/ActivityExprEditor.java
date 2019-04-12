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

public class ActivityExprEditor extends ExprEditor {


    CtClass ctClass;
    CtMethod ctMethod;
    String buildClasspath;

    public ActivityExprEditor(CtClass ctClass, CtMethod ctMethod, String buildClasspath) {
        this.ctClass = ctClass;
        this.ctMethod = ctMethod;
        this.buildClasspath = buildClasspath;
    }

    @Override
    public void edit(MethodCall c) throws CannotCompileException {
        super.edit(c);

        if (METHOD_SetContentView.equals(c.getMethodName())) {

            // 插入调用
            ctMethod.insertAt(c.getLineNumber() + 1, METHOD_NAME + "();");
            // 可以重复修改
            ctClass.debugWriteFile(buildClasspath);
        }
    }


}
