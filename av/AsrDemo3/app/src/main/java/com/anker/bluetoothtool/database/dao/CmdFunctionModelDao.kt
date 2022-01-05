package com.anker.bluetoothtool.database.dao

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Transaction
import com.anker.bluetoothtool.model.CmdFunctionModel
import com.anker.bluetoothtool.model.ProductWithCmdModel

@Dao
interface CmdFunctionModelDao : BaseDao<CmdFunctionModel> {

    @Transaction
//    @Query("SELECT * FROM ProductModel where productCode = '' or productCode = :productCode")
    @Query("SELECT * FROM ProductModel")
    suspend fun getProductWithCmdModels(): List<ProductWithCmdModel>

    @Query("SELECT * FROM CmdFunctionModel where cmd = :cmdContent AND parentCode = :code order by createTime asc")
    suspend fun getCmdProductModels(cmdContent: String,code : String): List<CmdFunctionModel>
}