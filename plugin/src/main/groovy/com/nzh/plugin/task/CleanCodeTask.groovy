package com.nzh.plugin.task

import com.nzh.plugin.GUtil
import com.nzh.plugin.util.CleanCodeEditor
import com.nzh.plugin.util.Util
import javassist.ClassPool
import javassist.CtClass
import org.gradle.api.DefaultTask
import org.gradle.api.Project
import org.gradle.api.file.ConfigurableFileTree
import org.gradle.api.tasks.TaskAction

// 暂时无用
class CleanCodeTask extends DefaultTask {
    CleanCodeTask() {
        group 'myGroup'
        description '删除代码任务'
    }


    @TaskAction
    void doMyRun() {
        Util util = Util.getInstance()
        ClassPool pool = util.getClassPool()

        String buildDir = util.getBuildDir()

        List<String> allClasses = GUtil.getAllClassExcludeR_()

        println allClasses.size() + '--------'

        /* for (String clazz : allClasses) {
             CtClass temp = pool.get(clazz)
             CleanCodeEditor editor = new CleanCodeEditor(temp, buildDir)
             temp.instrument(editor)
         }*/
    }

}