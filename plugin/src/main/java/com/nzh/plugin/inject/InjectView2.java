package com.nzh.plugin.inject;


import com.nzh.plugin.util.ActivityExprEditor;
import com.nzh.plugin.MyFieldInfo;
import com.nzh.plugin.util.SuperClassExprEditor;
import com.nzh.plugin.util.Util;

import java.util.ArrayList;
import java.util.Map;
import java.util.Set;

import javassist.CannotCompileException;
import javassist.ClassPool;
import javassist.CtClass;
import javassist.CtField;
import javassist.CtMember;
import javassist.CtMethod;
import javassist.CtNewMethod;
import javassist.NotFoundException;
import javassist.bytecode.AnnotationsAttribute;
import javassist.bytecode.Bytecode;
import javassist.bytecode.DuplicateMemberException;
import javassist.bytecode.FieldInfo;
import javassist.bytecode.annotation.Annotation;
import javassist.bytecode.annotation.IntegerMemberValue;
import javassist.compiler.CompileError;
import javassist.compiler.Javac;

import static com.nzh.plugin.Const.BINDVIEW_ANNOTATION;
import static com.nzh.plugin.Const.METHOD_NAME;
import static com.nzh.plugin.Const.METHOD_OnCreate;

/**
 * BindView 注解的实现 。
 */
public class InjectView2 {

    private static final String view = "android.app.Activity a = this;";
    private static final String template = " a.findViewById(";


    InjectType type;

    public InjectView2(InjectType type) {
        this.type = type;
    }

    public enum InjectType {
        /**
         * 仅初始化带注解的View
         */
        Only_Inject_By_Anno,
        /**
         * (暂时废弃)初始化带注解的View 和 id 和View名一致的   view.
         */
        Inject_By_Anno_And_ViewName

    }

