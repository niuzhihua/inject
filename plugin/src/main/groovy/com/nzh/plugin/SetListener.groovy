package com.nzh.plugin

import com.nzh.plugin.inject.InjectListener
import org.gradle.api.DefaultTask
import org.gradle.api.tasks.TaskAction


class SetListener extends DefaultTask {

    String buildDir
    String androidLib

    ArrayList<String> activityes
    String packageName

    SetListener() {
        group 'myGroup'
        description '设置onclickListener任务'
    }

    void init(String buildDir, String androidLib, ArrayList<String> activityes, String packageName) {
        this.buildDir = buildDir
        this.androidLib = androidLib
        this.activityes = activityes
        this.packageName = packageName
    }



    @TaskAction
    void myRun() {

        println("SetListener task-----")
        InjectListener injectListener = new InjectListener()
        injectListener.injectView(buildDir, androidLib, activityes, packageName)

    }

}