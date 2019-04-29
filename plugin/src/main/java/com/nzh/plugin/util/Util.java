package com.nzh.plugin.util;

import org.gradle.api.Project;

import java.net.URI;
import java.util.ArrayList;

import javassist.ClassPool;
import javassist.CtClass;
import javassist.NotFoundException;

/**
 * Created by 31414 on 2019/4/3.
 */

public class Util {
    public static final String CLASS_VIEW = "android.view.View";
    public static final String CLASS_FRAGMENT = "android.app.Fragment";
    public static final String CLASS_FRAGMENT2 = "android.support.v4.app.Fragment";
    private static final String INTERFACE = "android.view.View$OnClickListener";
    private static Util util = null;
    CtClass viewClass;
    CtClass fragmentClass;
    CtClass fragmentClass2;
    ClassPool pool;

    private ArrayList<String> activities; // 所有activity 全路径名。来自于Manifest.xml
    private String buildDir;    // 要被操作的class 目录
    private String packgeName; // android 工程的包名
    // 要被操作的 第三方包括android.jar ,v4.jar,v7.jar
    Project project;
    private ArrayList<String> libsPath;

    private Util(ClassPool pool) {
        this.pool = pool;
    }

    /**
     * 初始化工具
     *
     * @param pool       要操作的class 加入的类池
     * @param activis    所有的activity 名
     * @param buildDir   android 工程生成的class 所在路径
     * @param libsPath    操作 $buildDir 路径下的class 时 依赖的 库。（android.jar v4 v7等等）
     * @param packgeName android 工程包名
     * @param project    gradle插件
     * @return Util
     */
    public static Util init(ClassPool pool, ArrayList<String> activis, String buildDir, ArrayList<String> libsPath, String packgeName, Project project) {
        if (util == null) {
            synchronized (Util.class) {
                if (util == null) {
                    util = new Util(pool);
                    util.activities = activis;
                    util.buildDir = buildDir;
                    util.libsPath = libsPath;
                    util.packgeName = packgeName;
                    util.project = project;
                }
            }
        }
        return util;
    }

    public ArrayList<String> getLibPath() {
        return libsPath;
    }

    public String getBuildDir() {
        return buildDir;
    }

    public String getPacakgeName() {
        return packgeName;
    }

    public Project getProject() {
        return project;
    }

    public ArrayList<String> getActivities() {
        return activities;
    }

    public ClassPool getClassPool() {
        return pool;
    }

    public static Util getInstance() {
        return util;
    }

    // 初始化 View 类的CtClass
    private void checkViewCtClass() {
        if (viewClass == null) {
            try {
                viewClass = pool.get(CLASS_VIEW);
            } catch (NotFoundException e) {
                throw new IllegalArgumentException(e);
            }
        }
    }

    // 初始化 Fragment 类的CtClass
    private void checkFragmentCtClass() {
        if (fragmentClass == null) {
            try {
                fragmentClass = pool.get(CLASS_FRAGMENT);
            } catch (NotFoundException e) {
                throw new IllegalArgumentException(e);
            }
        }
        if (fragmentClass2 == null) {
            try {
                fragmentClass2 = pool.get(CLASS_FRAGMENT2);
            } catch (NotFoundException e) {
                throw new IllegalArgumentException(e);
            }
        }
    }

    public boolean isExtendsOfFragment(CtClass ctClass) {
        checkFragmentCtClass();

        // 直接或者间接继承自 View
        return ctClass.subclassOf(fragmentClass) || ctClass.subclassOf(fragmentClass2);
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

        if (viewClass != null) {
            viewClass.detach();
        }
        if (fragmentClass != null) {
            fragmentClass.detach();
        }
        if (fragmentClass2 != null) {
            fragmentClass2.detach();
        }
        pool = null;
        util = null;
        SuperClassExprEditor.isAlreadyAdd = false;

    }



}
