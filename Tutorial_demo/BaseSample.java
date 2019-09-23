package com.nzh.javassit.sample;


import javassist.ClassPool;

/*
 *
 * 一、ClassPool 基本介绍和使用
 *
 * 0: ClassPool ： 是存放了CtClass 的容器。
 * 1: 一旦一个CtClass 对象被创建出来，就会添加进 ClassPool
 * 2: 若调用了CtClass 的 detach() 方法，那么CtClass就被从ClassPool中移除了。
 * 3: 当调用CtClass 的 detach() 方法后，就不能调用任何方法了 。此时，必须从ClassPool中取出新
 *    的实例，才能用CtClass.新的实例 用 pool.get() 方法获取。
 *
 * 4: ClassPool 对象一旦被回收，里面的CtClass 都不能用了，这时就需要重新构建ClassPool.
 *
 * 5：ClassPool 的委托机制 :
 *
 *      ClassPool parent = ClassPool.getDefault();
 *      ClassPool child = new ClassPool(parent);
 *      child.appendSystemPath();
 *
 *      child.childFirstLookup = true;
 *
 *      默认情况下，用child.get（）方法找类时，会先从父ClassPool 开始找。
 *      如果设置了 childFirstLookup = true ，则 先从child 开始查找。
 *
 *      child.get("com.xxx.Student")
 *
 * 二、冻结的class 介绍
 *
 * 如果CtClass对象被转换为 class文件（writeFile()），或者Class(toClass() ),
 * 或者ByteCode ( toBytecode()),javassist 框架就会冻结这个 CtClass 对象。 这时修改这个CtClass 就不允许了。
 *
 * 此时，必须调用defrost()解冻方法 才能操作CtClass对象.
 *
 *
 *      CtClass c = 。。。;
 *      cc.writeFile();
 *      cc.defrost();   // writeFile 后必须解冻一下 CtClass. 才可以后续操作。
 *      cc.setSuperclass();   // ok 的 后续操作。
 *      解冻以后，对CtClass 的操作就可以了。
 * <p/>
 *
 *
 *  三、javassist 使用时注意
 *
 *
 *  1：javassist 不能用来删除类的信息（Field，Method）。但是可以更改方法，属性成员的名称。
 *  2：javassist 也不能给已有的方法添加参数，但是可以创建新的Method ,并添加参数 插入到类中。
 *
 *          void move(int newX, int newY)
 *          {
 *              x = newX; y = newY;   // 假设要给这个方法添加一个int参数。只能新加一个方法
 *          }
 *
 *          void move(int newX, int newY, int newZ) {
 *              do what you want with newZ.
 *                move(newX, newY);
 *          }
 */
public class BaseSample {


    public static void test() {
        ClassPool pool = null;
    }
}
