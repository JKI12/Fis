package me.king.jake.fis.views

import android.content.Context
import android.text.InputType
import android.util.AttributeSet
import android.widget.LinearLayout
import com.google.android.material.textfield.TextInputEditText
import me.king.jake.fis.R
import me.king.jake.fis.Utils.disableTextView
import me.king.jake.fis.Utils.enableTextView
import me.king.jake.fis.models.InventoryDTO
import me.king.jake.fis.models.PropertiesDTO

class PropertiesInput(context: Context, attrs: AttributeSet?) : LinearLayout(context, attrs) {
    private lateinit var quantityInput: TextInputEditText

    var editMode: Boolean = true
        set(value) {
            field = value
            update()
        }

    init {
        inflate(context, R.layout.input_properties, this)
    }

    override fun onFinishInflate() {
        super.onFinishInflate()

        quantityInput = findViewById(R.id.input_quantity)
        update()
    }

    fun populateFields(inventoryItem: InventoryDTO) {
        quantityInput.apply {
            val quantity = inventoryItem.properties?.quantity ?: 1
            setText(quantity.toString())
        }
    }

    fun getItemOutput(): PropertiesDTO {
        return PropertiesDTO().apply {
            quantity = quantityInput.text.toString().toInt(10)
        }
    }

    fun validate(): Boolean {
        return true
    }

    private fun update() {
        if (editMode) {
            enableTextView(quantityInput, InputType.TYPE_CLASS_NUMBER)
        } else {
            disableTextView(quantityInput)
        }
    }
}