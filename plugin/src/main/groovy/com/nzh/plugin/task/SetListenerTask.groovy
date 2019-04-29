package com.nzh.plugin.task

import com.nzh.plugin.inject.InjectListener
import com.nzh.plugin.util.Util
import org.gradle.api.DefaultTask
import org.gradle.api.tasks.TaskAction


class SetListenerTask extends DefaultTask {


    SetListenerTask() {
        group 'myGroup'
        description '设置onclickListener任务'
    }


    @TaskAction
    void myRun() {

        println("-------SetListenerTask task----start-")
        Util util = Util.getInstance()
        String buildDir = util.getBuildDir()
        String androidLibPath = util.getLibPath().get(0)
        ArrayList<String> activities = util.getActivities()
        String packageName = util.getPacakgeName()
        // 处理Onlick注解
        InjectListener injectListener = new InjectListener()
        injectListener.injectView(buildDir, androidLibPath, activities, packageName)
        println("-------SetListenerTask task----end-")
    }

}