package me.king.jake.fis.models

import com.google.gson.annotations.SerializedName
import com.google.gson.reflect.TypeToken

class ItemDTO {
    @SerializedName("title")
    var title: String = ""

    @SerializedName("genericTitle")
    var genericTitle: String? = null
        get() {
            return field ?: title
        }

    @SerializedName("attributes")
    var attributes: TypeToken<HashMap<String, String>>? = null
}