package me.king.jake.fis.models

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

@Parcelize
class ItemDTO(
    @SerializedName("title")
    var title: String = "",
    @SerializedName("genericTitle")
    var genericTitle: String? = null
    //@SerializedName("attributes")
    //var attributes: TypeToken<HashMap<String, String>>? = null
) : Parcelable