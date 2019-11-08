package me.king.jake.fis.callbacks

import androidx.recyclerview.widget.DiffUtil
import me.king.jake.fis.models.InventoryDTO

class InventoryListDiffCallback(
    private val oldList: ArrayList<InventoryDTO>,
    private val newList: ArrayList<InventoryDTO>
) : DiffUtil.Callback() {

    override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        return oldList[oldItemPosition].barcode == newList[newItemPosition].barcode
    }

    override fun getOldListSize(): Int {
        return oldList.size
    }

    override fun getNewListSize(): Int {
        return newList.size
    }

    override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        return oldList[oldItemPosition] == newList[newItemPosition]
    }
}