package com.nzh.plugin.inject;


import com.nzh.plugin.MyFieldInfo;
import com.nzh.plugin.util.FragmentExprEditor;
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
import static com.nzh.plugin.Const.METHOD_OnCreateView;
import static com.nzh.plugin.Const.NZH_Gen_Method;
import static com.nzh.plugin.Const.NZH_Gen_Method_Body;


public class InjectViewInFragment {


    public InjectViewInFragment() {

    }


    public void injectView(String buildClassDir, String packageName, ArrayList<String> fragmentList) throws Exception {

        final String myClassPath = buildClassDir;

        Util util = Util.getInstance();
        ClassPool pool = util.getClassPool();
        String annotationClassName = BINDVIEW_ANNOTATION;
        String annotationMethodName = "value";
        // 1 : 遍历所有Fragment
        for (String fragment : fragmentList) {
            System.out.println("---s---" + fragment);
            CtClass ctClass = pool.get(fragment);
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
                if (annotationAttr != null) {
                    System.out.println("annotationClassName" + annotationClassName);
                    Annotation annotation = annotationAttr.getAnnotation(annotationClassName);
                    System.out.println("annotation" + (annotation == null));

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

            // 当前fragment没有需要注解的View
            if (list.size() == 0) {
                continue;
            }

            // 4：生成一个方法。用来初始化view.
            genMethodByNewMethod(ctClass, getMethodString2(list, NZH_Gen_Method, packageName));
            // 5：不要忘记写入，写入 初始化view方法代码
            ctClass.debugWriteFile(myClassPath);

            final CtMethod ctMethod;
            try {
                // 4.1： 没有异常表示 直接父类是 Fragment 或者 V4 Fragment
                // 优化 1:是否有必要 判断直接父类是否是 Activity 或者AppCompatActivity (怎么引入v7)
                //   2： 像ButterKnife 一样 再提供一个api ： ButterKnife.bind(this)
                ctMethod = ctClass.getDeclaredMethod(METHOD_OnCreateView);

            } catch (NotFoundException e) {
//                e.printStackTrace();
                //改方案 ： 有异常表示 直接父类 不是 (Activity 或者 AppCompatActivity )
                // 可以在这里添加 METHOD_OnCreateView 方法。genOnCreateViewMethod
                System.err.println("重写" + METHOD_OnCreateView + "方法注入：");

                genMethodByNewMethod(ctClass, getOnCreateViewMethodString(list));
                //5：父类中写入初始化view方法的 声明和调用。
                //  不要忘记写入
                ctClass.debugWriteFile(myClassPath);
                ctClass.defrost();
                continue;
            }finally {
                list.clear();
            }

            // 4.1 在OnCreateView 方法中 添加 初始化view 方法的调用
            FragmentExprEditor fragmentExprEditor = new FragmentExprEditor(ctClass, ctMethod, myClassPath);
            ctMethod.instrument(fragmentExprEditor);

        }
    }


    public String getOnCreateViewMethodString(ArrayList<MyFieldInfo> viewInfos) {
        StringBuilder sb = new StringBuilder();
        sb.append("public android.view.View onCreateView(android.view.LayoutInflater inflater, android.view.ViewGroup container, android.os.Bundle savedInstanceState) {\r\n")
                .append("android.view.View v =  super.onCreateView(inflater, container, savedInstanceState);\r\n");

        for (MyFieldInfo f : viewInfos) {
            String name = f.getFieldViewName();
            String type = f.getFieldViewType();

            int id = f.getFieldViewId();

            sb.append(name).
                    append("=").append("(").append(type).append(")").
                    append(NZH_Gen_Method_Body).append(id).append(");").append("\r\n");

        }
        sb.append("return v;\r\n");
        sb.append("}");

        System.out.println("viewInfos" + viewInfos.size());
        System.out.println(sb.toString());
        return sb.toString();
    }

    public String getMethodString2(ArrayList<MyFieldInfo> viewInfos, String methodName, String packageName) {

        StringBuilder sb = new StringBuilder();
        for (MyFieldInfo f : viewInfos) {
            String name = f.getFieldViewName();
            String type = f.getFieldViewType();
            // 使用了注解的View,有id
            if (f.getFieldViewId() != 0) {
                int id = f.getFieldViewId();

                sb.append(name).
                        append("=").append("(").append(type).append(")").
                        append(NZH_Gen_Method_Body).append(id).append(");").append("\r\n");
            } else {
                // key 和value 一样。也就是 view的 名称和id一样。
                String idValue = name + "_"; // id 的值
                String genId = "int " + idValue + " = a.getResources().getIdentifier(\"" + name + "\",\"id\",\"" + packageName + "\");";
                sb.append(genId).append("\r\n").
                        append(name).
                        append("=").append("(").append(type).append(")").
                        append(NZH_Gen_Method_Body).append(idValue).append(");").append("\r\n");
            }
        }
        String methodContent = sb.toString();
        String s = "public void " + methodName + "{#}";
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
