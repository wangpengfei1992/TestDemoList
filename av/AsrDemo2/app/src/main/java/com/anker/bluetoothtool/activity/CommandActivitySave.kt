package com.anker.bluetoothtool.activity

import android.graphics.Color
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.os.Message
import android.view.LayoutInflater
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.RecyclerView
import com.anker.ankerwork.deviceExport.device.BaseDeviceManager
import com.anker.ankerwork.deviceExport.device.CheckSumUtil
import com.anker.ankerwork.deviceExport.device.ParseData
import com.anker.ankerwork.deviceExport.device.SppDataInfo
import com.anker.ankerwork.deviceExport.model.AnkerWorkDevice
import com.anker.bluetoothtool.model.ProductModel
import com.anker.ankerwork.deviceExport.search.SearchCallback
import com.anker.bluetoothtool.deviceExport.util.BTDeviceHelper
import com.anker.bluetoothtool.deviceExport.util.BytesUtil
import com.anker.bluetoothtool.R
import com.anker.bluetoothtool.activity.test.ByteUtils
import com.anker.bluetoothtool.adapter.CmdAdapter
import com.anker.bluetoothtool.adapter.CmdFunctionAdapter
import com.anker.bluetoothtool.model.CmdFunctionModel
import com.anker.bluetoothtool.deviceExport.util.LogDeviceE
import com.anker.bluetoothtool.deviceExport.util.LogUtil
import com.anker.bluetoothtool.model.CmdModel
import com.anker.bluetoothtool.util.ClipboardUtil
import com.anker.bluetoothtool.util.Constant
import com.anker.bluetoothtool.util.FileUtil
import com.anker.bluetoothtool.util.LinearItemDecoration
import com.anker.bluetoothtool.view.CommonTitleBar
import com.anker.bluetoothtool.viewmodel.ProductViewModel
import com.anker.libspp.OnSppConnectStateChangeListener
import com.wpf.opususedemo.utils.OpusUtils
import java.io.BufferedOutputStream
import java.io.*
import kotlin.concurrent.thread

/**
 *  Author: anker
 *  Time:   2021/8/16
 *  Description : This is description.
 */
class CommandActivitySave : AppCompatActivity(), OnSppConnectStateChangeListener, SearchCallback {
    protected val TAG = this::class.java.simpleName
    private lateinit var cmdAdapter: CmdAdapter
    private val mCmdList = mutableListOf<CmdModel>()

    private lateinit var functionAdapter: CmdFunctionAdapter
    private val mFuncList = mutableListOf<CmdFunctionModel>()

    private val rvCmd by lazy { findViewById<RecyclerView>(R.id.rvCmd) }
    private val titleBar by lazy { findViewById<CommonTitleBar>(R.id.titleBar) }
    private val rvFunction by lazy { findViewById<RecyclerView>(R.id.rvFunction) }
    private val etCmd by lazy { findViewById<EditText>(R.id.etCmd) }
    private val imgClear by lazy { findViewById<ImageView>(R.id.imgClear) }
    private val btSend by lazy { findViewById<Button>(R.id.btSend) }
    private val btClear by lazy { findViewById<Button>(R.id.btClear) }
    private val btConnect by lazy { findViewById<Button>(R.id.btConnect) }

    private lateinit var mProductModel: ProductModel
    private var mDevice: AnkerWorkDevice? = null

    private var mDeviceManager: BaseDeviceManager? = null

    private var alertDialog: AlertDialog? = null

    private val mViewModel: ProductViewModel by viewModels()

    private var isInitFind = true
    private var isSaveDeviceData = true //是否写入文件中
    private val fileUtils by lazy { FileUtil() }
    private var rootPath = ""
    private var tntOpusUtils: OpusUtils ?= null
    private var decoderHandler:Long ?= null

    private var downOutFileStream:FileOutputStream ?= null
    private var downOutBufferedStream:BufferedOutputStream ?= null
    private var upOutFileStream:FileOutputStream ?= null
    private var upOutBufferedStream:BufferedOutputStream ?= null

    private val mHandler = object : Handler(Looper.getMainLooper()) {
        override fun handleMessage(msg: Message) {
            when (msg.what) {
                DISPATCHER_CMD -> {
                    dispatch(msg.obj as ByteArray)
                }
            }
        }
    }

