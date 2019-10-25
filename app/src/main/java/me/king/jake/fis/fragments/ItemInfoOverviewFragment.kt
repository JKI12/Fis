package me.king.jake.fis.fragments

import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.google.android.material.button.MaterialButton
import me.king.jake.fis.Api
import me.king.jake.fis.InventoryOverviewStore
import me.king.jake.fis.R
import me.king.jake.fis.views.ItemInfoInput

class ItemInfoOverviewFragment: BaseOverviewFragment() {
    private lateinit var itemInfoInput: ItemInfoInput

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        super.onCreateView(inflater, container, savedInstanceState)
        return inflater.inflate(R.layout.fragment_item_info, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        itemInfoInput = view.findViewById(R.id.item_info_wrapper)
        itemInfoInput.populateFields(inventoryItem!!)

        view.findViewById<MaterialButton>(R.id.btn_overview_submit).setOnClickListener {
            submit()
        }
    }

    private fun submit() {
        if (!itemInfoInput.validate()) {
            return
        }

        closeKeyboard()

        Api.postBaseItem(inventoryItem!!.barcode, itemInfoInput.getItemOutput()) { inventoryItemError ->
            run {
                if (inventoryItemError != null) {
                    Log.e(this.javaClass.canonicalName, inventoryItemError)
                    return@run
                }


                Handler(context!!.mainLooper).post {
                    InventoryOverviewStore.setCurrentState(InventoryOverviewStore.States.NEXT_PAGE)
                }
            }
        }
    }
}