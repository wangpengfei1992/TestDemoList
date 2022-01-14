package com.ankerwork.plugin;

import org.gradle.api.DefaultTask;
import org.gradle.api.tasks.TaskAction;

/**
 * Author: feipeng.wang
 * Time:   2022/1/13
 * Description : This is description.
 */
class ProductTask extends DefaultTask {
    String input = 'hello from MyTask'

    @TaskAction
    def greet() {
        // 将内容写入文件
        Param param =  getProject().extensions.getByName("productPlugin")
        println "doAction task"+param.getPdroductcode()
    }
}