package com.wpf.asrdemo.test

import android.os.Bundle
import android.util.Log
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.anker.bluetoothtool.R
import com.anker.bluetoothtool.activity.test.ByteUtils
import com.anker.bluetoothtool.deviceExport.util.BytesUtil
import com.anker.bluetoothtool.deviceExport.util.LogUtil
import com.anker.bluetoothtool.util.AudioEditor
import com.wpf.ffmpegcmd.FFmpegCmd
import com.wpf.opususedemo.utils.OpusUtils
import java.io.*
import kotlin.concurrent.thread


/**
 *  Author: feipeng.wang
 *  Time:   2021/6/4
 *  Description : txt文件转wav
 */
class DataToWavAct : AppCompatActivity() {
    private var opusTv: TextView? = null
    private var wavTv: TextView? = null
    private val rootPath: String by lazy {
        "/storage/emulated/0/wpf/AsrDemo/DataToWavAct/"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.datatowav_act)
        initView()
        initClick()
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
                if (!outFile.parentFile.exists()) outFile.parentFile.mkdirs()
                if (outFile.exists()) outFile.delete()//删除旧文件
                outFile.createNewFile()
                val outFileStream = FileOutputStream(outFile, true)
                val outBufferedStream = BufferedOutputStream(outFileStream)
                //2
                val tntOpusUtils = OpusUtils.getInstant()
                val decoderHandler = tntOpusUtils.createDecoder(8000, 1, biteRate = 8000)
                try {
                    val bufferArray1: ByteArray = TestData.getTest()
                    val bufferArray2: ByteArray = TestData.getTest2()
                    val bufferArray21: ByteArray = TestData.getTest21()
                    val bufferArray3: ByteArray = TestData.getTest3()
                    val bufferArray4: ByteArray = TestData.getTest4()
                    val bufferArray: ByteArray =
                        ByteArray(bufferArray1.size + bufferArray2.size + bufferArray21.size + bufferArray3.size + bufferArray4.size)

                    System.arraycopy(bufferArray1, 0, bufferArray, 0, bufferArray1.size)
                    System.arraycopy(
                        bufferArray2,
                        0,
                        bufferArray,
                        bufferArray1.size,
                        bufferArray2.size
                    )
                    System.arraycopy(
                        bufferArray21,
                        0,
                        bufferArray,
                        bufferArray1.size + bufferArray2.size,
                        bufferArray21.size
                    )
                    System.arraycopy(
                        bufferArray3,
                        0,
                        bufferArray,
                        bufferArray1.size + bufferArray2.size + bufferArray21.size,
                        bufferArray3.size
                    )
                    System.arraycopy(
                        bufferArray4,
                        0,
                        bufferArray,
                        bufferArray1.size + bufferArray2.size + bufferArray21.size + bufferArray3.size,
                        bufferArray4.size
                    )

                    LogUtil.e("${bufferArray.size},${bufferArray1.size},${bufferArray2.size},${bufferArray21.size}，${bufferArray3.size}，${bufferArray4.size}")
                    val bufferSzie = 20
                    val allSize = bufferArray.size
                    var point = 0
                    var offerSize = 0
                    var number = 0
                    while (point < allSize) {
                        var count = 0
                        if (allSize - point > bufferSzie) {
                            count = bufferSzie
                        } else {
                            count = allSize - point
                        }
                        var cacherArray = ByteArray(count)
                        System.arraycopy(bufferArray, point, cacherArray, 0, count)



                        //解析数据
                        val decodeBufferArray = ShortArray(cacherArray.size * 8)
                        val size =
                            tntOpusUtils.decode(decoderHandler, cacherArray, decodeBufferArray)
                        if (size > 0) {
                            //opus数据
                            val decodeArray = ShortArray(size)
                            System.arraycopy(decodeBufferArray, 0, decodeArray, 0, size)
                            val byteArray: ByteArray = ByteUtils.shortArr2byteArr(decodeArray, size)

                            LogUtil.e("原数据：${BytesUtil.bytesToHexString(cacherArray)}")
                            LogUtil.e("写入文件：${BytesUtil.bytesToHexString(byteArray)}")
                            if (number == 0) {
                                LogUtil.e("${byteArray.size}")
                                number = 1
                            }

                            outBufferedStream.write(byteArray)
                            outBufferedStream.flush()
                            outFileStream.flush()
                            offerSize += byteArray.size
                        }

                        LogUtil.e("point=${point},size =${allSize},offerSize =${offerSize} ")
                        point += bufferSzie
                    }

                } catch (e: Exception) {
                    LogUtil.e("出现异常")
                } finally {
/*                    inputFileStream.close()
                    inputBufferedStream.close()*/
                    outBufferedStream.flush()
                    outBufferedStream.close()
                    outFileStream.flush()
                    outFileStream.close()
                }
                LogUtil.e("文件处理完毕")
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
}