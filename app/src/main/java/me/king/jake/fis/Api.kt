package me.king.jake.fis

import com.github.kittinunf.fuel.core.extensions.jsonBody
import com.github.kittinunf.fuel.httpGet
import com.github.kittinunf.fuel.httpPost
import com.github.kittinunf.fuel.httpPut
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import me.king.jake.fis.models.*

object Api {
    private val BASE_URL = "https://apps.jakeking.co.uk/fims"
    private val gson = Gson()

    inline fun <reified T> Gson.fromJson(json: String) = this.fromJson<T>(json, object: TypeToken<T>() {}.type)

    fun sendBarcode(barcode: String, callback: (inventoryModel: InventoryDTO?) -> Unit) {
        val body = BarcodeDIO(barcode).toJSON()

        "$BASE_URL/api/barcode"
            .httpPost()
            .jsonBody(body)
            .responseString {
                _, _, (data, error) ->
                run {
                    var response = InventoryDTO(barcode)

                    if (error != null) {
                        if (error.response.statusCode != 404) {
                            throw error
                        }
                    } else if (data != null) {
                        response = InventoryDTO.fromJson(data)
                    }

                    callback(response)
                }
            }
    }

    fun postBaseItem(barcode: String, baseItem: ItemDTO, callback: (err: String?) -> Unit) {
        val body = BaseItemDIO(barcode, baseItem.title, baseItem.genericTitle).toJSON()

        "$BASE_URL/api/item"
            .httpPost()
            .jsonBody(body)
            .responseString {
                _, _, (_, error) ->
                run {
                    if (error != null) {
                        callback("Base Item error: ${error.message}")
                    } else {
                        callback(null)
                    }
                }
            }
    }

    fun postInventoryItem(barcode: String, inventoryItem: PropertiesDTO, callback: (err: String?) -> Unit) {
        val body = InventoryItemDIO(barcode, inventoryItem.quantity).toJSON()

        "$BASE_URL/api/inventory"
            .httpPost()
            .jsonBody(body)
            .responseString {
                _, _, (_, error) ->
                run {
                    if (error != null) {
                        callback("Inventory Item Error: ${error.message}")
                    } else {
                        callback(null)
                    }
                }
            }
    }

    fun getInventory(callback: (err: String?, inventory: ArrayList<InventoryDTO>?) -> Unit) {
        "$BASE_URL/api/inventory"
            .httpGet()
            .responseString {
                _, _, (payload, error) ->
                run {
                    if (error != null) {
                        callback("GET Inventory Error: ${error.message}", null)
                    } else {
                        val response = gson.fromJson<ArrayList<InventoryDTO>>(payload!!)
                        callback(null, response)
                    }
                }
            }
    }

    fun updateInventoryItem(barcode: String, inventoryItem: PropertiesDTO, callback: (err: String?) -> Unit) {
        val body = InventoryItemDIO(barcode, inventoryItem.quantity).toJSON()

        "$BASE_URL/api/inventory"
            .httpPut()
            .jsonBody(body)
            .responseString {
                    _, _, (_, error) ->
                run {
                    if (error != null) {
                        callback("Inventory Item Error: ${error.message}")
                    } else {
                        callback(null)
                    }
                }
            }
    }
}