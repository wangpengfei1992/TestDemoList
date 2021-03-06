package com.ankerwork.plugin

import org.gradle.api.Plugin
import org.gradle.api.Project

class ProductPlugin implements Plugin<Project> {

    @Override
    void apply(Project target) {
        println("------------groovy----${target.getName()}----${target.rootProject.name}-${target.path}---")
        //task
        target.task(type: MyTask, "testTask") {
            doLast {
                println("执行完testTask")
            }
        }
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
                    def ktPath = "${configPath}"
                    println("需创建文件路径:${ktPath}")
                    File file = new File(ktPath)
                    if (!file.parentFile.exists()) {
                        file.parentFile.mkdirs()
                    }
                    file.withPrintWriter { printWriter ->
                        String configMapValue = replaceConfigContent(pathCode.code,keyList,values)
                        def modulePath = "${configMapValue}"
                        println("模板文件路径:${modulePath}")
                        printWriter.println(loadConfig(modulePath, keyList, values))
                    }
                }
            }

/*            def makeDir = {
                path->
                    File file = new File(path)
                    if (!file.exists()){
                        file.mkdirs()
                    }
            }
            doLast {
                println("需配置内容是:${extension.pdroductcode}")
                //创建文件夹
                def dirs = ["${target.getName()}/src/main/java/com/wpf/plugindemo/${extension.pdroductcode}model",
                            "${target.getName()}/src/main/java/com/wpf/plugindemo/${extension.pdroductcode}dispa"
                ]
                dirs.each(makeDir)
                def ktPath = "${target.getName()}/src/main/java/com/wpf/plugindemo/${extension.pdroductcode}HelloWorld.kt"
                new File(ktPath).withPrintWriter { printWriter ->
                    printWriter.println("""package com.wpf.plugindemo

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle

class ${extension.pdroductcode}HelloWorld : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
    }
}""")
                }
            }*/
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