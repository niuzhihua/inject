package com.nzh.javassit;


import javassist.CtConstructor;
import javassist.CtMethod;

public class Tutorial_4_1 {

    /**
     * $ 通配符 介绍：
     * <p>
     * 在方法体开始和结束位置 插入代码 ,（可以用$开头的通配符 ）
     *
     * @throws Exception
     */
    public static void test() throws Exception {


        //1: CtMethod 和 CtConstructor 提供了api 能够在方法中插入代码片段。插入的代码 直接写文本字符就可以，javassist 有简单编译代码功能，
        // 编译这个添加的代码后 连接到原方法上。


        CtConstructor ctConstructor = null;
        CtMethod ctMethod = null;

        ctMethod.insertAt(0, ""); // 在指定位置插入代码
        ctMethod.insertBefore("");
        ctMethod.insertAfter("");

        // 2： 异常抓取
        ctMethod.addCatch("", null);  // 表示用try catch 方式抓住方法异常。

//        CtMethod m = ...;
//        CtClass etype = ClassPool.getDefault().get("java.io.IOException");
        // 注意，异常抓取时，插入的代码最后必须 有返回值。 可以 直接 throw  或者 return 。
        // $e : 固定写法
//        m.addCatch("{ System.out.println($e); throw $e; }", etype);

        // 生成后的代码
//        try {
//            the original method body
//        }
//        catch (java.io.IOException e) {
//            System.out.println(e);
//            throw e;
//        }


        // 3: 使用$开头的 通配符 ，一共有10个 。用来向方法体插入代码


        // $0, $1, $2,  ： 表示方法的参数， $1 就是方法的第一个参数 ，$2 是第二个参数，以此类推

//        class Point {
//            int x, y;
//            void move(int dx, int dy) { x += dx; y += dy; }
//        }

//        ClassPool pool = ClassPool.getDefault();
//        CtClass cc = pool.get("Point");
//        CtMethod m = cc.getDeclaredMethod("move");

//        插入方法代码时 使用方法的参数 ，可以这么获取
//        m.insertBefore("{ System.out.println($1); System.out.println($2); }");
//        cc.writeFile();

        // 结果：

//        class Point {
//            int x, y;
//            void move(int dx, int dy) {
//                { System.out.println(dx); System.out.println(dy); }   // insertBefore插入后的结果代码以{}包着。
//                x += dx; y += dy;
//            }
//        }


        // $args : 这个表示符 表示参数列表 ，类型为 Object[] , 如果参数列表中有基本类型，则自动装箱为类。例如int 装箱为java.lang.Integer.

//          $args = Object[]
//          $args[0] = 第一个参数  = $1  // 注意如果只有一个参数，并且是基本类型(例如int)。那么$args[0] 就是Integer

        // $0  =  this    // 注意 $0 表示 this .当前对象。

        //$$ ：表示参数列表的缩写。一般用于方法的调用 ，和 $Proceed 配合使用

//          假如参数有3个 ，那么如下是一样的。
//          move($$) = move($1, $2, $3)
//          exMove($$, context) = exMove($1, $2, $3, context)

//          假如没有参数
//          move($$) = move()

        // $cflow ：表示递归的调用。

//        int fact(int n) {
//            if (n <= 1)
//                return n;
//            else
//                return n * fact(n - 1);
//        }
//
//        CtMethod cm = ...;
//        cm.useCflow("fact");   // 首先声明使用递归
//
        // 在方法中插入代码时使用递归
//        cm.insertBefore("if ($cflow(fact) == 0)"
//                + "    System.out.println(\"fact \" + $1);");

        // $r ： 表示方法的返回值类型 。
        // $_ ： 是一个变量，类型为当前方法的返回值类型。如果返回值类型是 void. 那么$_变量的类型是Object . 值为null.

//        Object result = ... ;
//        $_ = ($r)result;


        // 如果当前方法返回的是基本数据类型(例如int) ,  当操作的变量var为基本类型时，正常转换。
        // 如果当前方法返回的是基本数据类型(例如int) ,  当操作的变量var为引用类型时(Integer)，那么就拆箱转为int.
        // 了解以上两条后 正常使用即可。

        // 如果方法返回值类型是void,$r 不做任何转换。即使写了return 也没事。

        // 当前方法返回值为void时，这么写也合法。
//        return ($r)result;


        //$w ： 自动装箱用的。

//        Integer i = ($w)5;

//        如果不是基本数据类型，就没有用。


        //$type : 是一个变量，类型是当前方法的返回值类型 的Class 。例如 当前方法返回String,  $type = String.class
//                  如果是构造方法， $type = Void.class

        //$class : 是一个变量, 类型为Class,表示当前方法所在类的类型。  也就是$0 的类型。
//                   例如当前类是  Student . 其中有个方法 public void show(){} ,那么在show方法中这么写 ：

//                  $class  = Student.class
//                  $0 = this = Student对象

        // $sig ： 是一个变量，类型为Class数组。表示当前方法的参数类型。依次按照顺序。   例如当前方法如下：
//                    public void test(int a,String b){}
//            Class[] classes = new Class[]{int.class,String.class};
//            $sig = classes ;


        //


    }
}
