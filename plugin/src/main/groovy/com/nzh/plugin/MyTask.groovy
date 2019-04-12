package com.nzh.plugin

import com.nzh.plugin.inject.InjectView2
import org.gradle.api.DefaultTask
import org.gradle.api.tasks.TaskAction

class MyTask extends DefaultTask {

    String buildDir
    String androidLib

    ArrayList<String> activityes
    String packageName


    MyTask() {
        group 'myGroup'
        description '操作字节码任务'

    }

    void init(String buildDir, String androidLib, ArrayList<String> activityes, String packageName) {
        this.buildDir = buildDir
        this.androidLib = androidLib
        this.activityes = activityes
        this.packageName = packageName
    }

    void init(String buildDir, String androidLib, ArrayList<String> activityes) {
        this.buildDir = buildDir
        this.androidLib = androidLib
        this.activityes = activityes
    }

    void setBuildDir(String buildDir) {
        this.buildDir = buildDir
    }

    void setAndroidLib(String androidLib) {
        this.androidLib = androidLib
    }

    @TaskAction
    void doRun() {


        println '----MyTask start----'

        // 在操作字节码时，输入的字节码文件 路径是 绝对路径，写死的。 如何写活？

        /** 写活方案： 1：让使用者 引入插件的同时配置 本插件定义的 扩展

         config{myClass 'xxx'
         androidClass 'xxxx'}2: 在执行injectView() 修改字节码之前 获取扩展属性，并传参 给 在执行injectView（） 方法即可。

         **/

//        InjectView.injectView(buildDir, androidLib, activityes)

        InjectView2 injectView = new InjectView2(InjectView2.InjectType.Only_Inject_By_Anno)
        injectView.injectView(buildDir, androidLib, activityes, packageName)

        println '----MyTask end----'
    }


}