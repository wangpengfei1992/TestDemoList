package com.anker.bluetoothtool.database.dao

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Transaction
import com.anker.bluetoothtool.model.ProductModel

@Dao
interface ProductModelDao : BaseDao<ProductModel> {

    @Query("SELECT * FROM ProductModel where isBle = :isBle order by createTime asc")
    suspend fun getAllProductModel(isBle: Boolean): List<ProductModel>

}