    companion object {
        const val DISPATCHER_CMD = 100

        const val PRODUCT_MODEL = "PRODUCT_MODEL"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.layout_command_activity)

        mProductModel = intent.getSerializableExtra(PRODUCT_MODEL) as ProductModel

        titleBar.apply {
            imgLeft.setOnClickListener { finish() }
            imgRight.apply {
                visibility = View.VISIBLE
                setOnClickListener {
                    showAddDialog()
                }
                setImageResource(R.drawable.ic_add)
            }
            tvCenter.apply {
                visibility = View.VISIBLE
                text = mProductModel.productCode
            }
        }

        cmdAdapter = CmdAdapter(mCmdList)
        rvCmd.adapter = cmdAdapter

        cmdAdapter.setOnItemLongClickListener { adapter, view, position ->
            val model = mCmdList[position]
            if (model.itemType == CmdModel.SYSTEM_MSG) false
            else {
                ClipboardUtil.copy(this, model.content) {
                    Toast.makeText(this, "已复制内容到剪贴板", Toast.LENGTH_LONG).show()
                }
                false
            }
        }

        rvFunction.addItemDecoration(LinearItemDecoration(this, Color.BLACK))
        functionAdapter = CmdFunctionAdapter(R.layout.layout_cmd_item_function, mFuncList)
        rvFunction.adapter = functionAdapter

        functionAdapter.setOnItemClickListener { adapter, view, position ->
            val model = mFuncList[position]
            mFuncList.forEachIndexed { index, cmdFunctionModel ->
                cmdFunctionModel.isSelect = index == position
            }
            functionAdapter.notifyDataSetChanged()

            etCmd.setText(model.cmd)
            etCmd.setSelection(etCmd.text.toString().length)
        }

        functionAdapter.setOnItemLongClickListener { adapter, view, position ->
            showItemDialog(position)
            false
        }

        imgClear.setOnClickListener {
/*            etCmd.setText("")
            mFuncList.forEach { it.isSelect = false }
            functionAdapter.notifyDataSetChanged()*/
            thread {
                byteFileToPcm(rootPath+"/"+Constant.saveDownByteFile,rootPath+"/"+Constant.saveDownPcmFile)
                byteFileToPcm(rootPath+"/"+Constant.saveUpByteFileName,rootPath+"/"+Constant.saveUpPcmFile)
            }

        }

        btSend.setOnClickListener {
            if (mDeviceManager != null && mDeviceManager?.isConnected!!) {
                val cmd = etCmd.text.trim().toString()
                if (cmd.isNotEmpty()) {
                    sendCmd(cmd)
                } else {
                    addSystemMsg("命令不能为空...")
                }
            } else {
                addSystemMsg("请先连接设备...")
            }
        }

        btClear.setOnClickListener {
            mCmdList.clear()
            cmdAdapter.notifyDataSetChanged()
        }

        btConnect.setOnClickListener {
            connectDevice()
        }

        mViewModel.productsWithCmds.observe(this, {
            val list = it.firstOrNull { model -> model.product.productCode == mProductModel.productCode }?.cmds
            if (list.isNullOrEmpty()) {
                mViewModel.insertDefFunction(mProductModel.productCode)
            } else {
                functionAdapter.setList(list.sortedBy { cmd -> cmd.createTime })
            }
        })

        mViewModel.connectResult.observe(this, {
            if (it) addSystemMsg("设备连接成功！")
            else addSystemMsg("设备连接失败！")
        })
        rootPath = fileUtils.initFileRoot(baseContext)+"/AsrDemo2"
        tntOpusUtils = OpusUtils.getInstant()
        decoderHandler = tntOpusUtils?.createDecoder(8000, 1, biteRate = 8000)

        //文件
        val upOutFile = File(rootPath+"/"+Constant.saveUpPcmFile)
        if (!upOutFile.parentFile.exists())upOutFile.parentFile.mkdirs()
        if (upOutFile.exists())upOutFile.delete()//删除旧文件
        upOutFile.createNewFile()
        upOutFileStream = FileOutputStream(upOutFile, true)
        upOutBufferedStream = BufferedOutputStream(upOutFileStream)

