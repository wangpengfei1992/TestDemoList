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
class CommandActivity : AppCompatActivity(), OnSppConnectStateChangeListener, SearchCallback {
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
    private var isSaveDeviceData = true //?????????????????????
    private val fileUtils by lazy { FileUtil() }
    private var rootPath = ""
    private var tntOpusUtils: OpusUtils ?= null
    private var decoderHandler:Long ?= null



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
                    Toast.makeText(this, "???????????????????????????", Toast.LENGTH_LONG).show()
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
            etCmd.setText("")
            mFuncList.forEach { it.isSelect = false }
            functionAdapter.notifyDataSetChanged()
        }

        btSend.setOnClickListener {
            if (mDeviceManager != null && mDeviceManager?.isConnected!!) {
                val cmd = etCmd.text.trim().toString()
                if (cmd.isNotEmpty()) {
                    sendCmd(cmd)
                } else {
                    addSystemMsg("??????????????????...")
                }
            } else {
                addSystemMsg("??????????????????...")
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
            if (it) addSystemMsg("?????????????????????")
            else addSystemMsg("?????????????????????")
        })
        rootPath = fileUtils.initFileRoot(baseContext)+"/AsrDemo2"
        tntOpusUtils = OpusUtils.getInstant()
        decoderHandler = tntOpusUtils?.createDecoder(8000, 1, biteRate = 8000)

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
        addSystemMsg("????????????${mProductModel.productCode}????????????????????????????????????")
    }

    override fun hasOneDevice(device: AnkerWorkDevice) {
        mDevice = device
        LogUtil.e("hasOneDevice device === ${mDevice?.macAddress}")
        addSystemMsg("????????????${mProductModel.productCode}?????????mac????????????${mDevice?.macAddress}")
        if (!isInitFind) {
            connectDevice()
        }
    }

    override fun hasManyDevices(list: List<AnkerWorkDevice>) {
        mDevice = list[0]
        LogUtil.e("hasManyDevices device === ${mDevice?.macAddress}")
        addSystemMsg("????????????${mProductModel.productCode}???????????????????????????????????????mac????????????${mDevice?.macAddress}")
        if (!isInitFind) {
            connectDevice()
        }
    }
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
                            LogDeviceE.e("wpf","?????????????????????$oldData")
                            //??????????????????????????????
                            val parseResult = ParseData.parseDataToSegment(mDataInfo!!)
                            if (!parseResult) {
                                LogDeviceE.e("wpf","???????????????${!parseResult}")
                                return
                            }
                            mDataInfo!!.eventListArr?.run {
                                val it: Iterator<ByteArray?> = this.iterator()
                                while (it.hasNext()) {
                                    val subData = it.next()
                                    val hexString = BytesUtil.bytesToHexString(subData, subData!!.size)
//                                    LogUtil.e(TAG,hexString)
                                    //????????????checksum???????????????
                                    val isLegally = CheckSumUtil.isLegallyCmd(subData)
                                    if (subData.size < MIN_CMD_LENGTH || !isLegally) {
                                        LogDeviceE.e("wpf","??????????????????")
                                        fileUtils.write(rootPath,hexString,"error.txt")
                                        continue
                                    }
                                    //??????????????????????????????
                                    val keyData = hexString.substring(8,14)
//                                    LogUtil.e("keyData= $keyData")
                                    if (isSaveDeviceData && hexString.startsWith("09ff")
                                            &&(keyData == "01f001" || keyData == "01f002")){
                                        if (keyData == "01f001"){
                                            val hexSize = hexString.length
                                            val saveData = hexString.substring(18,hexSize-2)
                                            LogUtil.e("????????????= $saveData")
                                            if (saveData.isNotEmpty()){
                                                //2????????????
                                                var cacherArray = ByteArray(subData.size-10)
                                                System.arraycopy(subData, 9, cacherArray, 0, subData.size-10)
                                                hexToPcmData(cacherArray,Constant.saveUpPcmFile)
                                                //16????????????
                                                fileUtils.write(rootPath,saveData,Constant.saveFileName)
                                                return
                                            }
                                        }
                                        //09ff000001f002
                                        if (keyData == "01f002"){
                                            val hexSize = hexString.length
                                            val saveData = hexString.substring(18,hexSize-2)
                                            LogUtil.e("????????????= $saveData")
                                            if (saveData.isNotEmpty()){
                                                LogDeviceE.e("wpf","??????????????????,??????=,${saveData.length}")
                                                fileUtils.write(rootPath,saveData,Constant.saveFileName2)
                                                var cacherArray = ByteArray(subData.size-10)
                                                System.arraycopy(subData, 9, cacherArray, 0, subData.size-10)
                                                hexToPcmData(cacherArray,Constant.saveDownPcmFile)
                                                return
                                            }
                                        }
                                    }
                                    //????????????
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
        val lesson = arrayOf("??????", "??????")
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
        dialog.show() //???????????????
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
        runOnUiThread { addSystemMsg("Spp?????????????????????") }
    }

    override fun OnSppConnected(macAddress: String?, deviceName: String?) {
        LogUtil.e("OnSppConnected macAddress === $macAddress")
        runOnUiThread { addSystemMsg("Spp????????????") }
    }

    override fun OnSppDisconnected(macAddress: String?) {
        LogUtil.e("OnSppDisconnected")
        runOnUiThread { addSystemMsg("Spp???????????????") }
    }

    override fun onDestroy() {
        super.onDestroy()
        mDeviceManager?.destroy(true)
    }

    //3952??????????????????
    private fun add3952Cmd(){
//        if (mProductModel.productCode != "A3952")return
        if (mDeviceManager != null) {
           var byteUpArray =  mDeviceManager?.commandLink?.assemblyCommand(BytesUtil.toBytes("08ee000000f001"),BytesUtil.toBytes("01"))
            byteUpArray?.let {
                var byteString = BytesUtil.bytesToHexString(byteUpArray,byteUpArray.size)
                LogUtil.e("wpf","????????????:::${byteString}")
                mViewModel.getCmdProductModels(byteString,mProductModel.productCode){
                    if (it != null && it.isEmpty()){
                        val newModel = CmdFunctionModel("????????????", byteString, mProductModel.productCode)
                        newModel.createTime = System.currentTimeMillis()
                        functionAdapter.addData(newModel)
                        mViewModel.insertCmdFunctionModel(newModel)
                    }
                }
            }
            var closeUpCmd =  mDeviceManager?.commandLink?.assemblyCommand(BytesUtil.toBytes("08ee000000f001"),BytesUtil.toBytes("00"))
            closeUpCmd?.let {
                var byteString = BytesUtil.bytesToHexString(closeUpCmd,closeUpCmd.size)
                LogUtil.e("wpf","????????????:::${byteString}")
                mViewModel.getCmdProductModels(byteString,mProductModel.productCode){
                    if (it != null && it.isEmpty()){
                        val newModel = CmdFunctionModel("????????????", byteString, mProductModel.productCode)
                        newModel.createTime = System.currentTimeMillis()
                        functionAdapter.addData(newModel)
                        mViewModel.insertCmdFunctionModel(newModel)
                    }
                }
            }

            var byteDownArray =  mDeviceManager?.commandLink?.assemblyCommand(BytesUtil.toBytes("08ee000000f002"),BytesUtil.toBytes("01"))
            byteDownArray?.let {
                var byteString = BytesUtil.bytesToHexString(byteDownArray,byteDownArray.size)
                LogUtil.e("wpf","????????????:::${byteString}")
                mViewModel.getCmdProductModels(byteString,mProductModel.productCode){
                    if (it != null && it.isEmpty()){
                        val newModel = CmdFunctionModel("????????????", byteString, mProductModel.productCode)
                        newModel.createTime = System.currentTimeMillis()
                        functionAdapter.addData(newModel)
                        mViewModel.insertCmdFunctionModel(newModel)
                    }
                }
            }
            var closeDownCmd =  mDeviceManager?.commandLink?.assemblyCommand(BytesUtil.toBytes("08ee000000f002"),BytesUtil.toBytes("00"))
            closeDownCmd?.let {
                var byteString = BytesUtil.bytesToHexString(closeDownCmd,closeDownCmd.size)
                LogUtil.e("wpf","????????????:::${byteString}")
                mViewModel.getCmdProductModels(byteString,mProductModel.productCode){
                    if (it != null && it.isEmpty()){
                        val newModel = CmdFunctionModel("????????????", byteString, mProductModel.productCode)
                        newModel.createTime = System.currentTimeMillis()
                        functionAdapter.addData(newModel)
                        mViewModel.insertCmdFunctionModel(newModel)
                    }
                }
            }
        }
    }
    private fun hexToPcmData(data: ByteArray, fileName: String){
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


            //????????????
            val decodeBufferArray = ShortArray(cacherArray.size * 8)
            val size = tntOpusUtils!!.decode(decoderHandler!!, cacherArray, decodeBufferArray)
            if (size > 0) {
                //opus??????
                val decodeArray = ShortArray(size)
                System.arraycopy(decodeBufferArray, 0, decodeArray, 0, size)
                val byteArray: ByteArray = ByteUtils.shortArr2byteArr(decodeArray, size)

//                    LogUtil.e("????????????${BytesUtil.bytesToHexString(cacherArray)}")
                LogUtil.e("???????????????${BytesUtil.bytesToHexString(byteArray)}")

                fileUtils.write(rootPath,byteArray,fileName)
                offerSize += byteArray.size
            }

//            LogUtil.e("point=${point},size =${allSize},offerSize =${offerSize} ")
            point += bufferSzie

        }
    }
}