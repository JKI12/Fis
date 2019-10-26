package me.king.jake.fis.fragments

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import me.king.jake.fis.Api
import me.king.jake.fis.R
import me.king.jake.fis.adapters.QuickActionsAdapter
import me.king.jake.fis.models.QuickAction
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


    override fun onStart() {
        super.onStart()

        view!!.findViewById<ItemInfoInput>(R.id.item_info_wrapper).apply {
            populateFields(inventoryItem!!)
            editMode = false
        }

        view!!.findViewById<PropertiesInput>(R.id.item_properties_wrapper).apply {
            populateFields(inventoryItem!!)
            editMode = false
        }

        view!!.findViewById<TextView>(R.id.overview_description).text = String.format(
            resources.getString(R.string.overview_description),
            inventoryItem!!.item!!.title
        )

        setupQuickActions()
    }

    private fun setupQuickActions() {
        val recyclerView = view!!.findViewById<RecyclerView>(R.id.rv_quick_actions)
        val quickActionsAdapter = QuickActionsAdapter(getActions())

        recyclerView.apply {
            layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
            adapter = quickActionsAdapter
        }
    }

    private fun updateQuantity(byAmount: Int) {
        inventoryItem!!.properties!!.quantity += byAmount

        Api.updateInventoryItem(inventoryItem!!.barcode, inventoryItem!!.properties!!) {
            error -> run {
                if (error != null) {
                    Log.e(this.javaClass.canonicalName, error)
                } else {
                    activity!!.runOnUiThread {
                        view!!.findViewById<PropertiesInput>(R.id.item_properties_wrapper).apply {
                            populateFields(inventoryItem!!)
                            showSuccessSnackbar(true, R.string.success_prop_quantity)
                        }
                    }
                }
            }
        }
    }

    private fun getActions() : ArrayList<QuickAction> {
        val list = ArrayList<QuickAction>()
        list.apply {
            add(QuickAction("Increase Quantity by 1") { updateQuantity(+1) })
            add(QuickAction("Decrease Quantity by 1") { updateQuantity(-1) })
            add(QuickAction("Edit current item", null))
            add(QuickAction("Delete current item", null))
        }
        return list
    }
}