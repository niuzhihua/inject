package com.nzh.javassit.sample;

import com.android.dx.dex.file.DexFile;

import javassist.ClassPool;
import javassist.CtClass;
import javassist.CtMethod;
import javassist.CtNewMethod;
import javassist.Modifier;

/*
 *
 * CtClass 介绍：
 *
 * 用来操作Class字节码文件的工具。可以获取类的信息，例如 getName(), getSuperclass(), getMethods() 等等。
 * 也可以修改类的信息。例如 添加 Field ( cc.addField() ), Constructor, Method 等等。
 *
 *  1: 修改类的名称：         cc.setName("MyNewName");
 *
 *  2: 通过CtClass 来获取类的Class 对象 :  Class clazz = cc.toClass();
 *
 *  3: CtClass修改设置：若不准修改CtClass, 则需要调用 CtClass的 stopPruning(boolen) 方法来设置
 *
 *       cc.stopPruning(true);   // 设置不准修改 。 默认为false
 *       cc.writeFile();         // 结果是没有转化成功的。
 *
 *  案例：
 *
 *   1：生成新类：copy现有的类oldClassName ,生成新的类newClassName（也就是重命名了）
 *   2：生成新类：执行类名，生成新类， 并插入自定义的方法(普通和 抽象方法 )。如果有抽象方法，那么生成的类 自动成为 抽象类。
 *
 *
 */
public class ClassSample {


    /**
     * 1：copy现有的类oldClassName ,生成新的类newClassName（也就是重命名了）
     *
     * @param pool
     * @param oldClassName 原来的类 （全类名）
     * @param newClassName （生成后的类）
     * @throws Exception
     */
    public static void testRenameClassName(ClassPool pool, String oldClassName, String newClassName) throws Exception {


        CtClass cc = pool.get(oldClassName);

        cc.setName(newClassName);

        // 新生成的类在 ：工程/newName  路径下
        cc.writeFile();

        cc.defrost();

        // 如果调用了 defrose 冻结方法，就不能使用cc了。 那么就只能重新创建出 CtClass .
//        CtClass cc2 = pool.getAndRename(oldName, "Niu2");
//        // 新生成的Niu2.class 在工程/  路径下
//        cc2.writeFile();
//        cc2.defrost();

    }

    /**
     * 2: 生成类，并插入普通方法 和 抽象。如果有抽象方法，那么生成的类 自动成为 抽象类。
     *
     * @param pool
     * @param className
     * @throws Exception
     */
    public static void genAbstactClass(ClassPool pool, String className) throws Exception {

        CtClass ctClass = pool.makeClass(className);

        // 返回值类型
        CtClass returnType = pool.get("lsn.javassit.nzh.com.javassit.Student");

        // 方法名
        String abstractMethodName = "request";
        //参数列表
        CtClass param = pool.get("java.lang.String");
        CtClass[] paramList = {returnType, CtClass.intType, param};

        // 生成抽象方法1
        CtMethod abstractMethod = new CtMethod(returnType, abstractMethodName + "1", paramList, ctClass);
        // 生成抽象方法1.2
        CtMethod abstractMethod2 = CtNewMethod.make(
                "public abstract int abstractMethod2(lsn.javassit.nzh.com.javassit.Student i);", ctClass);
        // 生成抽象方法1.3
        CtMethod abstractMethod3 = CtNewMethod.make(
                "public abstract lsn.javassit.nzh.com.javassit.Student abstractMethod3(java.lang.String i);", ctClass);

        // 生成普通成员方法2

        int modifier = Modifier.PUBLIC;
        CtClass returnType2 = returnType;
        String methodName2 = "request2";
        CtClass[] paramList2 = {returnType, CtClass.intType, param};
        CtClass exceptionCls = pool.get("java.lang.Exception");
        CtClass[] exceptions = {exceptionCls};

        // 方法的实现
        String body = "return $1;";   // $1 表示参数列表中第一个参数
        String body2 = "return new lsn.javassit.nzh.com.javassit.Student();";
        String body3 = "return new lsn.javassit.nzh.com.javassit.Student(\"niuzhihua\",888);";


        // 访问修饰符 | 返回值类型 | 方法名 | 参数列表 | 异常类型 | 方法体 | 方法所在类
        CtMethod method2 = CtNewMethod.make(modifier, returnType2, methodName2, paramList2, exceptions, body3, ctClass);

        //  生成静态方法3 ： 静态方法
        CtMethod staticMethod3 = CtNewMethod.make(modifier | Modifier.STATIC,
                returnType2, methodName2 + "3", paramList2, exceptions, body, ctClass);


        // 生成final方法4 ： final 静态方法
        CtMethod finalMethod4 = CtNewMethod.make(modifier | Modifier.FINAL,
                returnType2, methodName2 + "4", paramList2, exceptions, body, ctClass);


        // 生成普通方法5
        CtMethod method5 = new CtMethod(returnType, abstractMethodName + "5", paramList, ctClass);
        method5.setModifiers(Modifier.PRIVATE);
        method5.setBody(body2);


        // 添加 三种方式生成的 抽象方法
        ctClass.addMethod(abstractMethod);
        ctClass.addMethod(abstractMethod2);
        ctClass.addMethod(abstractMethod3);

        // 添加普通方法
        ctClass.addMethod(method2);

        ctClass.addMethod(staticMethod3);
        ctClass.addMethod(finalMethod4);
        ctClass.addMethod(method5);

        ctClass.writeFile();
        ctClass.defrost();

    }

    public static void setSuperClass() throws Exception {
        //https://github.com/jboss-javassist/javassist/blob/master/src/test/javassist/JvstTest2.java
        // 169

        //https://github.com/jboss-javassist/javassist/blob/master/src/test/javassist/JvstTest3.java
        //712

        // annotation

        //
    }


}
