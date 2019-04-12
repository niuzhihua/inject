package com.nzh.plugin

import com.android.build.gradle.AppExtension
import com.nzh.plugin.util.Util
import groovy.xml.Namespace
import javassist.ClassPool
import org.gradle.api.GradleException
import org.gradle.api.Plugin
import org.gradle.api.Project

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
                println('androidJar:' + androidJar)

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
                def setListenerTask = project.tasks.create('setListenerTask', SetListener)
                def myRelease = project.tasks.create('myRelease', MyReleaseTask)



                init(buildDebug, androidJar, activities)

                // 只初始化带注解的View
//               myTask.init(buildDebug, androidJar, activities)
                myTask.init(buildDebug, androidJar, activities, packageName)
                setListenerTask.init(buildDebug, androidJar, activities, packageName)
                myRelease.init(buildDebug, androidJar)
                // 安排任务执行
                def beforeGenDex = project.tasks.getByName('mergeDebugAssets')

                // 设置任务执行顺序：
                // setListenerTask 先执行（这样 插入的监听代码在最后），然后执行myTask
                beforeGenDex.finalizedBy setListenerTask
                setListenerTask.finalizedBy myTask
                myTask.finalizedBy myRelease


            }


        } else {
            throw new GradleException("只能够在Android application插件中使用。")
        }
    }

    void init(String myClassPath, String myClassLibPath, ArrayList<String> activities) {
        ClassPool pool = new ClassPool(true)//ClassPool.getDefault()
        pool.appendClassPath(myClassPath)
        pool.appendClassPath(myClassLibPath)
        Util.init(pool, activities)
    }

}