package me.king.jake.fis.views

import android.content.Context
import android.util.AttributeSet
import android.view.View
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.button.MaterialButton
import me.king.jake.fis.R
import me.king.jake.fis.adapters.AttributesListAdapter

class AttributesList(context: Context, attrs: AttributeSet) : LinearLayout(context, attrs) {
    private lateinit var recyclerView: RecyclerView
    private lateinit var addBtn: MaterialButton
    private lateinit var noAttributesText: TextView

    private var attributesAdapter = AttributesListAdapter(HashMap())

    var editMode: Boolean = true
        set(value) {
            field = value
            update()
        }

    init {
        inflate(context, R.layout.view_attributres_list, this)
    }

    override fun onFinishInflate() {
        super.onFinishInflate()

        recyclerView = findViewById(R.id.rv_attributes)
        addBtn = findViewById(R.id.btn_add_attribute)
        noAttributesText = findViewById(R.id.tv_no_attributes)

        setupRecyclerView()
    }

    private fun setupRecyclerView() {
        recyclerView.apply {
            layoutManager = LinearLayoutManager(context)
            setHasFixedSize(false)
            adapter = attributesAdapter
        }
    }

    fun updateAttributesList(newAttributesList: HashMap<String, String>) {
        attributesAdapter.updateAttributes(newAttributesList)

        if (newAttributesList.size > 0) {
            noAttributesText.visibility = View.GONE
        } else {
            noAttributesText.visibility = View.VISIBLE
        }
    }

    private fun update() {
        attributesAdapter.setEditMode(editMode)

        if (editMode) {
            addBtn.visibility = View.VISIBLE
        } else {
            addBtn.visibility = View.GONE
        }
    }
}