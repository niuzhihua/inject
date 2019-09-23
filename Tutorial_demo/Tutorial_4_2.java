package com.nzh.javassit;


import javassist.CannotCompileException;
import javassist.CodeConverter;
import javassist.CtClass;
import javassist.CtMethod;
import javassist.Modifier;
import javassist.bytecode.MethodInfo;
import javassist.bytecode.analysis.Analyzer;
import javassist.expr.Cast;
import javassist.expr.ConstructorCall;
import javassist.expr.ExprEditor;
import javassist.expr.FieldAccess;
import javassist.expr.Handler;
import javassist.expr.Instanceof;
import javassist.expr.MethodCall;
import javassist.expr.NewArray;
import javassist.expr.NewExpr;
import javassist.tools.Callback;

public class Tutorial_4_2 {

    /**
     * javassist 只允许修改方法体 里面的 代码(表达式)。
     *
     * @throws Exception
     */
    public static void test() throws Exception {


        CtClass c = null;
        CtMethod method = null;

        // 修改步骤：运行 CtMethod或者CtClass 的instrument() 方法 ，并传递ExprEditor  对象。

        method.instrument(new ExprEditor(){

            @Override
            public boolean doit(CtClass clazz, MethodInfo minfo) throws CannotCompileException {
                return super.doit(clazz, minfo);
            }

            @Override
            public void edit(NewExpr e) throws CannotCompileException {
                super.edit(e);
            }

            @Override
            public void edit(NewArray a) throws CannotCompileException {
                super.edit(a);
            }

            @Override
            public void edit(MethodCall m) throws CannotCompileException {
                super.edit(m);
            }

            @Override
            public void edit(ConstructorCall c) throws CannotCompileException {
                super.edit(c);
            }

            // 这里f 就是 在ctMethod方法中已经使用过的变量。
            // 如果没有使用，这个方法就不会回调。
            // 如果使用了，使用几次 这个方法就会回调几次。
            @Override
            public void edit(FieldAccess f) throws CannotCompileException {
                super.edit(f);
            }

            @Override
            public void edit(Instanceof i) throws CannotCompileException {
                super.edit(i);
            }

            @Override
            public void edit(Cast c) throws CannotCompileException {
                super.edit(c);
            }

            @Override
            public void edit(Handler h) throws CannotCompileException {
                super.edit(h);
            }
        });

    }


    public static void testOther() throws Exception {

        // 1 ： 生成可变参数的方法

        //public int length(int... args) { return args.length; }

        CtClass cc  = null/* target class */;
        CtMethod m = CtMethod.make("public int length(int[] args) { return args.length; }", cc);
        m.setModifiers(m.getModifiers() | Modifier.VARARGS);
        cc.addMethod(m);

        // 2 ：自动装箱与拆箱：Javassist 内置的编译器是不支持自动装箱与拆箱的。

        Integer i = 3;  // 在java代码中写合法 ， 用javassist 直接生成的话 是非法的。

        Integer i2 = new Integer(3); // 用javassist 必须这么些。


        // 3：测试帮助：当为变量CtClass.debugDump 设置 一个目录时，用javassist 生成的类和修改类
        //    都会被存放到这个目录。  默认没有设置目录的  CtClass.debugDump = null.


        // 所有用javassist 修改过的文件都会被存放到 当前目录的 dump 目录下。
        CtClass.debugDump = "./dump";
    }
}
