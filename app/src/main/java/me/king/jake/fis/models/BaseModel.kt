package me.king.jake.fis.models

import com.google.gson.Gson

open class BaseModel {
    fun toJSON(): String {
        return Gson().toJson(this, this.javaClass)
    }
}