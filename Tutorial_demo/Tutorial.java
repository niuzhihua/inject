package com.nzh.javassit;


import javassist.ClassPool;
import javassist.CtClass;
import javassist.CtConstructor;
import javassist.CtMethod;
import javassist.CtNewMethod;

public class Tutorial {

    /**
     * CtClass ClassPool 基础介绍
     *
     * @throws Exception
     */
    public static void tutorial() throws Exception {


// 2: 生成新的class ,并插入方法

        ClassPool pool = ClassPool.getDefault();
        CtClass cc = pool.makeClass("Point");

        CtMethod m = CtNewMethod.make("testMethod", cc);
        cc.addMethod(m);
        cc.writeFile();

        // 以上代码定义了一个新的Point类，这个类并没有任何成员，可以用 CtNewMethod 生成新的方法，
        // 并调用addMethod() 方法 添加到 Point 类中。


        // 定义一个新的接口，并插入方法
        ClassPool pool2 = ClassPool.getDefault();
        CtClass myInterface = pool2.makeInterface("MyInterface");

        CtClass[] params = new CtClass[]{CtClass.intType, CtClass.booleanType};
        CtClass[] exceptions = new CtClass[]{};

        CtMethod abstractMethod = CtNewMethod.abstractMethod(CtClass.intType, "addMethodName",
                params, exceptions, myInterface);

        myInterface.addMethod(abstractMethod);
        // makeClass()方法不能创建interface, makeInterface() 才可以。使用CtNewMethod的
        // abstractMethod() 为interface创建抽象方法.


    }
}
