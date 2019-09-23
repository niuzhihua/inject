package com.nzh.javassit.sample;

import javassist.ClassPool;
import javassist.CtClass;
import javassist.CtMethod;
import javassist.CtNewMethod;
import javassist.Modifier;

/**
 * 1:生成接口：根据现有的类， 抽象出方法 生成接口。并设置子类。
 * 2:生成接口：指定一个接口名，编译一段 方法代码 ，生成接口。 注意方法 只能用基本数据类型  和 String 类型。
 */
public class InterfaceSample {

    //https://github.com/jboss-javassist/javassist/blob/master/src/test/javassist/JvstTest.java
    // line:725


    /**
     * 1：为 className 生成接口。
     *
     * @param pool
     * @param className
     */
    public static void genInterface4Class(ClassPool pool, String className, String interfaceName) throws Exception {

        CtClass IMyInterface = pool.makeInterface(interfaceName);

        CtClass targetClass = pool.get(className);

        CtMethod[] methods = targetClass.getDeclaredMethods();

        for (CtMethod m : methods) {

            // 获取方法的 访问权限  返回值 方法名 。
            String abstractMethod = Modifier.toString(m.getModifiers()) + " " +
                    m.getReturnType().getName() + " " +
                    m.getName();

            // 获取方法的参数列表
            CtClass[] parameterTypes = m.getParameterTypes();
            String paramList = "";
            for (int i = 0; i < parameterTypes.length; i++) {
                CtClass c = parameterTypes[i];

                if (i < parameterTypes.length - 1) {
                    paramList += c.getName() + " p" + i + ",";
                } else {
                    paramList += c.getName() + " p" + i;
                }
            }
            // 无参的方法
            if (paramList.length() == 0) {
                abstractMethod += "();";
            } else {
                // 有参数的方法
                abstractMethod = abstractMethod + "(" + paramList + ");";
            }

            System.out.println("abstractMethod  ->" + abstractMethod);
            CtMethod absMethod = CtNewMethod.make(abstractMethod, IMyInterface);
            IMyInterface.addMethod(absMethod);

        }

        targetClass.addInterface(IMyInterface);

        // 接口
        IMyInterface.writeFile();
        // 接口的实现类
        targetClass.writeFile();

        targetClass.defrost();
        IMyInterface.defrost();


    }


    /**
     * 2：编译一段 方法代码 ，生成接口.
     *
     * @param pool
     * @param interfaceName
     * @throws Exception
     */
    public static void genInterfaceByCompileMethod(ClassPool pool, String interfaceName) throws Exception {

        CtClass IMyInterface = pool.makeInterface(interfaceName);

        String methodSource = "public abstract  String getStu(String s);";

        CtMethod absMethod = CtNewMethod.make(methodSource, IMyInterface);

        IMyInterface.addMethod(absMethod);

        IMyInterface.writeFile();
        IMyInterface.defrost();

    }

    /**
     * 3:生成接口，并生成指定的任意抽象方法。
     * @param pool
     * @param interfaceName
     * @throws Exception
     */
    public static void genInterfaceByCommonMethod(ClassPool pool, String interfaceName) throws Exception {

        CtClass IMyInterface3 = pool.makeInterface(interfaceName);

        // 接口方法的返回值类型
        CtClass returnTypeCls = pool.get("lsn.javassit.nzh.com.javassit.Student");

        // 接口方法的参数类型
        CtClass paramTypeCls = pool.get("lsn.javassit.nzh.com.javassit.Student");
        CtClass paramTypeCls2 = pool.get("java.lang.String");

        CtClass[] params = {paramTypeCls, CtClass.intType, paramTypeCls2};

        // 接口方法抛出的异常类型
        CtClass exceptionTypeCls = pool.get("java.lang.Exception");
        CtClass[] exceptions = {exceptionTypeCls};

        String methodName = "request";

        CtMethod method = CtNewMethod.abstractMethod(returnTypeCls, methodName, params, exceptions, IMyInterface3);

        IMyInterface3.addMethod(method);

        IMyInterface3.writeFile();
        IMyInterface3.defrost();

    }


}
