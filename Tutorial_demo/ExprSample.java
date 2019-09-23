package com.nzh.javassit.sample;


import javassist.CannotCompileException;
import javassist.ClassPool;
import javassist.CtClass;
import javassist.CtMethod;
import javassist.CtNewMethod;
import javassist.NotFoundException;
import javassist.bytecode.MethodInfo;
import javassist.expr.Cast;
import javassist.expr.ConstructorCall;
import javassist.expr.ExprEditor;
import javassist.expr.FieldAccess;
import javassist.expr.Handler;
import javassist.expr.Instanceof;
import javassist.expr.MethodCall;
import javassist.expr.NewArray;
import javassist.expr.NewExpr;

/*
 * 1：ExprEditor 对象 ： 结合javassist定义的表达式 来编辑方法体。 ExprEditor 的作用就是用来修改方法体的。
 *    method.instrument(new ExprEditor() {}
 *
 *    当调用 method.instrument 方法时 传入ExprEditor对象的方法中对象后，如果复写了 edit 方法，那么就会回调edit方法。
 *    回调机制 在代码的注释上写了。
 *
 * 2：$proceed 理解： 用在 ExprEditor对象的方法中。m对象 就是在method 方法体的编译阶段的 当前方法。
 *
 *
 *   // m对象：假如有method 方法体有3方法(test1(),test2(),test3())，那么edit(MethodCall m) 就会被调用3次。每一次调用edit(MethodCall m)时，
 *   //       m对象一次代表test1(),test2(),test3() 等方法。  总之表示编译方法体时，方法体中的当前方法。
 *
 *    $proceed 的意思就是当前编译到的方法，即m.getMethod(). $proceed是用来操作方法体的。
 *    可以用$proceed 更改调用方法时的参数。
 *
 *
 *    例如：
 *      edit(MethodCall m) {
 *
 *          if (m.getMethodName().equals("call2")) {
                     // 这样写表示： call2方法的传参，直接传200.
                    m.replace("{$_ = $proceed(200);}");

                     //这样写表示： 在调用call2 方法前后插入代码。  $$ 就表示 call2 方法的参数列表。这样写就是不修改call2方法的传参。
                    m.replace("{insert code before；$_ = $proceed($$); insert code after；}");

                    // 这样写表示：下面表达式 表示 不对 call2 方法的调用做任何修改。 $_ 表示返回值。
                    m.replace("{$_ = $proceed($$);}");

                    //这样写表示： 用空代码块替换掉当前call2 方法，也就是 删除了call2 方法。
                    m.replace("{}");

                    // 表示 删除call2 方法 后，有添加了 call4() 方法的调用。
                    m.replace("{}call4();");
             }
 *
 *   }
 *
 *
 */
public class ExprSample {

    /**
     * 1： 1：ExprEditor 对象简介。
     */
    public static void testExprEditor(ClassPool pool, final String className) throws Exception {

        CtClass cc = pool.get(className);

        final CtMethod m1 = cc.getDeclaredMethod("k0");

        m1.instrument(new ExprEditor() {
            /**
             * k0 方法中有几次方法调用，就调用几次这个方法。
             */
            public void edit(MethodCall m) throws CannotCompileException {

                System.out.println("在" + m1.getName() + "方法中调用了" + m.getMethodName() + "方法");
//                CtMethod cm = m.getMethod();
//                System.out.println(cm.getParameterTypes()[0].getName());
//                System.out.println(cm.getReturnType().getName());

            }

            /**
             * 在k0 方法中有几次对象创建，也就是new关键字产生了几次 ，那么就回调几次这个方法。
             */
            @Override
            public void edit(NewExpr e) throws CannotCompileException {
                try {
                    System.out.println("在" + m1.getName() + "方法中创建了对象：" + e.getClassName() + "(" + e.getConstructor().getName() + ")");
                } catch (NotFoundException e1) {
                    e1.printStackTrace();
                }
            }

            @Override
            public void edit(NewArray a) throws CannotCompileException {
                super.edit(a);
            }

            @Override
            public void edit(ConstructorCall c) throws CannotCompileException {
                super.edit(c);
            }

            /**
             * k0 方法中有几次成员属性的使用， 就调用几次这个方法。
             *  注意： 方法内声明的变量不算。  类的成员才算。静态成员和非静态都算。
             */
            @Override
            public void edit(FieldAccess f) throws CannotCompileException {
                System.out.println("在" + m1.getName() + "方法中使用了属性：" + f.getFieldName());
            }

            @Override
            public void edit(Instanceof i) throws CannotCompileException {
                super.edit(i);
            }

            @Override
            public void edit(Cast c) throws CannotCompileException {
                super.edit(c);


            }

            /**
             * k0 方法中有几次 try-catch 代码 （必须有catch代码），就调用几次这个方法。
             */
            @Override
            public void edit(Handler h) throws CannotCompileException {
                try {
                    System.out.println("在" + m1.getName() + "方法中使用了try-catch：" + h.getType().getName());
                } catch (NotFoundException e) {
                    e.printStackTrace();
                }
            }


        });

        cc.writeFile();
        cc.defrost();
//        Object obj = make(cc.getName());
//        assertEquals(12, invoke(obj, "k0"));
    }


    /**
     * 2： 重要： 编译时修改方法体中 方法的调用
     */
    public static void testExprProceed(ClassPool sloader, String className) throws Exception {

        CtClass cc = sloader.get(className);
        CtMethod callMethod = cc.getDeclaredMethod("call");
        callMethod.instrument(new ExprEditor() {

            @Override
            public void edit(MethodCall m) throws CannotCompileException {


                if (m.getMethodName().equals("call1")) {

                    m.replace("{$_ = 100;}");

                } else if (m.getMethodName().equals("call2")) {

                    m.replace("{$_ = $proceed(200);}");

                } else if (m.getMethodName().equals("call3")) {

                    m.replace("{}call4();");
                }
//                m.replace("{$_ = $proceed($$);}");
                //
                // m.replace("{$_ = $proceed($$);}");

            }
        });

        cc.writeFile();
        cc.defrost();
    }
}
