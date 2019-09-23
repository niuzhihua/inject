package com.nzh.javassit.sample;

import javassist.ClassPool;
import javassist.CtClass;
import javassist.CtMember;
import javassist.CtMethod;
import javassist.CtNewMethod;
import javassist.bytecode.Bytecode;
import javassist.compiler.Javac;

/**
 * 1：生成方法：拷贝类的现有的方法，并插入到当前类中。
 * 2：生成方法：编译一个代码块 ，产生一个方法，并插入到当前类中
 * 3: 生成方法：使用CtNewMethod 工具生成方法，并插入到当前类中.
 * 4: 生成方法：生成可变参数的方法，并插入到当前类中。
 * 5：生成方法：super 关键字的使用 ，生成在子类中调用父类的方法。
 */

public class MethodSample {

    /**
     * 1：拷贝类的现有的方法，并插入到当前类中。
     *
     * @param ctClass
     * @throws Exception
     */
    public static void genMethodByCopyMethod(CtClass ctClass) throws Exception {

        CtClass cc = ctClass;

        CtMethod g3Method = cc.getDeclaredMethod("g3");
        // g3Method ：原方法
        // g3_bak ： 新生成的方法名
        // cc : 拷贝到那个类
        // ?
        CtMethod newMethod = CtNewMethod.copy(g3Method, "niuzhihua", cc, null);

        // 写入
        cc.addMethod(newMethod);
        cc.writeFile();
        System.out.println("------修改完class文件后解冻一下-------");
        ctClass.defrost();
    }

    /**
     * 2：编译一个代码块 ，产生一个方法，并插入到当前类中
     *
     * @param c
     * @throws Exception
     */
    public static void genMethodByCompileSourceFragment(CtClass c) throws Exception {

        String line = "  public void testAddMethod(){System.out.println(678);}  ";
        Bytecode b = new Bytecode(c.getClassFile().getConstPool(), 0, 0);

        Javac jc = new Javac(b, c);
        CtMember obj = jc.compile(line);
        if (obj instanceof CtMethod)
            c.addMethod((CtMethod) obj);
//        else
//            c.addConstructor((CtConstructor) obj);
        c.writeFile();
        c.defrost();

    }

    /**
     * 3: 使用CtNewMethod 工具生成方法，并插入到当前类中.
     *
     * @param cc
     * @throws Exception
     */
    public static void genMethodByNewMethod(CtClass cc) throws Exception {

        CtMethod m1 = CtNewMethod.make(
                "public int genMethod1() { return $proceed(3); }",
                cc, "this", "k1");
        CtMethod m2 = CtNewMethod.make(
                "public int genMethod2() { return $proceed(3); }",
                cc, "another", "k2");
        CtMethod m3 = CtNewMethod.make(
                "public int genMethod3(int i) { return p($1 + 1, $$); }", cc);
        cc.addMethod(m1);
        cc.addMethod(m2);
        cc.addMethod(m3);
        CtMethod m4 = CtNewMethod.make(
                "public int genMethod4() { return q(4); }", cc);
        cc.addMethod(m4);

        cc.writeFile();

        cc.defrost();
    }

    /**
     * 4:生成可变参数的方法，并插入到当前类中。
     *
     * @throws Exception
     */
    public static void genVarArgsMethodByCompileSourceFragment() throws Exception {

    }

    /**
     * 5：在子类方法中生成 super.xxx() 方法。（调用父类的方法，也就是super的使用）
     *
     * @param pool
     * @param superClass 父类
     * @param subClass   子类
     * @throws Exception
     */
    public static void genSuperMethodCall(ClassPool pool, String superClass, String subClass)
            throws Exception {

        CtMethod f = pool.getMethod(superClass, "f");
        CtMethod g = pool.getMethod(superClass, "g");

        CtClass cc = pool.get(subClass);

        cc.addMethod(CtNewMethod.delegator(f, cc));
        cc.addMethod(CtNewMethod.delegator(g, cc));

        cc.writeFile();
        cc.defrost();

    }


}
