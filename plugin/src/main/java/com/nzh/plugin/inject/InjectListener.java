package com.nzh.plugin.inject;


import com.nzh.plugin.util.Util;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javassist.CannotCompileException;
import javassist.ClassPool;
import javassist.CtClass;
import javassist.CtMethod;
import javassist.CtNewMethod;
import javassist.bytecode.AnnotationsAttribute;
import javassist.bytecode.DuplicateMemberException;
import javassist.bytecode.MethodInfo;
import javassist.bytecode.annotation.Annotation;
import javassist.bytecode.annotation.ArrayMemberValue;
import javassist.bytecode.annotation.IntegerMemberValue;
import javassist.bytecode.annotation.MemberValue;
import javassist.expr.ExprEditor;
import javassist.expr.MethodCall;

import static com.nzh.plugin.Const.CALl_SET_LISTENER;
import static com.nzh.plugin.Const.METHOD_OnClick;
import static com.nzh.plugin.Const.METHOD_OnCreate;
import static com.nzh.plugin.Const.METHOD_SetContentView;
import static com.nzh.plugin.Const.ONCLICK_ANNOTATION;

/**
 * OnClick 注解的实现
 */
public class InjectListener {

    private static final String view = "android.app.Activity a = this;";
    private static final String template = " a.findViewById(";


    private static final String SET_LISTENER = ".setOnClickListener(this);";

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

        Util util = Util.getInstance();
        ClassPool pool = util.getClassPool();

        for (String activity : activities) {

            String dstClass = activity;
            final CtClass ctClass = pool.get(dstClass);


//            String annotationClassName = ONCLICK_ANNOTATION;
            String annotationMethodName = "value";

            CtMethod[] methods = ctClass.getDeclaredMethods();

//            ArrayList<Integer> list = new ArrayList<>();
            // key : 使用了Onclick 注解的方法名
            // value : 需要这个clickc listener 的view id .
            Map<String, List<Integer>> map = new HashMap<>();

            for (CtMethod method : methods) {

                MethodInfo methodInfo = method.getMethodInfo();
                String methodName = methodInfo.getName();

                // 运行期不可见的注解
                AnnotationsAttribute annotationAttr = (AnnotationsAttribute) methodInfo.getAttribute(AnnotationsAttribute.invisibleTag);
                if (annotationAttr == null) {
                    continue;
                }
                Annotation annotation = annotationAttr.getAnnotation(ONCLICK_ANNOTATION);
                ArrayMemberValue memberValue = (ArrayMemberValue) annotation.getMemberValue(annotationMethodName);
                MemberValue[] memberValues = memberValue.getValue();
                // 添加id
                List<Integer> viewIds = new ArrayList<>();
                for (MemberValue m : memberValues) {
                    IntegerMemberValue integerMemberValue = (IntegerMemberValue) m;

                    viewIds.add(integerMemberValue.getValue());
                    System.out.println(integerMemberValue.getValue());
                }

                map.put(methodName, viewIds);
            }

            if (map.size() == 0) {
                continue;
            }

            // 判断是否实现OnClickListener
            if (!util.isImplementsOnclickListener(ctClass)) {

                ctClass.addInterface(util.getOnclickListenerClass());

                // 实现 Onclick 方法，并在 OnClick 内添加调用
                addMethod(ctClass, getCallMethodOnClick(map));
            } else {
                // 在 OnClick 内添加调用
                CtMethod onClickMethod = ctClass.getDeclaredMethod(METHOD_OnClick);
                onClickMethod.insertBefore(getCallMethod(map));
            }

            // 添加 设置onClickListener 监听代码

            addMethod(ctClass, getSetListenerMethod(map));


            final CtMethod ctMethod = ctClass.getDeclaredMethod(METHOD_OnCreate);
            System.out.println("----ctMethod在父类吗" + (ctMethod == null));
            ctMethod.instrument(new ExprEditor() {

                // onCreate 方法中有几次方法的调用，这个edit 方法就会被回掉几次。
                @Override
                public void edit(MethodCall c) throws CannotCompileException {
                    super.edit(c);

                    if (METHOD_SetContentView.equals(c.getMethodName())) {  // 初始化view方法在setContentView之后调用。

                        ctMethod.insertAt(c.getLineNumber() + 1, CALl_SET_LISTENER + ";");

                        // 修改后不再编辑 ，直接退出。
                        ctClass.debugWriteFile(myClassPath);
                        ctClass.detach();
                    }
                }
            });
            ctClass.debugWriteFile(myClassPath);
            ctClass.defrost();
        }
    }

    /**
     * 生成整个 设置 点击事件监听方法
     * 例如：
     * private void init(){
     * findViewById(R.id.xxx).setOnclickListener(this)
     * findViewById(R.id.abc).setOnclickListener(this)
     * findViewById(R.id.def).setOnclickListener(this)
     * }
     *
     * @param map
     * @return
     */
    private String getSetListenerMethod(Map<String, List<Integer>> map) {


        Set<Map.Entry<String, List<Integer>>> set = map.entrySet();
        StringBuilder sb = new StringBuilder();
        sb.append(view);
        for (Map.Entry<String, List<Integer>> entry : set) {
            List<Integer> ids = entry.getValue();

            for (Integer id : ids) {
                sb.append(template).append(id).append(")").append(SET_LISTENER).append("\r\n");
            }
        }
        String methodContent = sb.toString();
        String method = "private void " + CALl_SET_LISTENER + "{ # }";
        return method.replace("#", methodContent);

    }


    /**
     * 生成整个Onclick 方法 ， 包括调用。
     *
     * @param map 保存着OnClick注解的方法信息
     * @return 生成整个Onclick 方法
     */
    public String getCallMethodOnClick(Map<String, List<Integer>> map) {

        String base = "public void onClick(android.view.View v){ # }";

        String method = getCallMethod(map);

        return base.replaceAll("#", method);
    }


    /**
     * 生成 onclick 调用方法 （实现 Onclicklistner 接口情况）
     * onClick(View v){
     * testClick(v);  // 下面方法生成这一行调用代码。
     * }
     *
     * @param map 保存着OnClick注解的方法信息
     * @return 生成onclick 方法内的调用
     */
    private String getCallMethod(Map<String, List<Integer>> map) {
        Set<Map.Entry<String, List<Integer>>> set = map.entrySet();
        StringBuilder sb = new StringBuilder();
        for (Map.Entry<String, List<Integer>> entry : set) {
            String methodName = entry.getKey();
            List<Integer> ids = entry.getValue();

            for (int id : ids) {
                sb.append("if(v.getId()==" + id + ")\r\n");
                sb.append(methodName).append("(").append("v").append(");").append("\r\n");
            }

        }
        return sb.toString();
    }

    // 向 cc 类中添加 method 方法。
    public void addMethod(CtClass cc, String method) {

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
