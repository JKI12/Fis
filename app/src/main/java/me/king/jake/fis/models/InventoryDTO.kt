package me.king.jake.fis.models

import android.os.Parcelable
import com.google.gson.Gson
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

@Parcelize
data class InventoryDTO(@SerializedName("barcode")
                   private val _barcode: String?,
                   @SerializedName("item")
                   var item: ItemDTO? = null,
                   @SerializedName("properties")
                   var properties: PropertiesDTO? = null) : BaseModel(), Parcelable {

    val barcode
        get() = _barcode ?: throw IllegalArgumentException("Barcode is required")

    companion object {
        private val gson = Gson()

        fun fromJson(json: String): InventoryDTO {
            return gson.fromJson(json, InventoryDTO::class.java)
        }

        const val parcelableName = "inventory_item"
    }
}