package com.nzh.plugin.util;

import javassist.CannotCompileException;
import javassist.CtClass;
import javassist.CtMethod;
import javassist.expr.ExprEditor;
import javassist.expr.MethodCall;

import static com.nzh.plugin.Const.METHOD_NAME;
import static com.nzh.plugin.Const.METHOD_OnCreateView;
import static com.nzh.plugin.Const.METHOD_SetContentView;

/**
 * Created by 31414 on 2019/4/19.
 */

public class FragmentExprEditor extends ExprEditor {

    CtClass ctClass;
    CtMethod ctMethod;
    String buildClasspath;

    /**
     * @param ctClass        当前修改的类
     * @param ctMethod       当卡修改的方法。这里只改一个方法。
     * @param buildClasspath 修改类后的写入路径
     */
    public FragmentExprEditor(CtClass ctClass, CtMethod ctMethod, String buildClasspath) {
        this.ctClass = ctClass;
        this.ctMethod = ctMethod;
        this.buildClasspath = buildClasspath;
    }

    @Override
    public void edit(MethodCall c) throws CannotCompileException {
        super.edit(c);

        if ("bind".equals(c.getMethodName()) &&
                "lsn.javassit.nzh.com.javassit.fragment.MyInjector".equals(c.getClassName())) {

//            c.replace("{" + METHOD_NAME + "($$);}");
            // 插入调用
            c.replace("{$_ = $proceed($$); " + METHOD_NAME + "($_);}");
            //  ctMethod.insertAt(c.getLineNumber() + 1, METHOD_NAME + "($proceed($$));");

            // 可以重复修改
              ctClass.debugWriteFile(buildClasspath);
        }
    }

}
