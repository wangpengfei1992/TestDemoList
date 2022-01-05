package com.anker.bluetoothtool.adapter

import android.widget.LinearLayout
import com.anker.bluetoothtool.R
import com.anker.bluetoothtool.model.CmdFunctionModel
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.viewholder.BaseViewHolder

/**
 *  Author: anker
 *  Time:   2021/7/22
 *  Description : This is description.
 */
class CmdFunctionAdapter(resId: Int, list: MutableList<CmdFunctionModel>) : BaseQuickAdapter<CmdFunctionModel, BaseViewHolder>(resId, list) {
    override fun convert(holder: BaseViewHolder, item: CmdFunctionModel) {
        holder.setText(R.id.tvName, item.name)
        holder.setText(R.id.tvCmd, item.cmd)
        holder.getView<LinearLayout>(R.id.llRoot).isSelected = item.isSelect
    }
}