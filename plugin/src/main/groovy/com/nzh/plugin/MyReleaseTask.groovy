package com.nzh.plugin
import com.nzh.plugin.util.Util
import org.gradle.api.DefaultTask
import org.gradle.api.tasks.TaskAction


class MyReleaseTask extends DefaultTask {
    MyReleaseTask() {
        group 'myGroup'
        description '操作字节码任务'
    }

    String buildDir
    String androidLib

    void init(String buildDir, String androidLib) {
        this.buildDir = buildDir
        this.androidLib = androidLib
    }


    @TaskAction
    void doMyRun() {


        println('----MyReleaseTask------')
        Util.getInstance().release()

    }

}