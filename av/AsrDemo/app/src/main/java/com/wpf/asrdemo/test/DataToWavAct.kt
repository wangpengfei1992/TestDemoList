package com.wpf.asrdemo.test

import android.os.Bundle
import android.util.Log
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.wpf.asrdemo.AudioEditor
import com.wpf.asrdemo.R
import com.wpf.bluetooth.common.util.BytesUtil
import com.wpf.bluetooth.common.util.LogUtil
import com.wpf.bluetooth.utils.L
import com.wpf.ffmpegcmd.FFmpegCmd
import com.wpf.opususedemo.utils.OpusUtils
import java.io.*
import kotlin.concurrent.thread


/**
 *  Author: feipeng.wang
 *  Time:   2021/6/4
 *  Description : txt文件转wav
 */
class DataToWavAct : AppCompatActivity(){
    private var opusTv:TextView ?= null
    private var wavTv:TextView ?= null
    private val rootPath:String by lazy{
        "/storage/emulated/0/wpf/AsrDemo/DataToWavAct/"
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.datatowav_act)
        initView()
        initClick()
        LogUtil.e("wpf","oncreate")
    }
    private fun initView() {
        opusTv = findViewById(R.id.txt_to_opus)
        wavTv = findViewById(R.id.opus_to_wav)
    }
    private fun initClick() {
//        val resPath = "${rootPath}987.pcm"
        val resPath = "${rootPath}test1.txt"
        val opusPath = "${rootPath}data.pcm"
        val wavPath = "${rootPath}play.wav"
        opusTv?.setOnClickListener {
            thread {
                //1.读取文件流opus 2.编码为pcm 2.输出文件流
/*                val inputFile = File(resPath)
                val inputFileStream = FileInputStream(inputFile)
                val inputBufferedStream = BufferedInputStream(inputFileStream)*/


                val outFile = File(opusPath)
                if (!outFile.parentFile.exists())outFile.parentFile.mkdirs()
                if (outFile.exists())outFile.delete()//删除旧文件
                outFile.createNewFile()
                val outFileStream = FileOutputStream(outFile, true)
                val outBufferedStream = BufferedOutputStream(outFileStream)
                //2
                val tntOpusUtils = OpusUtils.getInstant()
                val decoderHandler = tntOpusUtils.createDecoder(8000, 1, biteRate = 8000)
                try {
                    val bufferArray1:ByteArray = TestData2.getTest()
                    val bufferArray2:ByteArray =TestData2.getTest2()
                    val bufferArray21:ByteArray=TestData2.getTest21()
                    val bufferArray3:ByteArray = TestData2.getTest3()
                    val bufferArray4:ByteArray = TestData2.getTest4()
                    val bufferArray5:ByteArray = TestData2.getTest5()
                    val bufferArray6:ByteArray = TestData2.getTest6()
                    val bufferArray7:ByteArray = TestData2.getTest7()
                    val bufferArray8:ByteArray = TestData2.getTest8()

                    val bufferArray:ByteArray = ByteArray(bufferArray1.size+bufferArray2.size+bufferArray21.size+bufferArray3.size+bufferArray4.size
                            +bufferArray5.size+bufferArray6.size+bufferArray7.size+bufferArray8.size)

                    System.arraycopy(bufferArray1, 0, bufferArray, 0, bufferArray1.size)
                    System.arraycopy(bufferArray2, 0, bufferArray, bufferArray1.size, bufferArray2.size)
                    System.arraycopy(bufferArray21, 0, bufferArray, bufferArray1.size+bufferArray2.size, bufferArray21.size)
                    System.arraycopy(bufferArray3, 0, bufferArray, bufferArray1.size+bufferArray2.size+bufferArray21.size, bufferArray3.size)
                    System.arraycopy(bufferArray4, 0, bufferArray, bufferArray1.size+bufferArray2.size+bufferArray21.size+bufferArray3.size, bufferArray4.size)
                    System.arraycopy(bufferArray5, 0, bufferArray, bufferArray1.size+bufferArray2.size+bufferArray21.size+bufferArray3.size+bufferArray4.size, bufferArray5.size)
                    System.arraycopy(bufferArray6, 0, bufferArray, bufferArray1.size+bufferArray2.size+bufferArray21.size+bufferArray3.size+bufferArray4.size+bufferArray5.size, bufferArray6.size)
                    System.arraycopy(bufferArray7, 0, bufferArray, bufferArray1.size+bufferArray2.size+bufferArray21.size+bufferArray3.size+bufferArray4.size+bufferArray5.size+bufferArray6.size, bufferArray7.size)
                    System.arraycopy(bufferArray8, 0, bufferArray, bufferArray1.size+bufferArray2.size+bufferArray21.size+bufferArray3.size+bufferArray4.size+bufferArray5.size+bufferArray6.size+bufferArray7.size, bufferArray8.size)
                    LogUtil.e("${bufferArray.size},${bufferArray1.size},${bufferArray2.size},${bufferArray21.size}，${bufferArray3.size}，${bufferArray4.size}")
                    val bufferSzie = 20
                    val allSize = bufferArray.size
                    var point = 0
                    var offerSize = 0
                    var number = 0
                    while (point < allSize ){
                        var count = 0
                        if(allSize - point>bufferSzie){
                            count = bufferSzie
                        }else{
                            count = allSize - point
                        }
                        var cacherArray = ByteArray(count)
                        System.arraycopy(bufferArray, point, cacherArray, 0, count)

                        //解析数据
                        val decodeBufferArray = ShortArray(cacherArray.size * 8)
                        val size = tntOpusUtils.decode(decoderHandler, cacherArray, decodeBufferArray)
                        if (size > 0) {
                            //opus数据
                            val decodeArray = ShortArray(size)
                            System.arraycopy(decodeBufferArray, 0, decodeArray, 0, size)
                            val byteArray:ByteArray = ByteUtils.shortArr2byteArr(decodeArray, size)

                            LogUtil.e("原数据：${BytesUtil.bytesToHexString(cacherArray)}")
                            LogUtil.e("写入文件：${BytesUtil.bytesToHexString(byteArray)}")
                            if (number == 0){
                                LogUtil.e("${byteArray.size}")
                                number = 1
                            }

                            outBufferedStream.write(byteArray)
                            outBufferedStream.flush()
                            outFileStream.flush()
                            offerSize += byteArray.size
                        }

                        LogUtil.e("point=${point},size =${allSize},offerSize =${offerSize} ")
                        point+=bufferSzie
                    }

                }catch (e: Exception){
                    L.e("出现异常")
                }finally {
/*                    inputFileStream.close()
                    inputBufferedStream.close()*/
                    outBufferedStream.flush()
                    outBufferedStream.close()
                    outFileStream.flush()
                    outFileStream.close()
                }
                L.e("文件处理完毕")
            }
        }
        wavTv?.setOnClickListener {
            var audioEditor = AudioEditor()
            audioEditor.pcmToWav(opusPath, wavPath,
                50000, object : FFmpegCmd.OnCmdExecListener {
                    override fun onSuccess() {
                        Log.e("wpf", "onSuccess")
                    }

                    override fun onFailure() {
                        Log.e("wpf", "onFailure")
                    }

                    override fun onProgress(progress: Float) {
                        Log.e("wpf", "$progress")
                    }

                })
        }
    }

    private fun getData(inputBufferedStream: BufferedInputStream, bufferArray: ByteArray): Int {
        var n = inputBufferedStream.read(bufferArray, 0, bufferArray.size)
//        L.e("$n")
        return n
    }
}

