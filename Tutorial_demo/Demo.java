package com.nzh.javassit;


import org.gradle.cache.internal.FileAccess;

import javassist.CannotCompileException;
import javassist.ClassPath;
import javassist.ClassPool;
import javassist.CtClass;
import javassist.CtConstructor;
import javassist.CtField;
import javassist.CtMethod;
import javassist.CtNewMethod;
import javassist.bytecode.CodeAttribute;
import javassist.expr.Cast;
import javassist.expr.ExprEditor;
import javassist.expr.FieldAccess;
import javassist.expr.Instanceof;
import javassist.expr.MethodCall;
import javassist.expr.NewExpr;

public class Demo {

    // 测试添加多个类寻找路径 ： appendClassPath
    public static void test(ClassPool pool) throws Exception {

        ClassPath cp1 = pool.appendClassPath("d1");
        ClassPath cp2 = pool.appendClassPath("d2");
        ClassPath cp3 = pool.appendClassPath("d3");
        ClassPath cp4 = pool.appendClassPath("d4");
        System.out.println(pool.toString());
        pool.removeClassPath(cp3);
        System.out.println(pool.toString());
        pool.removeClassPath(cp4);
        System.out.println(pool.toString());
        pool.removeClassPath(cp2);
        System.out.println(pool.toString());
        pool.removeClassPath(cp1);
        System.out.println("最后:" + pool.toString());
        // assertTrue("[class path: ]".equals(pool.toString()));
    }

    // 测试 是否是 子类 或者父类
    public static void testSubtype(ClassPool sloader, String classA, String classB) throws Exception {

        CtClass cca = sloader.get(classA);

        CtClass ccb = sloader.get(classB);

        System.out.println(cca.subtypeOf(cca));

        System.out.println(classA + "是否是" + classB + "的子类：" + cca.subtypeOf(ccb));

        System.out.println(classA + "是否是Object的子类：" + cca.subtypeOf(sloader.get("java.lang.Object")));
    }

    // 创建 成员属性 ，并初始化值，向类中添加。
    public static void testAddFieldtoClass(ClassPool sloader) throws Exception {

        CtClass cc = sloader.get("test1.FieldInit");
        CtField f1 = new CtField(CtClass.intType, "f1", cc);
        cc.addField(f1, CtField.Initializer.byCall(cc, "get"));
        CtField f2 = CtField.make("public int f2 = 3;", cc);
        cc.addField(f2);
        CtField f3 = CtField.make("public int f3;", cc);
        cc.addField(f3);
        CtField f4 = CtField.make("public int f4 = this.f2 + 3;", cc);
        cc.addField(f4);
        CtField fi = CtField.make("public test1.FieldInit.FI fi = new test1.FieldInit.FI(this);", cc);
        cc.addField(fi);
//        testFieldInitHash = f1.hashCode();
        cc.writeFile();
//        Object obj = make(cc.getName());
//        int value = obj.getClass().getField("counter").getInt(obj);
//        assertEquals(1, value);
//        int value2 = obj.getClass().getField("f2").getInt(obj);
//        assertEquals(3, value2);
//        int value3 = obj.getClass().getField("f3").getInt(obj);
//        assertEquals(0, value3);
//        int value4 = obj.getClass().getField("f4").getInt(obj);
//        assertEquals(6, value4);
//        Object obfi = obj.getClass().getField("fi").get(obj);
//        assertTrue(obfi.getClass().getField("fi").get(obfi) == obj);

    }


    /**
     * 创建成员属性 ， 并向类中添加
     *
     * @param sloader
     * @throws Exception
     */
    public void testAddField2(ClassPool sloader) throws Exception {
        CtClass cc = sloader.get("test1.FieldInit2");
        CtField f = new CtField(CtClass.intType, "f1", cc);
        cc.addField(f, CtField.Initializer.byCall(cc, "get"));
        cc.writeFile();
//        try {
//            Object obj = make(cc.getName());
//            fail();
//        }
//        catch (Exception e) {
//            print("testFieldInit2: catch");
//        }

    }


    /**
     * 向方法最后插入一句代码
     *
     * @param sloader
     * @throws Exception
     */
    public static void testAddFragmentToMethod(ClassPool sloader) throws Exception {
        CtClass cc = sloader.get("test1.CalleeAfter");

        CtMethod m1 = cc.getDeclaredMethod("m1");
        m1.insertAfter("{ int k = 1; $_ = $_ + k; }", false);

        CtMethod m2 = cc.getDeclaredMethod("m2");
        m2.insertAfter("{ char k = 1; $_ = $_ + k; }", false);

        CtConstructor[] cons = cc.getDeclaredConstructors();
        cons[0].insertAfter("{ ++p; $_ = ($r)null; }", false);

        cc.writeFile();
//            Object obj = make(cc.getName());
//            assertEquals(15, invoke(obj, "test"));
    }