    /**
     * 可以初始化带注解的View 和 id 和View名一致的   view.
     *
     * @param buildClassDir  要操作的字节码目录。（绝对路径）
     * @param androidLibPath 造作字节码时用到的 android.jar 的路径。（绝对路径）
     * @param activities     所有activity的全类名。
     * @param packageName    包名。
     * @throws Exception e
     */
    public void injectView(String buildClassDir, String androidLibPath, ArrayList<String> activities, String packageName) throws Exception {

        final String myClassPath = buildClassDir;
        String myClassLibPath = androidLibPath;

//        ClassPool pool = ClassPool.getDefault();
//        pool.insertClassPath(myClassPath);
//        pool.insertClassPath(myClassLibPath);
//        Util util = Util.init(pool);
        Util util = Util.getInstance();
        ClassPool pool = util.getClassPool();


        //1： 遍历所有 Activity 类
        for (String activity : activities) {

            String dstClass = activity;
            final CtClass ctClass = pool.get(dstClass);

//            String annotationClassName = BINDVIEW_ANNOTATION;
            String annotationMethodName = "value";

            //2: 遍历field ,找到 每个Activity类中的注解
            CtField[] fields = ctClass.getDeclaredFields();

            // 3: 保存 注解信息
            ArrayList<MyFieldInfo> list = new ArrayList<>();

            for (CtField field : fields) {
                if (field.getType().isPrimitive()) {
                    // 过滤基本数据类型
                    continue;
                }
                // Field 的类型
                String fieldTypeName = field.getType().getPackageName() + "." + field.getType().getSimpleName();

                if (!util.isExtendsOfView(fieldTypeName)) {
                    // 只处理View 类和其子类
                    continue;
                }

                MyFieldInfo fieldInfo;

                FieldInfo info = field.getFieldInfo();
                AnnotationsAttribute annotationAttr = (AnnotationsAttribute) info.getAttribute(AnnotationsAttribute.invisibleTag);

                // 没有使用@BindView 注解
                if (annotationAttr == null) {
                    if (type == InjectType.Only_Inject_By_Anno) {
                        // 只处理 @BindView 注解的变量
                        continue;
                    }
                }


                if (annotationAttr != null) {
                    Annotation annotation = annotationAttr.getAnnotation(BINDVIEW_ANNOTATION);
                    System.out.println("annotation"+(annotation==null));

                    IntegerMemberValue intValue = (IntegerMemberValue) annotation.getMemberValue(annotationMethodName);
                    int value = intValue.getValue();
                    System.out.println("类型:" + fieldTypeName + "  变量:" + field.getName() + "  id:" + value);

                    fieldInfo = new MyFieldInfo(fieldTypeName, field.getName(), value);
                } else {
                    // 未使用BindView 注解，这时 View 的变量名 必须和 id名一致。
                    fieldInfo = new MyFieldInfo(fieldTypeName, field.getName());

                }
                list.add(fieldInfo);
            }

            // 当前Activity没有需要注解的View
            if (list.size() == 0) {
                continue;
            }

            // 4：生成一个方法。用来初始化view.
            genMethodByNewMethod(ctClass, getMethodString2(list, METHOD_NAME, packageName));
            // 5：不要忘记写入，写入 初始化view方法代码
            ctClass.debugWriteFile(myClassPath);

            final CtMethod ctMethod;
            try {
                // 4.1： 没有异常表示 直接父类是 Activity 或者AppCompatActivity
                // 优化 判断直接父类是否是 Activity 或者AppCompatActivity (怎么引入v7)
                ctMethod = ctClass.getDeclaredMethod(METHOD_OnCreate);

            } catch (NotFoundException e) {
//                e.printStackTrace();
                //4.2 ： 有异常表示 直接父类 不是 (Activity 或者 AppCompatActivity )
                System.err.println(e.getMessage() + "\r\n 父类中注入");

                CtClass[] params = new CtClass[1];
                params[0] = pool.get("android.os.Bundle");

                // 在父类中 添加 初始化view方法的 声明和调用
                ArrayList<String> superClassNames = getSuperClassIncludeSetContentView(ctClass, METHOD_OnCreate, params, buildClassDir);

                for (String s : superClassNames) {
                    System.out.println("superClassNames--->" + s);
                    // 保证父类只注入一边
                    if (SuperClassExprEditor.isAlreadyAdd) {
                        break;
                    }
                    final CtClass superClass = pool.get(s);
                    try {
                        final CtMethod method = superClass.getDeclaredMethod(METHOD_OnCreate, params);
                        SuperClassExprEditor exprEditor = new SuperClassExprEditor(superClass, method, buildClassDir);
                        method.instrument(exprEditor);
                    } catch (Exception e1) {
                        System.out.println(e1.getMessage());
                    }
                }


                //5：父类中写入初始化view方法的 声明和调用。
                //  不要忘记写入
                ctClass.debugWriteFile(myClassPath);
                ctClass.defrost();
                continue;
            }

            // 4.1 在OnCreate 方法中 添加 初始化view 方法的调用
            ActivityExprEditor activityExprEditor = new ActivityExprEditor(ctClass, ctMethod, myClassPath);
            ctMethod.instrument(activityExprEditor);

        }

    }

    /**
     * 从currentCtClass 向上查找父类 。 在包含onCreate 方法的父类中 注入方法。
     *
     * @param currentCtClass 当前activity
     * @param methodName     onCreate 方法
     * @param param          onCreate 方法参数
     * @return 找到的父类
     */
    private ArrayList<String> getSuperClassIncludeSetContentView(CtClass currentCtClass, String methodName, CtClass[] param, final String buildClassDir) {
        ArrayList<String> list = new ArrayList<>();
        CtClass tempBase = null;
        String superClassName = null;
        try {
            tempBase = currentCtClass.getSuperclass();
            superClassName = tempBase.getName();
            while (!"android.app.Activity".equals(superClassName)) {
                list.add(superClassName);
                tempBase = tempBase.getSuperclass();
                superClassName = tempBase.getName();
            }
//            System.out.println("---->" + ctClass.getName());
//            final CtMethod method = ctClass.getDeclaredMethod(methodName, param);
//            if (METHOD_OnCreate.equals(method.getName())) {
//                return ctClass.getName();
//            } else {
//                return getSuperClassIncludeSetContentView(ctClass, methodName, param, buildClassDir);
//            }
        } catch (Exception e) {
            System.out.println("ignore--getSuperClassIncludeSetContentView---:" + e.getMessage());
            return list;
        }
        return list;
    }


