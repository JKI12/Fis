package me.king.jake.fis.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import me.king.jake.fis.R
import me.king.jake.fis.models.InventoryDTO

class InventoryListAdapter(private var inventoryList: ArrayList<InventoryDTO>, var onClickCallback: (item: InventoryDTO) -> Unit) : RecyclerView.Adapter<InventoryListAdapter.ViewHolder>() {
    fun updateInventoryList(newList: ArrayList<InventoryDTO>) {
        inventoryList = newList
        this.notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.view_inventory_list_item, parent, false)
        return ViewHolder(view)
    }

    override fun getItemCount(): Int {
        return inventoryList.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val inventoryItem = inventoryList[position]

        holder.apply {
            this.productTitle.text = inventoryItem.item!!.title
            this.productQuantity.text = String.format(
                holder.productQuantity.context.getString(R.string.overview_quantity),
                inventoryItem.properties!!.quantity)
            this.wrapper.setOnClickListener {
                onClickCallback(inventoryItem)
            }
        }
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var wrapper: RelativeLayout = itemView.findViewById(R.id.product_wrapper)
        var productImage: ImageView = itemView.findViewById(R.id.iv_product_image)
        var productTitle: TextView = itemView.findViewById(R.id.tv_product_title)
        var productQuantity: TextView = itemView.findViewById(R.id.tv_product_quantity)
    }
}