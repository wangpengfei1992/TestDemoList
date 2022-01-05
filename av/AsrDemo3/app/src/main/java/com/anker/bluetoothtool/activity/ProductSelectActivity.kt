package com.anker.bluetoothtool.activity

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.widget.ViewPager2
import com.anker.bluetoothtool.R
import com.anker.bluetoothtool.activity.CommandActivity.Companion.IS_BLE
import com.anker.bluetoothtool.activity.CommandActivity.Companion.PRODUCT_MODEL
import com.anker.bluetoothtool.adapter.ProductAdapter
import com.anker.bluetoothtool.model.ProductModel
import com.anker.bluetoothtool.util.*
import com.anker.bluetoothtool.view.CommonTitleBar
import com.anker.bluetoothtool.viewmodel.ProductViewModel
import com.google.android.material.tabs.TabLayout
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
    private val tabLayout by lazy { findViewById<TabLayout>(R.id.tabLayout) }
    private var alertDialog: AlertDialog? = null
    private var isBle = false

    companion object {
        const val TYPE_SPP = 0
        const val TYPE_BLE = 1
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.product_select_activity)

        checkPermission()

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
            imgLeft.apply {
                visibility = View.GONE
            }
        }

        mAdapter = ProductAdapter(R.layout.product_select_item, productList)
        recyclerView.addItemDecoration(LinearItemDecoration(this, Color.BLACK))
        recyclerView.adapter = mAdapter

        mAdapter.setOnItemClickListener { adapter, view, position ->
            val bundle = Bundle()
            bundle.putSerializable(PRODUCT_MODEL, productList[position])
            bundle.putBoolean(IS_BLE, isBle)
            val intent = Intent(this, CommandActivity::class.java)
            intent.putExtras(bundle)
            startActivity(intent)
        }
        mAdapter.setOnItemLongClickListener { adapter, view, position ->
            showItemDialog(position)
            true
        }
        val other_fuc = findViewById<Button>(R.id.other_fuc)
        other_fuc.setOnClickListener {
            val intent = Intent(baseContext, MainActivity::class.java)
            startActivity(intent)
        }
        tabLayout.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab) {
                isBle = tab.position != TYPE_SPP
                Log.d("haha", "isBle === " + isBle)
                getProducts()
            }

            override fun onTabUnselected(tab: TabLayout.Tab?) {
            }

            override fun onTabReselected(tab: TabLayout.Tab?) {
            }
        })

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
            if (etProductCode.text.isEmpty()) {
                Toast.makeText(this, "ProductCode cannot be empty!", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
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

    private val ALL_PERMISSIONS = arrayOf(Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION)
    private fun checkPermission() { //判断是否有访问位置的权限，没有权限，直接申请位置权限
        if (!allPermissionsGranted()) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                requestPermissions(ALL_PERMISSIONS, 1)
            }
        }
    }

    private fun allPermissionsGranted() = ALL_PERMISSIONS.all {
        ContextCompat.checkSelfPermission(this, it) == PackageManager.PERMISSION_GRANTED
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when (requestCode) {
            1 -> {
                Log.d("haha", "onRequestPermissionsResult")
            }
        }
    }

    override fun onResume() {
        super.onResume()
        getProducts()
    }

    private fun getProducts() {
        mViewModel.getAllProducts(isBle)
    }
}