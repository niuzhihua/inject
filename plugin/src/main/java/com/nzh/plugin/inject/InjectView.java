package com.nzh.plugin.inject;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import javassist.CannotCompileException;
import javassist.ClassPool;
import javassist.CtClass;
import javassist.CtField;
import javassist.CtMember;
import javassist.CtMethod;
import javassist.CtNewMethod;
import javassist.bytecode.AnnotationsAttribute;
import javassist.bytecode.Bytecode;
import javassist.bytecode.DuplicateMemberException;
import javassist.bytecode.FieldInfo;
import javassist.bytecode.annotation.Annotation;
import javassist.bytecode.annotation.IntegerMemberValue;
import javassist.compiler.CompileError;
import javassist.compiler.Javac;
import javassist.expr.ExprEditor;
import javassist.expr.FieldAccess;
import javassist.expr.MethodCall;

public class InjectView {

    public static final String METHOD_OnCreate = "onCreate";
    public static final String METHOD_NAME = "nzhInitView";

    /**
     * 用来初始化带注解的 View.
     *
     * @param buildClassDir  要操作的字节码目录。（绝对路径）
     * @param androidLibPath 造作字节码时用到的 android.jar 的路径。（绝对路径）
     * @param activities     所有activity的全类名。
     * @throws Exception e
     */
    public static void injectView(String buildClassDir, String androidLibPath, ArrayList<String> activities) throws Exception {

        final String myClassPath = buildClassDir;
        String myClassLibPath = androidLibPath;

        ClassPool pool = ClassPool.getDefault();
        pool.insertClassPath(myClassPath);
        pool.insertClassPath(myClassLibPath);

        for (String activity : activities) {

            String dstClass = activity;
            final CtClass ctClass = pool.get(dstClass);
            System.out.println("----ctClass---" + ctClass.getName());

            String annotationClassName = "lsn.javassit.nzh.com.javassit.BindView";
            String annotationMethodName = "value";

            CtField[] fields = ctClass.getDeclaredFields();

            // 保存使用注解的view  , key: view的变量名  value : id 值
            final Map<String, Object> map = new HashMap<>();
            for (CtField field : fields) {
                String fieldTypeName = field.getType().getPackageName() + "." + field.getType().getSimpleName();

                FieldInfo info = field.getFieldInfo();
                AnnotationsAttribute annotationAttr = (AnnotationsAttribute) info.getAttribute(AnnotationsAttribute.invisibleTag);
                // 使用BindView 注解

                Annotation annotation = annotationAttr.getAnnotation(annotationClassName);
                if (annotation != null) {
                    IntegerMemberValue intValue = (IntegerMemberValue) annotation.getMemberValue(annotationMethodName);
                    int value = intValue.getValue();
                    System.out.println("value:" + value + "-name:" + field.getName());
                    System.out.println("fieldTypeName:" + fieldTypeName + "-->" + "android.widget.TextView".equals(fieldTypeName));
                    map.put(field.getName(), value);
                }

            }

            // 生成一个方法。用来初始化view.
            genMethodByNewMethod(ctClass, getMethodString(map, METHOD_NAME));
            final CtMethod ctMethod = ctClass.getDeclaredMethod(METHOD_OnCreate);
            ctMethod.instrument(new ExprEditor() {
                @Override
                public void edit(FieldAccess f) throws CannotCompileException {
                    super.edit(f);
             /*       String usedField = f.getFieldName();

                    if (map.containsKey(usedField)) {

                        int value = (Integer) map.get(usedField);

                        String result = String.format(template, usedField, value);
                        System.out.println("eee----" + f.getLineNumber() + "--" + result);
                        ctMethod.insertAt(f.getLineNumber(), result);

                        System.out.println(result);
                        map.remove(usedField);
                    }*/
                }

                // onCreate 方法中有几次方法的调用，这个edit 方法就会被回掉几次。
                @Override
                public void edit(MethodCall c) throws CannotCompileException {
                    super.edit(c);
                    if ("setContentView".equals(c.getMethodName())) {  // 初始化view方法在setContentView之后调用。
                        ctMethod.insertAt(c.getLineNumber() + 1, METHOD_NAME + "();");

                        // 修改后不再编辑 ，直接退出。
                        ctClass.debugWriteFile(myClassPath);
                        ctClass.detach();
                    }
                }
            });
//            ctClass.debugWriteFile(myClassPath);
//            ctClass.defrost();
        }

    }


    /**
     * 生成方法代码
     *
     * @param viewInfo   初始化view需要的信息
     * @param methodName 方法名
     * @return 生成的方法代码
     */
    public static String getMethodString(Map<String, Object> viewInfo, String methodName) {
        final String view = "android.app.Activity a = this;";
        final String template = " = (android.widget.TextView)a.findViewById(";
        Set<Map.Entry<String, Object>> set = viewInfo.entrySet();
        StringBuilder sb = new StringBuilder();
        sb.append(view);
        for (Map.Entry<String, Object> entry : set) {
            String field = entry.getKey();
            int value = (int) entry.getValue();
            sb.append(field).append(template).append(value).append(");").append("\r\n");
        }
        String methodContent = sb.toString();
        System.out.println("=============");
        System.out.println(methodContent);

        String s = "public void " + methodName + "(){#}";
        String method = s.replaceAll("#", methodContent);
        return method;
    }

    public static void genMethodByCompileSource(CtClass c, Map<String, Object> viewInfo) {

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

    public static void genMethodByNewMethod(CtClass cc, String method) {

        System.out.println("----method:" + method);
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
