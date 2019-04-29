package com.nzh.plugin

import com.nzh.plugin.util.Util
import org.gradle.api.Project
import org.gradle.api.file.ConfigurableFileTree

class GUtil {

    /**
     *  获取除了R 类以外的所有自己写的类。
     * @return
     */
    static List<String> getAllClassExcludeR_() {

        Util util = Util.getInstance()
        String buildDir = util.getBuildDir()
        String androidLib = util.getLibPath().get(0)
        Project project = util.getProject()

        ConfigurableFileTree fileTree = project.fileTree(buildDir)

        String excludeDir = buildDir + File.separator + 'android'
        // 遍历fileTree 输出时有目录有文件。
        Set<File> files = fileTree.files

        // 用来排除 R文件中的类
        List<String> list = new ArrayList<>()
        // 用来保存自己写的类
        ArrayList<String> listClass = new ArrayList<>()
        list.add('R.class')
        list.add('R$anim.class')
        list.add('R$attr.class')
        list.add('R$bool.class')
        list.add('R$color.class')
        list.add('R$dimen.class')
        list.add('R$drawable.class')
        list.add('R$id.class')
        list.add('R$integer.class')
        list.add('R$layout.class')
        list.add('R$mipmap.class')
        list.add('R$string.class')
        list.add('R$style.class')
        list.add('R$styleable.class')
        list.add('R$style.class')

        fileTree.visit {
            // 排除 系统类，这里不处理
            boolean b2 = it.file.absolutePath.endsWith('.class')
            // 过滤掉目录 ，只处理 .class 结尾的文件
            boolean b1 = !it.file.absolutePath.contains(excludeDir)
            // 过滤R 文件 中的类。
            boolean b3 = !list.contains(it.name)
            if (b1 && b2 && b3) {
//                System.out.println('fileTree name:' + it.name + "-absolutePath:" + it.file.absolutePath + "-path" + it.path)

                String className = it.path.replaceAll('/', '.')
                className = className.replaceAll('.class', "")
                listClass.add(className)
            }
        }
        return listClass
    }

}