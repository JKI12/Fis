package me.king.jake.fis.models

import android.os.Parcelable
import com.google.gson.Gson
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

@Parcelize
class InventoryDTO(@SerializedName("barcode")
                   private val _barcode: String?,
                   @SerializedName("item")
                   private val _item: ItemDTO? = null,
                   @SerializedName("properties")
                   private val _properties: PropertiesDTO? = null) : BaseModel(), Parcelable {

    val barcode
        get() = _barcode ?: throw IllegalArgumentException("Barcode is required")

    val properties
        get() = _properties

    val item
        get() = _item

    companion object {
        private val gson = Gson()

        fun fromJson(json: String): InventoryDTO {
            return gson.fromJson(json, InventoryDTO::class.java)
        }
    }
}