    /**
     * 向方法最后插入一句代码
     *
     * @param sloader
     * @throws Exception
     */
    public static void testAddFragmentToMethod2(ClassPool sloader) throws Exception {
        CtClass cc = sloader.get("test1.CalleeAfter2");

        CtMethod m1 = cc.getDeclaredMethod("m1");
        m1.insertAfter("$_ = 7; $_ = ($r)k1(0);", false);

        CtMethod m2 = cc.getDeclaredMethod("m2");
        m2.insertAfter("$_ = ($r)k2(0);", false);

        CtMethod m3 = cc.getDeclaredMethod("m3");
        m3.insertAfter("$_ = ($r)k3(0);", false);

        CtMethod m4 = cc.getDeclaredMethod("m4");
        try {
            m4.insertAfter("$_ = ($r)1;", false);
//            assertTrue(false);
        } catch (CannotCompileException e) {
            e.printStackTrace();
        }

        CtMethod m5 = cc.getDeclaredMethod("m5");
        m5.insertAfter("$_ = ($r)k5(0);", false);

        cc.writeFile();
//        Object obj = make(cc.getName());
//        assertEquals(17, invoke(obj, "test"));
    }

    // 向方法最后插入一句代码
    public void testAddFragmentToMethod3(ClassPool sloader) throws Exception {
        CtClass cc = sloader.get("test1.CalleeAfter3");
        CtMethod m1 = cc.getDeclaredMethod("m1");
        m1.insertAfter("value++;", true);
        cc.writeFile();
//        Object obj = make(cc.getName());
//        assertEquals(22, invoke(obj, "test"));
    }

    /**
     * 对方法体进行异常处理
     *
     * @param sloader
     * @throws Exception
     */
    public static void testAddCatchToMethod(ClassPool sloader) throws Exception {
        CtClass cc = sloader.get("test1.CalleeCatch");

        CtMethod m1 = cc.getDeclaredMethod("m1");
        m1.addCatch("{ System.out.println($e); return p; }",
                sloader.get("java.lang.Exception"));

        cc.writeFile();
//        Object obj = make(cc.getName());
//        assertEquals(3, invoke(obj, "test"));
    }

    /**
     * 添加新方法， 并在新方法中 return 现有的方法。
     * $proceed ： 作用相当于一个占位符。后面必须跟() .表示调用一个方法。 方法是 k1.
     */
    public static void testProceed(ClassPool sloader) throws Exception {
        CtClass cc = sloader.get("test1.Proceed");

        CtMethod m1 = CtNewMethod.make(
                "public int m1() { return $proceed(3); }",
                cc, "this", "k1");
        CtMethod m3 = CtNewMethod.make(
                "public int q(int i) { return p($1 + 1, $$); }", cc);
        cc.addMethod(m1);
        cc.addMethod(m3);
        CtMethod m4 = CtNewMethod.make(
                "public int q2() { return q(4); }", cc);
        cc.addMethod(m4);

        cc.writeFile();
//        Object obj = make(cc.getName());
//        assertEquals(3, invoke(obj, "m1"));
//        assertEquals(4, invoke(obj, "m2"));
//        assertEquals(9, invoke(obj, "q2"));
    }

    /**
     * 监听方法体的 方法调用，变量使用，new ,instance of  等 信息。
     *
     * @param sloader
     * @throws Exception
     */
    public void testProceed2(ClassPool sloader) throws Exception {
        CtClass cc = sloader.get("test1.Proceed2");
        CtMethod m1 = cc.getDeclaredMethod("k1");
        m1.instrument(new ExprEditor() {

            // 猜： 当前方法(m.getMethod()) 出现几次调用别的方法 ，那么就 回调几次这个方法。
            public void edit(MethodCall m) throws CannotCompileException {
                m.replace("{ $_ = $proceed($$); }");
            }

            // 猜 ： 当前方法中 出现几次 new  关键字 ，就 回调几次这个方法。
            public void edit(NewExpr m) throws CannotCompileException {
                m.replace("{ $_ = $proceed($$); }");
            }

            // 方法内使用了多少次 Field(m.getField)  就调用几次这个方法。
            public void edit(FieldAccess m) throws CannotCompileException {
                m.replace("{ $_ = $proceed($$); }");

            }

            // 方法内出现了几次 Instanceof 表达式 就调用几次这个方法。
            public void edit(Instanceof i) throws CannotCompileException {
                i.replace("{ $_ = $proceed($$); }");
            }

            // 方法内出现了 几次类型转换 就调用几次这个方法
            public void edit(Cast c) throws CannotCompileException {
                c.replace("{ $_ = $proceed($$); }");
            }
        });


        cc.writeFile();
//        Object obj = make(cc.getName());
//        assertEquals(2, invoke(obj, "k1"));
    }


