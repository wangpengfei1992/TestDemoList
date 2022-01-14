package com.ankerwork.plugin

import org.gradle.api.Plugin
import org.gradle.api.Project

public class ProductPlugin implements Plugin<Project> {

    @Override
    void apply(Project target) {
        println("------------groovy----${target.getName()}--------")
        //do something
        def extension = target.extensions.create("productPlugin", Param)
        target.task('pluginTest') {
            def makeDir = {
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
            }
        }
    }

}