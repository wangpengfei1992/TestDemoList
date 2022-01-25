package com.ankerwork.plugin

import org.gradle.api.Plugin
import org.gradle.api.Project

class ProductPlugin implements Plugin<Project> {

    @Override
    void apply(Project target) {
        println("------------groovy----${target.getName()}--------")
        //task
        target.task(type:MyTask,"testTask"){
            doLast {
                println("执行完testTask")
            }
        }
        //do something
        MakeCodeParam makeCodeParam = target.extensions.create("makeCodeConfig", MakeCodeParam)
        target.task('makeCodeTask') {
            doLast {
                List<PathCode> pathCodeList =makeCodeParam.pathCodes
                for (int i = 0; i <pathCodeList.size() ; i++) {
                    PathCode pathCode = pathCodeList.get(i)
                    println("需创建文件路径:${pathCode.path}")
                    def ktPath = "${target.getName()}/${pathCode.path}"
                    File file = new File(ktPath)
                    if (!file.parentFile.exists()){
                        file.parentFile.mkdirs()
                    }
                    file.withPrintWriter { printWriter ->
                        printWriter.println(pathCode.code)
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

}