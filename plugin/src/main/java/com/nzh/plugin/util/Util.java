package com.nzh.plugin.util;

import java.util.ArrayList;

import javassist.ClassPool;
import javassist.CtClass;
import javassist.NotFoundException;

/**
 * Created by 31414 on 2019/4/3.
 */

public class Util {
    public static final String CLASS_VIEW = "android.view.View";
    private static final String INTERFACE = "android.view.View$OnClickListener";
    private static Util util = null;
    CtClass viewClass;
    ClassPool pool;

    private ArrayList<String> activities;

    private Util(ClassPool pool) {
        this.pool = pool;
    }

    public static Util init(ClassPool pool, ArrayList<String> activis) {
        if (util == null) {
            synchronized (Util.class) {
                if (util == null) {
                    util = new Util(pool);
                    util.activities = activis;
                }
            }
        }
        return util;
    }

    public ClassPool getClassPool() {
        return pool;
    }

    public static Util getInstance() {
        return util;
    }

    // 初始化 View 类的CtClass
    public void checkViewCtClass() {
        if (viewClass == null) {
            try {
                viewClass = pool.get(CLASS_VIEW);
            } catch (NotFoundException e) {
                throw new IllegalArgumentException(e);
            }
        }
    }

    /**
     * 返回是否是View 的子类
     *
     * @param ctClass ctClass
     * @return true: 直接或者间接继承自 View
     */
    public boolean isExtendsOfView(CtClass ctClass) {
        checkViewCtClass();

        // 直接或者间接继承自 View
        return ctClass.subclassOf(viewClass);
    }

    /**
     * 返回是否是View 的子类
     *
     * @param className 全类名
     * @return 直接或者间接继承自 View
     */
    public boolean isExtendsOfView(String className) {

        checkViewCtClass();
        CtClass ctClass = null;
        try {
            ctClass = pool.get(className);
        } catch (NotFoundException e) {
            e.printStackTrace();
            return false;
        }
        // 直接或者间接继承自 View
        return ctClass.subclassOf(viewClass);
    }

    /**
     * 返回本类是否 实现了 View的 OnclickListener
     *
     * @param ctClass 需要设置监听的类
     * @return true: 实现了 View的OnclickListener 接口
     */
    public boolean isImplementsOnclickListener(CtClass ctClass) {
        CtClass[] interfaces = null;
        try {
            interfaces = ctClass.getInterfaces();
            for (CtClass i : interfaces) {
                System.out.println(i.getName());
                if (INTERFACE.equals(i.getName())) {
                    return true;
                }
            }
        } catch (NotFoundException e) {
            e.printStackTrace();
        }

        return false;
    }

    public CtClass getOnclickListenerClass() {
        try {
            return pool.get(INTERFACE);
        } catch (NotFoundException e) {
            e.printStackTrace();
        }
        return null;
    }

    public void release() {
        try {
            for (String activity : activities) {
                System.out.println("------detach-start-------");
                CtClass ctClass = pool.get(activity);
                ctClass.detach();
                System.out.println("------detach-end-------" + activity);
            }
        } catch (NotFoundException e) {
            e.printStackTrace();
        }
        if (activities != null) {
            for (String activity : activities) {

                System.out.println("------activity-------" + activity);
            }
            System.out.println("------activities-clear-------");
            activities.clear();
        }
        pool = null;
        util = null;
        SuperClassExprEditor.isAlreadyAdd = false;

    }
}