        val outFile = File(rootPath+"/"+Constant.saveDownPcmFile)
        if (!outFile.parentFile.exists())outFile.parentFile.mkdirs()
        if (outFile.exists())outFile.delete()//删除旧文件
        outFile.createNewFile()
        downOutFileStream = FileOutputStream(outFile, true)
        downOutBufferedStream = BufferedOutputStream(downOutFileStream)


        findDevice()
    }



    private fun addSystemMsg(content: String) {
        val cmd = CmdModel(content, CmdModel.SYSTEM_MSG)
        cmdAdapter.addData(cmd)
        rvCmd.smoothScrollToPosition(mCmdList.lastIndex)
    }

    override fun onResume() {
        super.onResume()
        mViewModel.getCmdFunction()
    }

    private fun findDevice() {
        isInitFind = false
        BTDeviceHelper.searchAnkerDevice(this, mProductModel, this)
    }

    override fun onNoDevice(hasConnectedDevice: Boolean) {
        LogUtil.e("onNoDevice")
        addSystemMsg("没有找到${mProductModel.productCode}设备，请先连接经典蓝牙。")
    }

    override fun hasOneDevice(device: AnkerWorkDevice) {
        mDevice = device
        LogUtil.e("hasOneDevice device === ${mDevice?.macAddress}")
        addSystemMsg("找到一个${mProductModel.productCode}设备，mac地址为：${mDevice?.macAddress}")
        if (!isInitFind) {
            connectDevice()
        }
    }

    override fun hasManyDevices(list: List<AnkerWorkDevice>) {
        mDevice = list[0]
        LogUtil.e("hasManyDevices device === ${mDevice?.macAddress}")
        addSystemMsg("找到多个${mProductModel.productCode}设备，默认连接第一个设备，mac地址为：${mDevice?.macAddress}")
        if (!isInitFind) {
            connectDevice()
        }
    }

    private var slog = false

    private fun connectDevice() {
        if (mDevice == null) {
            findDevice()
        } else {
            mDevice?.let { device ->
                if (mDeviceManager == null) {
                    mDeviceManager = object : BaseDeviceManager() {
                        override fun dispatch(data: ByteArray?, length: Int) {
                            if (data == null || data.size < length) {
                                LogDeviceE.e("dispatch get illegall data")
                                return
                            }
                            if (mDataInfo == null) {
                                mDataInfo = SppDataInfo()
                            }
                            val reveiveData = BytesUtil.cutOutBytes(data, 0, length)
                            mDataInfo!!.curReveiveData = reveiveData

                            val oldData = BytesUtil.bytesToHexString(reveiveData, reveiveData!!.size)
                            LogDeviceE.e("wpf","接收到的数据：$oldData")
                            //拆分包，处理粘包问题
                            val parseResult = ParseData.parseDataToSegment(mDataInfo!!)
                            if (!parseResult) {
                                LogDeviceE.e("wpf","是否粘包：${!parseResult}")
                                return
                            }
                            mDataInfo!!.eventListArr?.run {
                                val it: Iterator<ByteArray?> = this.iterator()
                                while (it.hasNext()) {
                                    val subData = it.next()
                                    val hexString = BytesUtil.bytesToHexString(subData, subData!!.size)
//                                    LogUtil.e(TAG,hexString)
                                    //检测代码checksum值是否正常
                                    val isLegally = CheckSumUtil.isLegallyCmd(subData)
                                    if (subData.size < MIN_CMD_LENGTH || !isLegally) {
                                        LogDeviceE.e("wpf","收到非法数据")
                                        fileUtils.write(rootPath,hexString,"error.txt")
                                        continue
                                    }
                                    //判断是否需要写入文件
                                    val keyData = hexString.substring(8,14)
//                                    LogUtil.e("keyData= $keyData")
                                    if (isSaveDeviceData && hexString.startsWith("09ff")
                                            &&(keyData == "01f001" || keyData == "01f002")){
                                        if (keyData == "01f001"){
                                            val hexSize = hexString.length
                                            val saveData = hexString.substring(18,hexSize-2)
                                            LogUtil.e("上行数据= $saveData")
                                            if (saveData.isNotEmpty()){
                                                //2进制数据
                                                var cacherArray = ByteArray(subData.size-10)
                                                System.arraycopy(subData, 9, cacherArray, 0, subData.size-10)
                                                hexToPcmData(cacherArray,Constant.saveUpPcmFile,upOutBufferedStream,upOutFileStream)
                                                //16进制数据
                                                fileUtils.write(rootPath,saveData,Constant.saveFileName)

                   /*                             var cacherArray = ByteArray((hexSize/2-20))
                                                System.arraycopy(subData, 18*8/2, cacherArray, 0, (hexSize/2-2)*8)
                                                LogDeviceE.e("wpf","写入上行数据,长度=,${cacherArray.size}")
                                                fileUtils.write(rootPath,cacherArray,Constant.saveUpByteFileName)

                         */
/*                                                var cacherArray = ByteArray(subData.size - 10)
                                                System.arraycopy(subData, 9, cacherArray, 0, subData.size - 10)
                                                fileUtils.write(rootPath, cacherArray, Constant.saveUpByteFileName)*/
                                                return
                                            }
                                        }
                                        //09ff000001f002
                                        if (keyData == "01f002"){
                                            val hexSize = hexString.length
                                            val saveData = hexString.substring(18,hexSize-2)
                                            LogUtil.e("下行数据= $saveData")
                                            if (saveData.isNotEmpty()){
                                                LogDeviceE.e("wpf","写入下行数据,长度=,${saveData.length}")
                                                fileUtils.write(rootPath,saveData,Constant.saveFileName2)
                                                var cacherArray = ByteArray(subData.size-10)
                                                System.arraycopy(subData, 9, cacherArray, 0, subData.size-10)
                                                hexToPcmData(cacherArray,Constant.saveDownPcmFile,downOutBufferedStream,downOutFileStream)


/*                                                var cacherArray = ByteArray(subData.size-10)
                                                System.arraycopy(subData, 9, cacherArray, 0, subData.size-10)
                                                fileUtils.write(rootPath,cacherArray,Constant.saveDownByteFile)*/

                            /*                    val byteString = BytesUtil.byteArrToBinStr(cacherArray)
                                                LogDeviceE.e("wpf","写入下行数据,长度=,${cacherArray.size}, $byteString")*/

                                                return
                                            }
                                        }
                                    }
                                    //显示出来
                                    val msg = mHandler.obtainMessage()
                                    msg.what = DISPATCHER_CMD
                                    msg.obj = subData
                                    mHandler.sendMessage(msg)
                                }
                            }
                        }
                    }
                    mDeviceManager?.addSppConnnectStatuListener(this)
                    add3952Cmd()
                }
                mViewModel.connectDevice(mDeviceManager!!, mProductModel, device.macAddress)
            }
        }
    }

    private fun dispatch(data: ByteArray) {
        val cmd = BytesUtil.bytesToHexString(data)
        val cmdModel = CmdModel(cmd, CmdModel.REPLY_MSG, System.currentTimeMillis())
        cmdAdapter.addData(cmdModel)
        rvCmd.smoothScrollToPosition(mCmdList.lastIndex)
        LogUtil.e("receive === $cmd")
    }

    private fun sendCmd(cmd: String) {
        if (mDeviceManager != null) {
            mDeviceManager?.commandLink?.setWhichAction(BytesUtil.toBytes(cmd))

            val cmdModel = CmdModel(cmd, CmdModel.SEND_MSG, System.currentTimeMillis())
            cmdAdapter.addData(cmdModel)
            rvCmd.smoothScrollToPosition(mCmdList.lastIndex)
        }
    }

    private fun showItemDialog(position: Int) {
        val builder = AlertDialog.Builder(this)
        val model = mFuncList[position]
        builder.setCancelable(true)
        val lesson = arrayOf("编辑", "删除")
        val dialog = builder.setItems(lesson) { dialog, which ->
            when (which) {
                0 -> {
                    showAddDialog(model)
                }
                1 -> {
                    mViewModel.deleteCmdFunction(model)
                    functionAdapter.remove(model)
                }
            }
        }.create()
        dialog.show() //显示对话框
    }

    private fun showAddDialog(model: CmdFunctionModel? = null) {
        val view = LayoutInflater.from(this).inflate(R.layout.layout_add_cmd_function_dialog, null)
        alertDialog = AlertDialog.Builder(this).setView(view).create()
        val etCmdName = view.findViewById<EditText>(R.id.etCmdName)
        val etCmd = view.findViewById<EditText>(R.id.etCmd)
        val btSave = view.findViewById<Button>(R.id.btSave)
        val btCancel = view.findViewById<Button>(R.id.btCancel)

        if (model != null) {
            etCmd.setText(model.cmd)
            etCmdName.setText(model.name)
        }

        btSave.setOnClickListener {
            alertDialog?.dismiss()
            val name = etCmdName.text.toString().trim()
            val cmd = etCmd.text.toString().trim()

            if (model != null) {
                model.name = name
                model.cmd = cmd
                functionAdapter.setData(mFuncList.indexOf(model), model)
                mViewModel.updateCmdFunction(model)
            } else {
                val newModel = CmdFunctionModel(name, cmd, mProductModel.productCode)
                newModel.createTime = System.currentTimeMillis()
                functionAdapter.addData(newModel)
                mViewModel.insertCmdFunctionModel(newModel)
            }
        }
        btCancel.setOnClickListener { alertDialog?.dismiss() }
        alertDialog?.show()
    }

    override fun OnSppCnnError(macAddress: String?) {
        LogUtil.e("OnSppCnnError")
        mDeviceManager?.destroy(true)
        mDeviceManager = null
        runOnUiThread { addSystemMsg("Spp出错，已断开！") }
    }

    override fun OnSppConnected(macAddress: String?, deviceName: String?) {
        LogUtil.e("OnSppConnected macAddress === $macAddress")
        runOnUiThread { addSystemMsg("Spp已连接！") }
    }

    override fun OnSppDisconnected(macAddress: String?) {
        LogUtil.e("OnSppDisconnected")
        runOnUiThread { addSystemMsg("Spp已经断开！") }
    }

    override fun onDestroy() {
        super.onDestroy()
        mDeviceManager?.destroy(true)

        downOutBufferedStream?.flush()
        downOutBufferedStream?.close()
        downOutFileStream?.flush()
        downOutFileStream?.close()

        upOutBufferedStream?.flush()
        upOutBufferedStream?.close()
        upOutFileStream?.flush()
        upOutFileStream?.close()

    }

    //3952特殊指令添加
    private fun add3952Cmd(){
//        if (mProductModel.productCode != "A3952")return
        if (mDeviceManager != null) {
           var byteUpArray =  mDeviceManager?.commandLink?.assemblyCommand(BytesUtil.toBytes("08ee000000f001"),BytesUtil.toBytes("01"))
            byteUpArray?.let {
                var byteString = BytesUtil.bytesToHexString(byteUpArray,byteUpArray.size)
                LogUtil.e("wpf","打开上行:::${byteString}")
                mViewModel.getCmdProductModels(byteString,mProductModel.productCode){
                    if (it != null && it.isEmpty()){
                        val newModel = CmdFunctionModel("打开上行", byteString, mProductModel.productCode)
                        newModel.createTime = System.currentTimeMillis()
                        functionAdapter.addData(newModel)
                        mViewModel.insertCmdFunctionModel(newModel)
                    }
                }
            }
            var closeUpCmd =  mDeviceManager?.commandLink?.assemblyCommand(BytesUtil.toBytes("08ee000000f001"),BytesUtil.toBytes("00"))
            closeUpCmd?.let {
                var byteString = BytesUtil.bytesToHexString(closeUpCmd,closeUpCmd.size)
                LogUtil.e("wpf","关闭上行:::${byteString}")
                mViewModel.getCmdProductModels(byteString,mProductModel.productCode){
                    if (it != null && it.isEmpty()){
                        val newModel = CmdFunctionModel("关闭上行", byteString, mProductModel.productCode)
                        newModel.createTime = System.currentTimeMillis()
                        functionAdapter.addData(newModel)
                        mViewModel.insertCmdFunctionModel(newModel)
                    }
                }
            }

            var byteDownArray =  mDeviceManager?.commandLink?.assemblyCommand(BytesUtil.toBytes("08ee000000f002"),BytesUtil.toBytes("01"))
            byteDownArray?.let {
                var byteString = BytesUtil.bytesToHexString(byteDownArray,byteDownArray.size)
                LogUtil.e("wpf","打开下行:::${byteString}")
                mViewModel.getCmdProductModels(byteString,mProductModel.productCode){
                    if (it != null && it.isEmpty()){
                        val newModel = CmdFunctionModel("打开下行", byteString, mProductModel.productCode)
                        newModel.createTime = System.currentTimeMillis()
                        functionAdapter.addData(newModel)
                        mViewModel.insertCmdFunctionModel(newModel)
                    }
                }
            }
            var closeDownCmd =  mDeviceManager?.commandLink?.assemblyCommand(BytesUtil.toBytes("08ee000000f002"),BytesUtil.toBytes("00"))
            closeDownCmd?.let {
                var byteString = BytesUtil.bytesToHexString(closeDownCmd,closeDownCmd.size)
                LogUtil.e("wpf","关闭下行:::${byteString}")
                mViewModel.getCmdProductModels(byteString,mProductModel.productCode){
                    if (it != null && it.isEmpty()){
                        val newModel = CmdFunctionModel("关闭下行", byteString, mProductModel.productCode)
                        newModel.createTime = System.currentTimeMillis()
                        functionAdapter.addData(newModel)
                        mViewModel.insertCmdFunctionModel(newModel)
                    }
                }
            }
        }
    }
    //读取字节数组文件
    private fun byteFileToPcm(resPath:String,opusPath:String) {
        //1.读取文件流opus 2.编码为pcm 2.输出文件流
        val inputFile = File(resPath)
        val inputFileStream = FileInputStream(inputFile)
        var inputBufferedStream = BufferedInputStream(inputFileStream)

        val outFile = File(opusPath)
        if (!outFile.parentFile.exists()) outFile.parentFile.mkdirs()
        if (outFile.exists()) outFile.delete()//删除旧文件
        outFile.createNewFile()
        val outFileStream = FileOutputStream(outFile, true)
        val outBufferedStream = BufferedOutputStream(outFileStream)
        try {
            var bytes = ByteArray(1024);
            var len = 0;
            while ((inputBufferedStream.read(bytes).also { len = it })!= -1){
                val bufferArray: ByteArray = bytes
                val bufferSzie = 20
                val allSize = len
                var point = 0
                var offerSize = 0
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
                    val size = tntOpusUtils!!.decode(decoderHandler!!, cacherArray, decodeBufferArray)
                    if (size > 0) {
                        //opus数据
                        val decodeArray = ShortArray(size)
                        System.arraycopy(decodeBufferArray, 0, decodeArray, 0, size)
                        val byteArray: ByteArray = ByteUtils.shortArr2byteArr(decodeArray, size)

//                    LogUtil.e("原数据：${BytesUtil.bytesToHexString(cacherArray)}")
                        LogUtil.e("写入文件：${BytesUtil.bytesToHexString(byteArray)}")
                        outBufferedStream?.write(byteArray)
                        outBufferedStream?.flush()
                        outFileStream?.flush()
                        offerSize += byteArray.size
                    }

                    point += bufferSzie

                }
            }
            inputBufferedStream.close();
        } catch (e:Exception) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            LogUtil.e("出现异常")
        }finally{
            //最后一定要关闭文件流
            try {
                inputBufferedStream.close()
                outBufferedStream.flush()
                outBufferedStream.close()
                outFileStream.flush()
                outFileStream.close()
            } catch (e:IOException) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
    }
    private fun hexToPcmData(data: ByteArray, fileName: String, outBufferedStream: BufferedOutputStream?, outFileStream: FileOutputStream?){
        val bufferArray: ByteArray = data

        val bufferSzie = 20
        val allSize = bufferArray.size
        var point = 0
        var offerSize = 0
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
            val size = tntOpusUtils!!.decode(decoderHandler!!, cacherArray, decodeBufferArray)
            if (size > 0) {
                //opus数据
                val decodeArray = ShortArray(size)
                System.arraycopy(decodeBufferArray, 0, decodeArray, 0, size)
                val byteArray: ByteArray = ByteUtils.shortArr2byteArr(decodeArray, size)

//                    LogUtil.e("原数据：${BytesUtil.bytesToHexString(cacherArray)}")
                LogUtil.e("写入文件：${BytesUtil.bytesToHexString(byteArray)}")

                fileUtils.write(rootPath,byteArray,fileName)
//                outBufferedStream?.write(byteArray)
//                outBufferedStream?.flush()
//                outFileStream?.flush()
                offerSize += byteArray.size
            }

//            LogUtil.e("point=${point},size =${allSize},offerSize =${offerSize} ")
            point += bufferSzie

        }
    }
}