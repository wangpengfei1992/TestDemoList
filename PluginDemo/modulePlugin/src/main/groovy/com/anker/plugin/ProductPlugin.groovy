package com.anker.plugin

import org.gradle.api.Plugin
import org.gradle.api.Project

class ProductPlugin implements Plugin<Project> {

    @Override
    void apply(Project target) {
        //do something
        MakeCodeParam makeCodeParam = target.extensions.create("makeCodeConfig", MakeCodeParam)
        target.task('makeCodeTask') {
            doLast {
                List<PathCode> pathCodeList = makeCodeParam.pathMoudlueMap
                List<String> keyList = makeCodeParam.keyList
                List<String> values = makeCodeParam.valueList

                for (int i = 0; i < pathCodeList.size(); i++) {
                    PathCode pathCode = pathCodeList.get(i)
                    String configPath = replaceConfigContent(pathCode.path,keyList,values)
                    println("需创建文件路径:${configPath}")

                    def ktPath = "${configPath}"
                    File file = new File(ktPath)
                    if (!file.parentFile.exists()) {
                        file.parentFile.mkdirs()
                    }
                    file.withPrintWriter { printWriter ->
                        def modulePath = "${pathCode.code}"
                        println("写入的文件:${modulePath}")
                        printWriter.println(loadConfig(modulePath, keyList, values))
                    }
                }
            }
        }
    }

    String loadConfig(String modulePath, List<String> keyList, List<String> valueList) {
        StringBuilder content = new StringBuilder()
        def config = new File(modulePath)
        config.eachLine { line ->
            content.append(replaceConfigContent(line, keyList, valueList) + "\n")
        }
        return content.toString()
    }

    String replaceConfigContent(String content, List<String> keyList, List<String> valueList) {
        for (int j = 0; j < keyList.size(); j++) {
            if (content.contains(keyList.get(j))) {
                content = content.replace(keyList.get(j), valueList.get(j))
            }
        }
        return content
    }
}