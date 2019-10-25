package me.king.jake.fis.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import me.king.jake.fis.R
import me.king.jake.fis.views.ItemInfoInput
import me.king.jake.fis.views.PropertiesInput

class OverviewFragment: BaseOverviewFragment() {
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        super.onCreateView(inflater, container, savedInstanceState)
        return inflater.inflate(R.layout.fragment_overview, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        view.findViewById<ItemInfoInput>(R.id.item_info_wrapper).apply {
            populateFields(inventoryItem!!)
            editMode = false
        }

        view.findViewById<PropertiesInput>(R.id.item_properties_wrapper).apply {
            populateFields(inventoryItem!!)
            editMode = false
        }

        view.findViewById<TextView>(R.id.overview_description).text = String.format(
            resources.getString(R.string.overview_description),
            inventoryItem!!.item!!.title
        )
    }
}