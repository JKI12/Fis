package me.king.jake.fis.fragments

import android.content.Context
import android.os.Bundle
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.FrameLayout
import androidx.annotation.StringRes
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.fragment.app.Fragment
import com.google.android.material.snackbar.Snackbar
import me.king.jake.fis.R
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

    fun showSuccessSnackbar(shouldBeTop: Boolean = false, @StringRes stringResource: Int) {
        val mainWrapper = activity!!.findViewById<ConstraintLayout>(R.id.main_wrapper)
        val snackbar = Snackbar.make(mainWrapper, stringResource, Snackbar.LENGTH_SHORT)

        if (shouldBeTop) {
            val snackbarView = snackbar.view
            val snackbarParams = snackbarView.layoutParams as FrameLayout.LayoutParams
            snackbarParams.gravity = Gravity.TOP
            snackbarView.layoutParams = snackbarParams
        }

        snackbar.show()
    }
}