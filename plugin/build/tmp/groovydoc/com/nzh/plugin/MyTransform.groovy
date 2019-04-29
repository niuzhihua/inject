package com.nzh.plugin

import com.android.build.api.transform.DirectoryInput
import com.android.build.api.transform.JarInput
import com.android.build.api.transform.QualifiedContent
import com.android.build.api.transform.Status
import com.android.build.api.transform.Transform
import com.android.build.api.transform.TransformException
import com.android.build.api.transform.TransformInput
import com.android.build.api.transform.TransformInvocation
import com.android.build.api.transform.TransformOutputProvider
import com.android.build.gradle.AppExtension
import com.android.build.gradle.internal.pipeline.TransformManager
import com.android.ide.common.internal.WaitableExecutor
import com.android.utils.FileUtils
import com.nzh.plugin.inject.InjectView2
import com.nzh.plugin.util.Util
import javassist.ClassPool
import org.gradle.api.Project

import java.util.concurrent.Callable

// 我们自己注册的Transform 优先于gradle 内置的 Transform来先执行。
class MyTransform extends Transform {


    Project project
    AppExtension android

    MyTransform(Project project, AppExtension android) {
        this.android = android
        this.project = project

    }

    String buildDir
    String androidLib

    ArrayList<String> activityes
    String packageName

    void init(String buildDir, String androidLib, ArrayList<String> activityes, String packageName) {
        this.buildDir = buildDir
        this.androidLib = androidLib
        this.activityes = activityes
        this.packageName = packageName
        ClassPool pool = new ClassPool(true)//ClassPool.getDefault()
        pool.appendClassPath(buildDir)
        pool.appendClassPath(androidLib)
        Util.init(pool, activityes)
        println('---MyTransform--init---')
    }

    // 配置gradle构建过程中产生的 任务名 。
    @Override
    String getName() {
        // gradle 构建过程中会多一个 transformclassesWith${当前name}ForDebug 任务。
        return 'niuzhihua'
    }
    /**
     * 配置当前transform处理的类型: 有class ,dex,jar ,resource  .
     * 这里选 class
     * @return
     */
    @Override
    Set<QualifiedContent.ContentType> getInputTypes() {
//        return TransformManager.CONTENT_CLASS
        return TransformManager.CONTENT_CLASS
    }
    @Override
    Set<QualifiedContent.ContentType> getOutputTypes() {
        return super.getOutputTypes();
    }

    /**
     * 配置当前transform要处理的内容范围
     *
     *
     * @return
     */
    @Override
    Set<? super QualifiedContent.Scope> getScopes() {
//         return  TransformManager.SCOPE_FULL_INSTANT_RUN_PROJECT
        // 处理范围：整个工程 所有的getInputTypes()都会交给 我们自定义的MyTransform来处理
//        TransformManager.SCOPE_FULL_PROJECT :
//        TransformManager.SCOPE_FULL_WITH_IR_FOR_DEXING
        return TransformManager.SCOPE_FULL_PROJECT
    }

    @Override
    boolean isIncremental() {
        return false
    }
    private WaitableExecutor waitableExecutor = WaitableExecutor.useGlobalSharedThreadPool();

    @Override
    void transform(TransformInvocation transformInvocation) throws TransformException, InterruptedException, IOException {
        super.transform(transformInvocation)


        //当前是否是增量编译
        boolean isIncremental = transformInvocation.isIncremental();
        //消费型输入，可以从中获取jar包和class文件夹路径。需要输出给下一个任务
        Collection<TransformInput> inputs = transformInvocation.getInputs();

        //OutputProvider管理输出路径，如果消费型输入为空，你会发现OutputProvider == null
        TransformOutputProvider outputProvider = transformInvocation.getOutputProvider();
        for(TransformInput input : inputs) {
        /*    for(JarInput jarInput : input.getJarInputs()) {
                File dest = outputProvider.getContentLocation(
                        jarInput.getFile().getAbsolutePath(),
                        jarInput.getContentTypes(),
                        jarInput.getScopes(),
                        Format.JAR);
                //将修改过的字节码copy到dest，就可以实现编译期间干预字节码的目的了
                FileUtils.copyFile(jarInput.getFile(), dest);
            }*/
            for(DirectoryInput directoryInput : input.getDirectoryInputs()) {
                File dest = outputProvider.getContentLocation(directoryInput.getName(),
                        directoryInput.getContentTypes(), directoryInput.getScopes(),
                        Format.DIRECTORY);
                //将修改过的字节码copy到dest，就可以实现编译期间干预字节码的目的了
               // FileUtils.copyDirectory(directoryInput.getFile(), dest);
            }
        }
    }

    void dealIncremental(boolean isIncremental, JarInput jarInput, File dest) {
        if (isIncremental) {
            //处理增量编译
            switch (jarInput.status) {
                case Status.NOTCHANGED:
                    break
                case Status.ADDED:
                case Status.CHANGED:
                    //处理有变化的
                    if (jarInput.status == Status.CHANGED) {
                        //Changed的状态需要先删除之前的
                        if (dest.exists()) {
//                                            FileUtils.forceDelete(dest)
                            FileUtils.delete(dest)
                        }
                    }
                    FileUtils.copyFile(jarInput.getFile(), dest)
                    break
                case Status.REMOVED:
                    //移除Removed
                    if (dest.exists()) {
                        //   FileUtils.forceDelete(dest)
                        FileUtils.delete(dest)
                    }
                    break
            }
        } else {
            //不处理增量编译
            //将修改过的字节码copy到dest，就可以实现编译期间干预字节码的目的了
            FileUtils.copyFile(jarInput.getFile(), dest);
        }
    }

    void writeMyClass() {
        //https://www.sohu.com/a/192685301_659256
//        https://github.com/minsko/AndroidGradleTransformTest/blob/master/buildSrc/src/main/groovy/com/example/transform/MyTransform.groovy
        InjectView2 injectView = new InjectView2(InjectView2.InjectType.Only_Inject_By_Anno)
        injectView.injectView(buildDir, androidLib, activityes, packageName)
    }
}