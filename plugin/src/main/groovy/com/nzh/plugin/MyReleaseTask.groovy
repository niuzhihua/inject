package com.nzh.plugin
import com.nzh.plugin.util.Util
import org.gradle.api.DefaultTask
import org.gradle.api.tasks.TaskAction


class MyReleaseTask extends DefaultTask {
    MyReleaseTask() {
        group 'myGroup'
        description '操作字节码任务'
    }



    @TaskAction
    void doMyRun() {


        println('----MyReleaseTask----start--')
        Util.getInstance().release()
        println('----MyReleaseTask----end--')
    }

}