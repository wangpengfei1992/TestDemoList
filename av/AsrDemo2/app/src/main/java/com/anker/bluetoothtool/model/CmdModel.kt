package com.anker.bluetoothtool.model

import com.chad.library.adapter.base.entity.MultiItemEntity

data class CmdModel(
    var content: String = "",
    var type: Int = 0,
    var time: Long = 0
):  MultiItemEntity {

    companion object {
        const val SYSTEM_MSG = 0
        const val SEND_MSG = 1
        const val REPLY_MSG = 2
    }

    override val itemType: Int = type
}
