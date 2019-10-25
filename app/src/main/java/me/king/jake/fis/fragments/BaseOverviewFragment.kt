package me.king.jake.fis.fragments

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import androidx.fragment.app.Fragment
import me.king.jake.fis.models.InventoryDTO

open class BaseOverviewFragment : Fragment() {
    protected var inventoryItem: InventoryDTO? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        inventoryItem = arguments?.getParcelable(InventoryDTO.parcelableName)
            ?: throw IllegalArgumentException("Must provide and inventory item to Overview Fragments")

        return super.onCreateView(inflater, container, savedInstanceState)
    }

    protected fun closeKeyboard() {
        val view = activity!!.currentFocus

        if (view != null) {
            val imm = activity!!.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager?
            imm!!.hideSoftInputFromWindow(view.windowToken, 0)
        }
    }
}