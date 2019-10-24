package me.king.jake.fis.models

import com.google.gson.annotations.SerializedName
import com.google.gson.reflect.TypeToken

class PropertiesDTO {
    @SerializedName("quantity")
    val quantity: Int = 1

    @SerializedName("attributes")
    val attributes: TypeToken<HashMap<String, String>>? = null
}