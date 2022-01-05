package com.anker.bluetoothtool.adapter

import com.anker.bluetoothtool.R
import com.anker.bluetoothtool.deviceExport.util.BytesUtil
import com.anker.bluetoothtool.model.CmdModel
import com.chad.library.adapter.base.BaseMultiItemQuickAdapter
import com.chad.library.adapter.base.viewholder.BaseViewHolder
import java.text.SimpleDateFormat
import java.util.*

/**
 *  Author: anker
 *  Time:   2021/7/22
 *  Description : This is description.
 */
class CmdAdapter(list: MutableList<CmdModel>) : BaseMultiItemQuickAdapter<CmdModel, BaseViewHolder>(list) {

    init {
        addItemType(CmdModel.SYSTEM_MSG, R.layout.layout_cmd_item_system)
        addItemType(CmdModel.SEND_MSG, R.layout.layout_cmd_item_send)
        addItemType(CmdModel.REPLY_MSG, R.layout.layout_cmd_item_reply)
    }

    companion object {
        val dataFormat = SimpleDateFormat("HH:mm:ss SSS", Locale.US)
    }

    override fun convert(holder: BaseViewHolder, item: CmdModel) {
        when (item.itemType) {
            CmdModel.SYSTEM_MSG -> {
                holder.setText(R.id.tvCmd, item.content)
            }
            CmdModel.SEND_MSG -> {
                val time = dataFormat.format(item.time)
                holder.setText(R.id.tvCmd, "发送($time)：" + BytesUtil.getStringAddSpace(item.content))
            }
            CmdModel.REPLY_MSG -> {
                val time = dataFormat.format(item.time)
                holder.setText(R.id.tvCmd, "回复($time)：" + BytesUtil.getStringAddSpace(item.content))
            }
        }

    }
}