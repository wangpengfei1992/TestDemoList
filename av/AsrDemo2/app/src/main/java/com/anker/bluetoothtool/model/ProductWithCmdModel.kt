package com.anker.bluetoothtool.model

import androidx.room.Embedded
import androidx.room.Relation

/**
 *  Author: anker
 *  Time:   2021/8/24
 *  Description : This is description.
 */
data class ProductWithCmdModel(
    @Embedded
    val product: ProductModel,
    @Relation(
        parentColumn = "productCode",
        entityColumn = "parentCode"
    )
    val cmds: List<CmdFunctionModel>
)