/*
*
                    val bufferArray = ByteArray(4*20)
                    var n = 0
                    var printf = true
                    var number = 0
                    while (n!=-1){
                        if (!printf){
                            break
                            printf = false
                        }
                        try {
                            if (n>0){
                                //0x13,0xC8,0x41,0x86,0xB4,0x98,0x08,0xB7,0x33,

                          /*      var cacherArray = ByteArray(bufferArray.size)
                                System.arraycopy(bufferArray, 0, cacherArray, 0, bufferArray.size)
                                val s = String(cacherArray, 0, n).replace(" ", "")
                                LogUtil.e(",${n}原数据：${s}")
                                var byte2Array = ByteUtils.toBytes(s)
                                LogUtil.e("${BytesUtil.bytesToHexString(byte2Array)}")*/



                                //写入到文件中
//                                outBufferedStream.write(bufferArray,0,n)

                                val decodeBufferArray = ShortArray(bufferArray.size*4)
                                val size = tntOpusUtils.decode(decoderHandler, bufferArray, decodeBufferArray)
                                if (size > 0) {
                                    //opus数据
                                    val decodeArray = ShortArray(size)
                                    System.arraycopy(decodeBufferArray, 0, decodeArray, 0, size)
                                    val byteArray:ByteArray = ByteUtils.shortArr2byteArr(decodeArray,size)

                                    if (number == 0){
                                        LogUtil.e("原数据：${s}")
                                        LogUtil.e("${BytesUtil.bytesToHexString(bufferArray)}")
                                        LogUtil.e("${BytesUtil.bytesToHexString(byteArray)}")
                                        number = 1
                                    }


                                    outBufferedStream.write(byteArray,0,byteArray.size)
                                }

                            }
                            n = getData(inputBufferedStream,bufferArray)
                        } catch (e: Exception) {
                            L.e("读取文件流错误${e.message}")
                            printf = false
                        }
                    }
* */