    /**
     * 根据类的现有的方法， copy 一个相同方法。并调用 原方法。插入类中。
     * <br/>
     * 就是根据现有方法 新建一个重载方法。 插入到 类中。
     *
     * @param sloader
     * @throws Exception
     */
    public void testAddCopy_InsertMethod_Proceed3(ClassPool sloader) throws Exception {
        CtClass cc = sloader.get("test1.Proceed3");
        CtMethod m1 = cc.getDeclaredMethod("p");
        CtMethod m2 = CtNewMethod.copy(m1, cc, null);
        m1.setName(m1.getName() + "_orig");
        m2.setBody("{ return $proceed($1 + 1); }", "this", m1.getName());
        cc.addMethod(m2);
        cc.writeFile();
//        Object obj = make(cc.getName());
//        assertEquals(4, invoke(obj, "k1"));
    }

    /**
     * 给空方法 设置方法体
     *
     * @param sloader
     * @throws Exception
     */
    public static void testSetBody(ClassPool sloader) throws Exception {
        CtClass cc = sloader.get("test1.SetBody");
        CtMethod m1 = cc.getDeclaredMethod("m1");
        m1.setBody("{ int i = $1 * $2; return i; }");
        CtMethod m2 = cc.getDeclaredMethod("m2");
        m2.setBody("System.out.println(\"setbody: \" + $1);");

        CtMethod m3 = cc.getDeclaredMethod("m3");
        try {
            m3.setBody("value = 1; System.out.println(\"setbody: \" + $1);");
//            fail();
        } catch (CannotCompileException e) {
            // System.err.println(e);
        }

        CtConstructor cons
                = new CtConstructor(new CtClass[]{CtClass.intType}, cc);
        cons.setBody(null);
        cc.addConstructor(cons);

        cc.writeFile();
//        Object obj = make(cc.getName());
//        assertEquals(12, invoke(obj, "run"));

    }

    /**
     * 设置 静态代码块 和 构造方法的 方法体。
     *
     * @param sloader
     * @throws Exception
     */
    public static void testSetStatic_Constructor_Body(ClassPool sloader) throws Exception {
        CtClass cc = sloader.get("test1.StaticConsBody");
        CtConstructor cons = cc.getClassInitializer();   // 获取静态代码块
        cons.setBody(null);                             // 设置静态代码块 ，这里置空。

        cons = cc.getConstructors()[0];             // 获取构造方法
        cons.setBody(null);                         // 设置构造方法体  ，这里置空。

        cc.writeFile();
//        Object obj = make(cc.getName());
//        assertEquals(0, invoke(obj, "run"));
    }

    /**
     * 设置A 类 是B类的父类。并添加 无参和有参 的构造
     *
     * @param sloader
     * @throws Exception
     */
    public void testSetSuperclass_AddConstructor(ClassPool sloader) throws Exception {
        CtClass superClazz = sloader.get("java.io.File");
        CtClass cc = sloader.makeClass("test1.SetConsBody");
        cc.setSuperclass(superClazz);
        CtConstructor constructor = new CtConstructor(new CtClass[0], cc);
        constructor.setBody("super(\"MyFile\");");
        cc.addConstructor(constructor);

        constructor = new CtConstructor(new CtClass[]{CtClass.intType},
                cc);
        constructor.setBody("{ super(\"MyFile\"); }");
        cc.addConstructor(constructor);

        cc.addMethod(CtNewMethod.make(CtClass.voidType, "m1",
                null, null, null, cc));
        cc.addMethod(CtNewMethod.make(CtClass.intType, "m2",
                null, null, null, cc));
        cc.addMethod(CtNewMethod.make(sloader.get("int[]"), "m7",
                null, null, null, cc));

        cc.addMethod(CtNewMethod.make(
                "public int run() {"
                        + "  return (int)(m2() + m3() + m4() + m5() + m6() + 3); }", cc));
        cc.writeFile();
//        Object obj = make(cc.getName());
//        assertEquals(3, invoke(obj, "run"));

    }





}
