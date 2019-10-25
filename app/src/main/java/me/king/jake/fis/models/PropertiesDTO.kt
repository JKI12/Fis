package me.king.jake.fis.models

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

@Parcelize
class PropertiesDTO(
    @SerializedName("quantity")
    var quantity: Int = 1
//    @SerializedName("attributes")
//    val attributes: TypeToken<HashMap<String, String>>? = null
) : Parcelable