package me.king.jake.fis

import com.github.kittinunf.fuel.core.extensions.jsonBody
import com.github.kittinunf.fuel.httpPost
import me.king.jake.fis.models.BarcodeDIO
import me.king.jake.fis.models.InventoryDTO

object Api {
    private val BASE_URL = "https://apps.jakeking.co.uk/fims"

    fun sendBarcode(barcode: String, callback: (inventoryModel: InventoryDTO?) -> Unit) {
        val body = BarcodeDIO(barcode).toJSON()

        "$BASE_URL/api/barcode"
            .httpPost()
            .jsonBody(body)
            .responseString {
                _, _, (data, error) ->
                run {
                    var response: InventoryDTO? = null

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
}