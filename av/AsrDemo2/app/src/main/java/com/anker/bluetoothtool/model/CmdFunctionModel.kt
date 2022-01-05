package com.anker.bluetoothtool.model

import androidx.room.Entity
import androidx.room.Ignore
import androidx.room.PrimaryKey

/**
 *  Author: anker
 *  Time:   2021/8/17
 *  Description : This is description.
 */
@Entity
data class CmdFunctionModel(
    var name: String = "",
    var cmd: String = "",
    var parentCode: String = "",
    var createTime: Long = 0,
    @Ignore
    var isSelect: Boolean = false,
    @PrimaryKey(autoGenerate = true)
    var id: Int = 0
)