    public String getMethodString2(ArrayList<MyFieldInfo> viewInfos, String methodName, String packageName) {

        StringBuilder sb = new StringBuilder();
        sb.append(view);
        for (MyFieldInfo f : viewInfos) {
            String name = f.getFieldViewName();
            String type = f.getFieldViewType();
            // 使用了注解的View,有id
            if (f.getFieldViewId() != 0) {
                int id = f.getFieldViewId();

                sb.append(name).
                        append("=").append("(").append(type).append(")").
                        append(template).append(id).append(");").append("\r\n");
            } else {
                // key 和value 一样。也就是 view的 名称和id一样。
                String idValue = name + "_"; // id 的值
                String genId = "int " + idValue + " = a.getResources().getIdentifier(\"" + name + "\",\"id\",\"" + packageName + "\");";
                sb.append(genId).append("\r\n").
                        append(name).
                        append("=").append("(").append(type).append(")").
                        append(template).append(idValue).append(");").append("\r\n");
            }
        }
        viewInfos.clear();
        String methodContent = sb.toString();
        String s = "public void " + methodName + "(){#}";
        String method = s.replaceAll("#", methodContent);
        return method;
    }


    /**
     * 生成方法代码
     *
     * @param viewInfo   初始化view需要的信息
     * @param methodName 方法名
     * @return 生成的方法代码
     */
    public String getMethodString(Map<String, Object> viewInfo, String methodName) {
        final String view = "android.app.Activity a = this;";
        final String template = " = (android.widget.TextView)a.findViewById(";
        Set<Map.Entry<String, Object>> set = viewInfo.entrySet();
        StringBuilder sb = new StringBuilder();
        sb.append(view);
        for (Map.Entry<String, Object> entry : set) {
            String field = entry.getKey();
            int value = (int) entry.getValue();
//            String result = String.format(template, field, value);
            sb.append(field).append(template).append(value).append(");").append("\r\n");
        }
        String methodContent = sb.toString();
        System.out.println("=============");
        System.out.println(methodContent);

        String s = "public void " + methodName + "(){#}";
        String method = s.replaceAll("#", methodContent);
        return method;
    }

    public void genMethodByCompileSource(CtClass c, Map<String, Object> viewInfo) {
//        String method = "public void testAddMethod(){#}".replaceAll("#", "");
        Bytecode b = new Bytecode(c.getClassFile().getConstPool(), 0, 0);
//
        Javac jc = new Javac(c);
        try {
            CtMember obj = jc.compile(getMethodString(viewInfo, METHOD_NAME));
            if (obj instanceof CtMethod)
                c.addMethod((CtMethod) obj);
        } catch (CompileError compileError) {
            compileError.printStackTrace();
        } catch (CannotCompileException e) {
            if (e instanceof DuplicateMemberException) {
                System.out.println("------ignore-----DuplicateMemberException");
            } else {
                e.printStackTrace();
            }
        }
//        CtMethod method1 = CtNewMethod.make(method,c);
//        c.addMethod(method1);
    }

    public void genMethodByNewMethod(CtClass cc, String method) {

        System.err.println("\r\n");
        System.err.println(method);
        try {
            CtMethod m3 = CtNewMethod.make(
                    method, cc);

            cc.addMethod(m3);
        } catch (CannotCompileException e) {
            if (e instanceof DuplicateMemberException) {
                System.err.println("------ignore------");
            } else {
                e.printStackTrace();
            }
        }
    }
}
