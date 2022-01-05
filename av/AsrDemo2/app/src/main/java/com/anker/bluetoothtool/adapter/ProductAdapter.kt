package com.anker.bluetoothtool.adapter

import com.anker.bluetoothtool.model.ProductModel
import com.anker.bluetoothtool.R
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.viewholder.BaseViewHolder

/**
 *  Author: anker
 *  Time:   2021/7/22
 *  Description : This is description.
 */
class ProductAdapter(resId: Int, list: MutableList<ProductModel>) : BaseQuickAdapter<ProductModel, BaseViewHolder>(resId, list) {
    override fun convert(holder: BaseViewHolder, item: ProductModel) {
        holder.setText(R.id.textView, item.productCode)
    }
}