package me.king.jake.fis.fragments

import android.content.Context.INPUT_METHOD_SERVICE
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.LinearLayout
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.button.MaterialButton
import me.king.jake.fis.R
import me.king.jake.fis.activities.MainActivity
import me.king.jake.fis.views.ItemInfoInput
import me.king.jake.fis.views.PropertiesInput
import me.king.jake.fis.workflows.WorkflowModel
import java.util.*

class InventoryFragment : Fragment() {
    private var mainWorkflowModel: WorkflowModel? = null
    private var mainCurrentWorkflowState: WorkflowModel.WorkflowState? = null

    private lateinit var bottomSheet: LinearLayout
    private lateinit var bsBehavior: BottomSheetBehavior<LinearLayout>
    private lateinit var parent: MainActivity

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        super.onCreateView(inflater, container, savedInstanceState)

        parent = activity as MainActivity

        return inflater.inflate(R.layout.fragment_inventory, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        bottomSheet = view.findViewById(R.id.inventory_wrapper)
        bsBehavior = BottomSheetBehavior.from(bottomSheet)

        bsBehavior.addBottomSheetCallback(object : BottomSheetBehavior.BottomSheetCallback() {
            override fun onSlide(bottomSheet: View, slideOffset: Float) {}

            override fun onStateChanged(bottomSheet: View, newState: Int) {
                if (newState == BottomSheetBehavior.STATE_EXPANDED) {
                    populateBottomSheet()
                }
            }
        })

        bsBehavior.addBottomSheetCallback(bottomSheetDisableDrag)

        setupMainWorkflowModel()
    }

    private fun setupMainWorkflowModel() {
        mainWorkflowModel = (activity as MainActivity).workflowModel

        mainWorkflowModel!!.workflowState.observe(this, Observer { workflowState ->
            if (workflowState == null || Objects.equals(mainCurrentWorkflowState, workflowState)) {
                return@Observer
            }

            mainCurrentWorkflowState = workflowState

            when (workflowState) {
                WorkflowModel.WorkflowState.SEARCHED -> {
                    Handler().postDelayed({
                        bsBehavior.state = BottomSheetBehavior.STATE_EXPANDED
                    }, 500)
                }
                else -> {
                    bsBehavior.state = BottomSheetBehavior.STATE_COLLAPSED
                }
            }
        })
    }

    private fun populateBottomSheet() {
        val inventoryItem = mainWorkflowModel!!.inventoryItem.value ?: return

        val itemInfoInput = bottomSheet.findViewById<ItemInfoInput>(R.id.item_info_input)
        itemInfoInput.apply {
            populateFields(inventoryItem)
        }

        val propertiesInput = bottomSheet.findViewById<PropertiesInput>(R.id.properties_input)
        propertiesInput.populateFields(inventoryItem)

        bottomSheet.findViewById<MaterialButton>(R.id.submit).setOnClickListener {
            if (itemInfoInput.validate()) {
                Log.i(this.javaClass.canonicalName, "Adding item to inventory")
                reset()
            }
        }

        bottomSheet.findViewById<MaterialButton>(R.id.cancel).setOnClickListener {
            reset()
        }
    }

    private fun reset() {
        bsBehavior.state = BottomSheetBehavior.STATE_COLLAPSED
        clearInputs()
        closeKeyboard()
    }

    private fun clearInputs() {
        val itemInfoInput = bottomSheet.findViewById<ItemInfoInput>(R.id.item_info_input)
        itemInfoInput.clearInputs()

        val propertiesInput = bottomSheet.findViewById<PropertiesInput>(R.id.properties_input)
        propertiesInput.clearInputs()
    }

    private val bottomSheetDisableDrag = object : BottomSheetBehavior.BottomSheetCallback() {
        override fun onSlide(bottomSheet: View, slideOffset: Float) {}

        override fun onStateChanged(bottomSheet: View, newState: Int) {
            if (newState == BottomSheetBehavior.STATE_DRAGGING) {
                bsBehavior.state = BottomSheetBehavior.STATE_EXPANDED
            }
        }
    }

    private fun closeKeyboard() {
        val view = parent.currentFocus

        if (view != null) {
            val imm = parent.getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager?
            imm!!.hideSoftInputFromWindow(view.windowToken, 0)
        }
    }
}