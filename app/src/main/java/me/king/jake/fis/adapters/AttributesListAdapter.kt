package me.king.jake.fis.adapters

import android.text.InputType
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.textfield.TextInputEditText
import me.king.jake.fis.R
import me.king.jake.fis.Utils

class AttributesListAdapter(private var attributes: HashMap<String, String>, private var editMode: Boolean = false)
    : RecyclerView.Adapter<AttributesListAdapter.ViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.input_attribute, parent, false)
        return ViewHolder(view)
    }

    override fun getItemCount(): Int {
        return attributes.size
    }

    private fun determineInputType(string: String): Int {
        return if (Utils.isNumber(string)) {
            InputType.TYPE_CLASS_NUMBER
        } else {
            InputType.TYPE_CLASS_TEXT
        }
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val key = attributes.keys.toList()[position]
        val value = attributes.getValue(key)

        holder.apply {
            keyInput.setText(key)
            valueInput.setText(value)

            if (editMode) {
                Utils.enableTextView(keyInput)
                Utils.enableTextView(valueInput, determineInputType(value))
            } else {
                Utils.disableTextView(keyInput)
                Utils.disableTextView(valueInput)
            }
        }
    }

    fun updateAttributes(newAttributesList: HashMap<String, String>) {
        attributes = newAttributesList
        this.notifyDataSetChanged()
    }

    fun setEditMode(newEditMode: Boolean) {
        editMode = newEditMode
        this.notifyDataSetChanged()
    }

    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val keyInput: TextInputEditText = view.findViewById(R.id.input_attribute_key)
        val valueInput: TextInputEditText = view.findViewById(R.id.input_attribute_value)
    }
}