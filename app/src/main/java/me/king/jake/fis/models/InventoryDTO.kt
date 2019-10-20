package me.king.jake.fis.models

import com.google.gson.Gson
import com.google.gson.annotations.SerializedName

class InventoryDTO(@SerializedName("barcode")
                   private val _barcode: String?) : BaseModel() {

    val barcode
        get() = _barcode ?: throw IllegalArgumentException("Barcode is required")

    companion object {
        private val gson = Gson()

        fun fromJson(json: String): InventoryDTO {
            return gson.fromJson(json, InventoryDTO::class.java)
        }
    }
}