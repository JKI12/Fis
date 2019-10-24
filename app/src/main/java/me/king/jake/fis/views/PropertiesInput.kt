package me.king.jake.fis.views

import android.content.Context
import android.text.InputType
import android.util.AttributeSet
import android.widget.LinearLayout
import com.google.android.material.textfield.TextInputEditText
import me.king.jake.fis.R
import me.king.jake.fis.models.InventoryDTO

class PropertiesInput(context: Context, attrs: AttributeSet?) : LinearLayout(context, attrs) {
    private lateinit var quantityInput: TextInputEditText

    init {
        inflate(context, R.layout.input_properties, this)
    }

    override fun onFinishInflate() {
        super.onFinishInflate()

        quantityInput = findViewById(R.id.input_quantity)
    }

    fun populateFields(inventoryItem: InventoryDTO) {
        quantityInput.apply {
            val quantity = inventoryItem.properties?.quantity ?: 1
            setText(quantity.toString())
        }
    }

    fun clearInputs() {
        quantityInput.apply {
            text = null
            inputType = InputType.TYPE_CLASS_NUMBER
        }
    }
}