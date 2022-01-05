package com.wpf.myviewtest

import android.util.Log
import com.wpf.myviewtest.bean.Sentence
import java.util.regex.Pattern

object StringHelper {
    private const val PSD_REGEX = "^[!,;.\\t\\n]"
    private fun isPsdValidWithRegex(psd: String?): Boolean {
        return Pattern.matches(PSD_REGEX, psd)
    }
    fun dealWith(content:String):ArrayList<Sentence>{
        var sentenceList = ArrayList<Sentence>()

        var pointNumber = 1
        var pointStart = 0
        for (c in content.indices){
            var current = content[c].toString()
            var isValid = isPsdValidWithRegex(current)
            if (isValid){
                if (current == "\n"){
                    pointStart = c+1
                    continue
                }
                when(pointNumber){
                    1->{
                        var sentence = Sentence()
                        sentence.startPoint = pointStart
                        sentence.endPoint = c
                        sentence.content = content.substring(pointStart,c+1)
                        sentenceList.add(sentence)
                        pointNumber = 2
                    }
                    2->{
                        pointStart = c+1
                        pointNumber = 1
                    }
                }
            }
        }

/*        var pointList = ArrayList<Int>()
        pointList.add(0)
        for (c in 0..content.length){
            var isValid = isPsdValidWithRegex(content[c].toString())
            if (isValid){
                pointList.add(c)
            }
        }
        var allSize = pointList.size
        if (allSize%2 == 0){
            Log.e("wpf","加上开头，偶数个标记")
        }
        for (point in 0..allSize step 2){
            var sentence = Sentence()
            sentence.startPoint = pointList[point]
            sentence.endPoint = pointList[point+1]
            sentence.content = content.substring(pointList[point],pointList[point+1])
            sentenceList.add(sentence)
        }*/
        return sentenceList
    }
}