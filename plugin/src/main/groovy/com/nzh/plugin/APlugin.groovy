package com.nzh.plugin

import com.android.build.gradle.AppExtension
import com.android.build.gradle.LibraryExtension
import com.nzh.plugin.util.Util
import groovy.xml.Namespace
import javassist.ClassClassPath
import javassist.ClassPool
import org.gradle.api.GradleException
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.internal.FileUtils

class APlugin implements Plugin<Project> {

    @Override
    void apply(Project project) {

        // 创建配置
        project.extensions.create('inject', MyExtensions)

        if (project.plugins.hasPlugin("com.android.application")) {
            AppExtension android = project.extensions.getByType(AppExtension.class)

            // 获取android.jar 路径
            Properties properties = new Properties()
            InputStream inputStream = project.rootProject.file('local.properties').newDataInputStream()
            properties.load(inputStream)
            def sdkDir = properties.getProperty('sdk.dir')

            // 获取build生成目录
            def buildDebug = project.buildDir.absolutePath + File.separator + 'intermediates' + File.separator + 'classes' + File.separator + 'debug'
            println('buildDir:' + buildDebug)

//            println('---Transform-----')
//            android.registerTransform(new MyTransform(project, android))

            project.afterEvaluate {

                println project.inject.onlyInjectWithAnnotaion

                def androidJar = sdkDir + File.separator + 'platforms' + File.separator + android.compileSdkVersion + File.separator + 'android.jar'

                // 保存所有 处理字节码时需要的jar
                ArrayList<String> libsPath = new ArrayList<>()
                libsPath.add(androidJar)
                def v4_v7_Dir = sdkDir + File.separator + 'extras' + File.separator + 'android' + File.separator + 'support' + File.separator + 'v7' + File.separator + 'appcompat' + File.separator + 'libs'
                File f = new File(v4_v7_Dir)
                File[] files = f.listFiles()
                if (files.length <= 1) {
                    println('---------请检查sdk 中 v4,v7 jar 包是否存在-------')
                    return
                }
                files.each {
                    libsPath.add(it.absolutePath)
                }

                // 获取清单文件
                def manifestFile = android.sourceSets.main.manifest.srcFile
                println('---:' + manifestFile)

                // 解析清单文件
                def parser = new XmlParser().parse(manifestFile)
                def nameSpace = new Namespace('http://schemas.android.com/apk/res/android', 'android')
                //获取application 节点
                Node node = parser.application[0]
                // 获取application下的四大组件等信息
                List childs = node.children()
                def packageName = android.defaultConfig.applicationId
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

                // 创建 初始化View task
                def myTask = project.tasks.create('myTask', MyTask)
                // 创建 listener task
                def setListenerTask = project.tasks.create('setListenerTask', SetListenerTask)
                // 初始化View task , 使用在Fragment 中。
                def doInFragmentTask = project.tasks.create('doInFragmentTask', DoInFragmentTask)
                // 是否资源 task
                def myRelease = project.tasks.create('myRelease', MyReleaseTask)

                // 初始化工具类
                init(buildDebug, libsPath, activities, packageName, project)

                // 安排任务执行
                def beforeGenDex = project.tasks.getByName('mergeDebugAssets')

                // 设置任务执行顺序：
                // setListenerTask 先执行（这样 插入的监听代码在最后），然后执行myTask
                beforeGenDex.finalizedBy setListenerTask
                setListenerTask.finalizedBy myTask
                myTask.finalizedBy doInFragmentTask
                doInFragmentTask.finalizedBy myRelease


            }


        } else {
            throw new GradleException("只能够在Android application插件中使用。")
        }
    }

    /**
     *  初始化工具类
     * @param myClassPath 自己写的java代码生成的class 所在 路径。
     * @param myClassLibPath 处理字节码时需要的第三方jar的路径。 包括android.jar ，v4.jar,v7.jar 的路径.
     * @param activities 解析Manifest.xml 获取的所有activity名称。
     * @param packageName application 插件所在module 的包名（android 工程的包名）
     * @param project
     */
    void init(String myClassPath, ArrayList<String> libsPath, ArrayList<String> activities, String packageName, Project project) {
        ClassPool pool = new ClassPool(true)//ClassPool.getDefault()
        pool.appendClassPath(myClassPath)

        libsPath.each {
            pool.appendClassPath(it)
        }

        Util.init(pool, activities, myClassPath, libsPath, packageName, project)
    }

}