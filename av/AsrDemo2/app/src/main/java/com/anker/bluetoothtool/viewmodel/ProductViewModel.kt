package com.anker.bluetoothtool.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.anker.ankerwork.deviceExport.device.BaseDeviceManager
import com.anker.bluetoothtool.database.AnkerWorkDatabase
import com.anker.bluetoothtool.deviceExport.util.LogUtil
import com.anker.bluetoothtool.model.CmdFunctionModel
import com.anker.bluetoothtool.model.ProductModel
import com.anker.bluetoothtool.model.ProductWithCmdModel
import com.anker.bluetoothtool.util.Constant.getDefFunction
import com.anker.bluetoothtool.util.Constant.getDefProduct
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.launch

/**
 *  Author: anker
 *  Time:   2021/8/24
 *  Description : This is description.
 */
class ProductViewModel : ViewModel() {

    val products = MutableLiveData<List<ProductModel>>()

    val productsWithCmds = MutableLiveData<List<ProductWithCmdModel>>()

    val connectResult = MutableLiveData<Boolean>()

    fun launchOnIO(block: suspend () -> Unit) {
        viewModelScope.launch(Dispatchers.IO) { block() }
    }

    fun async(block: suspend () -> Unit) {
        viewModelScope.async { block() }
    }

    fun connectDevice(deviceManager: BaseDeviceManager, productModel: ProductModel, mac: String) {
        launchOnIO {
            deviceManager.init(productModel.sppUUID, productModel.productCode)
            val result = deviceManager.connectDevice(mac)
            connectResult.postValue(result)
        }
    }

    fun insertDefProduct(isBle: Boolean) {
        launchOnIO {
            val result = AnkerWorkDatabase.getDatabase().productDao.insertAll(getDefProduct())
            getAllProducts(isBle)
        }
    }

    fun getAllProducts(isBle: Boolean) {
        launchOnIO {
            val result = AnkerWorkDatabase.getDatabase().productDao.getAllProductModel(isBle)
            products.postValue(result)
        }
    }

    fun insertDefFunction(productCode: String) {
        launchOnIO {
            val dao = AnkerWorkDatabase.getDatabase().functionDao
            dao.insertAll(getDefFunction(productCode))
            getCmdFunction()
        }
    }

    fun getCmdFunction() {
        launchOnIO {
            val result = AnkerWorkDatabase.getDatabase().functionDao.getProductWithCmdModels()
            productsWithCmds.postValue(result)
        }
    }

    fun getCmdProductModels(cmdContent: String,code : String, block: (List<CmdFunctionModel>) -> Unit) {
        launchOnIO {
            block(AnkerWorkDatabase.getDatabase().functionDao.getCmdProductModels(cmdContent,code))
        }
    }


    fun insertCmdFunctionModel(model: CmdFunctionModel) {
        launchOnIO {
            AnkerWorkDatabase.getDatabase().functionDao.insert(model)
        }
    }

    fun insertProductModel(model: ProductModel) {
        launchOnIO {
            AnkerWorkDatabase.getDatabase().productDao.insert(model)
        }
    }

    fun updateProductModel(model: ProductModel) {
        launchOnIO {
            val result = AnkerWorkDatabase.getDatabase().productDao.update(model)
        }
    }

    fun updateCmdFunction(model: CmdFunctionModel) {
        launchOnIO {
            val result = AnkerWorkDatabase.getDatabase().functionDao.update(model)
            LogUtil.e("update result === $result")
        }
    }

    fun insertProductsWithCmds(products: List<ProductModel>, cmds: List<CmdFunctionModel>, block: () -> Unit) {
        launchOnIO {
            AnkerWorkDatabase.getDatabase().productDao.insertAll(products)
            AnkerWorkDatabase.getDatabase().functionDao.insertAll(cmds)
            block()
        }
    }

    fun deleteProduct(model: ProductModel) {
        launchOnIO {
            AnkerWorkDatabase.getDatabase().productDao.delete(model)
        }
    }

    fun deleteCmdFunction(model: CmdFunctionModel) {
        launchOnIO {
            AnkerWorkDatabase.getDatabase().functionDao.delete(model)
        }
    }

}