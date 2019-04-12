package com.nzh.plugin

import com.android.build.api.transform.QualifiedContent
import com.android.build.api.transform.Transform
import com.android.build.api.transform.TransformException
import com.android.build.api.transform.TransformInvocation
import com.android.build.gradle.AppExtension
import com.android.build.gradle.internal.pipeline.TransformManager
import com.nzh.plugin.inject.InjectView2
import com.nzh.plugin.util.Util
import groovy.xml.Namespace
import javassist.ClassPool
import org.gradle.api.Project

// 我们自己注册的Transform 优先于gradle 内置的 Transform来先执行。
class MyTransform extends Transform {


    Project project
    AppExtension android

    MyTransform(Project project, AppExtension android) {
        this.android = android
        this.project = project
        def sdkDir = 'D:\\setup\\android_sdk\\android-sdk'

        // 获取build生成目录
        def buildDir = project.buildDir.absolutePath + File.separator + 'intermediates' + File.separator + 'classes' + File.separator + 'debug'

//        def androidJarPath = sdkDir + File.separator + 'platforms' + File.separator + android.compileSdkVersion + File.separator + 'android.jar'
        def androidJarPath = sdkDir + File.separator + 'platforms' + File.separator + 'android-26' + File.separator + 'android.jar'

        // 获取清单文件
        def manifestFile = android.sourceSets.main.manifest.srcFile

        // 解析清单文件
        def parser = new XmlParser().parse(manifestFile)
        def nameSpace = new Namespace('http://schemas.android.com/apk/res/android', 'android')
        //获取application 节点
        Node node = parser.application[0]
        // 获取application下的四大组件等信息
        List childs = node.children()
        def packageName = 'lsn.javassit.nzh.com.javassit' // android.defaultConfig.applicationId
        println('packageName:' + packageName)
        ArrayList<String> activities = new ArrayList<>()
        for (Node child : childs) {
            if ('activity'.equals(child.name())) {
                String activityName = child.attributes()[nameSpace.name]
                if (activityName.contains(packageName)) {
                    activities.add(activityName)
                } else {
                    activities.add(packageName + activityName)
                }
            }
        }

        println('buildDir:' + buildDir)
        println('androidJarPath:' + androidJarPath)
        println('activities:' + activities.size())
        println('packageName:' + packageName)
        init(buildDir, androidJarPath, activities, packageName)
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
        return TransformManager.CONTENT_CLASS
    }

    /**
     * 配置当前transform要处理的内容范围
     *
     *
     * @return
     */
    @Override
    Set<? super QualifiedContent.Scope> getScopes() {
        // TransformManager.SCOPE_FULL_INSTANT_RUN_PROJECT
        // 处理范围：整个工程 所有的getInputTypes()都会交给 我们自定义的MyTransform来处理
//        TransformManager.SCOPE_FULL_PROJECT :
//        TransformManager.SCOPE_FULL_WITH_IR_FOR_DEXING
        return TransformManager.SCOPE_FULL_PROJECT
    }

    @Override
    boolean isIncremental() {
        return false
    }

    @Override
    void transform(TransformInvocation transformInvocation) throws TransformException, InterruptedException, IOException {
        super.transform(transformInvocation)

        println '---执行自定义transform------'

/*        // 获取transform所有的输入
        Collection<TransformInput> inputs = transformInvocation.getInputs()

        // 从inputs 中又可以拿到2个输入： 目录输入 , (jar)文件输入

        for(TransformInput input:inputs){

            // 我们自己写的代码的输入。
            Collection<DirectoryInput> dirInputs = input.getDirectoryInputs()

            // 所有的jar输入，包括第三方jar. 除了我们自己写的代码之外，
            Collection<JarInput> jarInputs = input.jarInputs

        }*/

        ///

        writeMyClass()

//        TransformOutputProvider outputProvider = transformInvocation.outputProvider
//
//        transformInvocation.inputs.each {
//            // 处理 DirectoryInput 集合
//            it.directoryInputs.each {
//                print it.file.absolutePath
//
//                File fromFile = it.file
//                // 把 自己 修改后的class 写入到 transform 的输入目录
//                //                writeMyClass(fromFile.absolutePath)
//                writeMyClass()
//
//                println('it.file:' + it.file)
//                String name = it.name
//                Set<QualifiedContent.ContentType> contenttyps = it.contentTypes
//                Set<? super QualifiedContent.Scope> scopes = it.scopes
//
//                File toFile = outputProvider.getContentLocation(name, contenttyps, scopes, Format.DIRECTORY)
//
//                FileUtils.copyDirectory(fromFile, toFile)
//
//            }
//
//                   it.jarInputs.each {
//
//                        String name = it.name
//                        Set<QualifiedContent.ContentType> contenttyps = it.contentTypes
//                        Set<? super QualifiedContent.Scope> scopes = it.scopes
//                        File fromFile = it.file
//
//                        File toFile = outputProvider.getContentLocation(name, contenttyps, scopes, Format.JAR)
//
//                        FileUtils.copyFile(fromFile, toFile)
//                    }
//        }

    }

    void writeMyClass() {
        //https://www.sohu.com/a/192685301_659256
//        https://github.com/minsko/AndroidGradleTransformTest/blob/master/buildSrc/src/main/groovy/com/example/transform/MyTransform.groovy
        InjectView2 injectView = new InjectView2(InjectView2.InjectType.Only_Inject_By_Anno)
        injectView.injectView(buildDir, androidLib, activityes, packageName)
    }
}