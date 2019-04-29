package com.nzh.plugin.util;


import javassist.CannotCompileException;
import javassist.CtClass;
import javassist.CtMethod;
import javassist.expr.ExprEditor;
import javassist.expr.MethodCall;

import static com.nzh.plugin.Const.METHOD_NAME;
import static com.nzh.plugin.Const.METHOD_SetContentView;

/**
 * Created by 31414 on 2019/4/26.
 */

public class CleanCodeEditor extends ExprEditor {

    CtClass ctClass;
    String buildClasspath;

    public CleanCodeEditor(CtClass ctClass, String buildClasspath) {
        this.ctClass = ctClass;
        this.buildClasspath = buildClasspath;
    }

    @Override
    public void edit(MethodCall c) throws CannotCompileException {
        super.edit(c);



    }
}
