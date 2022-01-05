package com.anker.bluetoothtool.activity

import android.os.Bundle
import android.view.View
import android.widget.EditText
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.anker.bluetoothtool.R
import com.anker.bluetoothtool.deviceExport.util.LogUtil
import com.anker.bluetoothtool.model.CmdFunctionModel
import com.anker.bluetoothtool.model.ProductModel
import com.anker.bluetoothtool.model.ProductModelGson
import com.anker.bluetoothtool.util.ClipboardUtil
import com.anker.bluetoothtool.util.Constant.DEF_JSON_PRODUCT_EDITTEXT
import com.anker.bluetoothtool.util.jsonToList
import com.anker.bluetoothtool.view.CommonTitleBar
import com.anker.bluetoothtool.viewmodel.ProductViewModel

/**
 *  Author: anker
 *  Time:   2021/8/30
 *  Description : This is description.
 */
class InputActivity : AppCompatActivity() {

    private val titleBar by lazy { findViewById<CommonTitleBar>(R.id.titleBar) }
    private val etCopy by lazy { findViewById<EditText>(R.id.etCopy) }

    private val mViewModel: ProductViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.layout_input_activity)

        etCopy.setText(DEF_JSON_PRODUCT_EDITTEXT)

        titleBar.apply {
            imgLeft.setOnClickListener {
                finish()
            }
            imgRight.apply {
                visibility = View.VISIBLE
                setImageResource(R.drawable.ic_save)
                setOnClickListener {
                    save()
                }
            }
            imgRight2.apply {
                visibility = View.VISIBLE
                setImageResource(R.drawable.ic_paste)
                setOnClickListener {
                    val pasteStr = ClipboardUtil.paste(this@InputActivity)
                    etCopy.setText(pasteStr)
                }
            }
        }
    }

    private fun save() {
        val json = etCopy.text.toString().trim()
        try {
            val models = json.jsonToList<ProductModelGson>()
            val createTime = System.currentTimeMillis()

            val products = mutableListOf<ProductModel>()
            var productModel: ProductModel

            val cmds = mutableListOf<CmdFunctionModel>()
            var cmd: CmdFunctionModel
            models.forEach {
                productModel = if (!it.isBle) {
                    ProductModel(it.productCode, it.sppUUID, isBle = it.isBle, createTime = createTime)
                } else {
                    ProductModel(
                        it.productCode,
                        serviceUUID = it.serviceUUID,
                        writeChara = it.writeChara,
                        notifyChara = it.notifyChara,
                        isBle = it.isBle,
                        createTime = createTime
                    )
                }
                products.add(productModel)

                it.cmds.forEach { cmdModel ->
                    cmd = CmdFunctionModel(cmdModel.name, cmdModel.cmd, it.productCode)
                    cmds.add(cmd)
                }
            }

            //保存到数据库
            mViewModel.insertProductsWithCmds(products, cmds) {
                LogUtil.e("保存成功")
                finish()
            }
        } catch (e: Throwable) {
            LogUtil.d("解析出错： ${e.message}")
            Toast.makeText(this, "json格式错误，保存失败", Toast.LENGTH_LONG).show()
        }
    }
}