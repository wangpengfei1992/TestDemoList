package com.anker.bluetoothtool.activity

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.LinearLayout
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.RecyclerView
import com.anker.bluetoothtool.R
import com.anker.bluetoothtool.activity.CommandActivity.Companion.PRODUCT_MODEL
import com.anker.bluetoothtool.adapter.ProductAdapter
import com.anker.bluetoothtool.model.ProductModel
import com.anker.bluetoothtool.util.*
import com.anker.bluetoothtool.view.CommonTitleBar
import com.anker.bluetoothtool.viewmodel.ProductViewModel
import com.google.gson.Gson


/**
 *  Author: anker
 *  Time:   2021/7/22
 *  Description : This is description.
 */
class ProductSelectActivity : AppCompatActivity() {

    private lateinit var mAdapter: ProductAdapter
    private val productList = mutableListOf<ProductModel>()

    private val mViewModel: ProductViewModel by viewModels()

    private val titleBar by lazy { findViewById<CommonTitleBar>(R.id.titleBar) }
    private val recyclerView by lazy { findViewById<RecyclerView>(R.id.recyclerView) }
    private var alertDialog: AlertDialog? = null
    private var isBle = false

    companion object {
        const val IS_BLE = "is_ble"
        const val TYPE_SPP = 0
        const val TYPE_BLE = 1
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.product_select_activity)

        isBle = intent.getBooleanExtra(IS_BLE, false)

        titleBar.apply {
            imgRight.apply {
                visibility = View.VISIBLE
                setOnClickListener {
                    showAddDialog()
                }
                setImageResource(R.drawable.ic_add)
            }
            tvRight.apply {
                visibility = View.VISIBLE
                setOnClickListener {
                    startActivity(Intent(this@ProductSelectActivity, InputActivity::class.java))
                }
                text = "导入"
            }
            imgLeft.setOnClickListener {
                finish()
            }
        }

        mAdapter = ProductAdapter(R.layout.product_select_item, productList)
        recyclerView.addItemDecoration(LinearItemDecoration(this, Color.BLACK))
        recyclerView.adapter = mAdapter

        mAdapter.setOnItemClickListener { adapter, view, position ->
            val bundle = Bundle()
            bundle.putSerializable(PRODUCT_MODEL, productList[position])
            val intent = Intent(this, CommandActivity::class.java)
            intent.putExtras(bundle)
            startActivity(intent)
        }
        mAdapter.setOnItemLongClickListener { adapter, view, position ->
            showItemDialog(position)
            true
        }

        mViewModel.products.observe(this, {
            if (it.isNotEmpty()) {
                mAdapter.setList(it)
            } else {
                mViewModel.insertDefProduct(isBle)
            }
        })
    }

    private fun showAddDialog(model: ProductModel? = null) {
        val view = LayoutInflater.from(this).inflate(R.layout.layout_add_product_dialog, null)
        alertDialog = AlertDialog.Builder(this).setView(view).create()
        val llSpp = view.findViewById<LinearLayout>(R.id.llSpp)
        val llBle = view.findViewById<LinearLayout>(R.id.llBle)
        val etProductCode = view.findViewById<EditText>(R.id.etProductCode)
        val etUUID = view.findViewById<EditText>(R.id.etUUID)
        val etServiceUUID = view.findViewById<EditText>(R.id.etServiceUUID)
        val etWriteC = view.findViewById<EditText>(R.id.etWriteC)
        val etNotifyC = view.findViewById<EditText>(R.id.etNotifyC)

        val btSave = view.findViewById<Button>(R.id.btSave)
        val btCancel = view.findViewById<Button>(R.id.btCancel)

        if (isBle) {
            llBle.visibility = View.VISIBLE
            llSpp.visibility = View.GONE
        } else {
            llBle.visibility = View.GONE
            llSpp.visibility = View.VISIBLE
        }

        if (model != null) {
            etProductCode.setText(model.productCode)
            etProductCode.isEnabled = false
            etUUID.setText(model.sppUUID)
            etServiceUUID.setText(model.serviceUUID)
            etWriteC.setText(model.writeChara)
            etNotifyC.setText(model.notifyChara)
        }

        btSave.setOnClickListener {
            alertDialog?.dismiss()
            val newModel: ProductModel
            if (isBle) {
                val productCode = etProductCode.text.toString().trim()
                val uuid = etServiceUUID.text.toString().trim()
                val writeC = etWriteC.text.toString().trim()
                val notifyC = etNotifyC.text.toString().trim()

                newModel = ProductModel(
                    productCode,
                    serviceUUID = uuid,
                    writeChara = writeC,
                    notifyChara = notifyC,
                    isBle = true
                )
            } else {
                val productCode = etProductCode.text.toString().trim()
                val uuid = etUUID.text.toString().trim()

                newModel = ProductModel(productCode, uuid)
            }

            if (model != null) {
                mAdapter.setData(productList.indexOf(model), newModel)
                mViewModel.updateProductModel(newModel)
            } else {
                newModel.createTime = System.currentTimeMillis()
                mAdapter.addData(newModel)
                mViewModel.insertProductModel(newModel)
            }
        }
        btCancel.setOnClickListener { alertDialog?.dismiss() }
        alertDialog?.show()
    }

    private fun showItemDialog(position: Int) {
        val builder = AlertDialog.Builder(this)
        val model = productList[position]
        builder.setCancelable(true)
        val lesson = arrayOf("编辑", "删除")
        val dialog = builder.setItems(lesson) { dialog, which ->
            when (which) {
                0 -> {
                    showAddDialog(model)
                }
                1 -> {
                    mViewModel.deleteProduct(model)
                    mAdapter.remove(model)
                }
            }
        }.create()
        dialog.show() //显示对话框
    }

    override fun onResume() {
        super.onResume()
        mViewModel.getAllProducts(isBle)
    }
}