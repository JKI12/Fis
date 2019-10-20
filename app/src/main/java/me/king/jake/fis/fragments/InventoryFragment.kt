package me.king.jake.fis.fragments

import android.os.Bundle
import android.os.Handler
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.floatingactionbutton.FloatingActionButton
import me.king.jake.fis.R
import me.king.jake.fis.activities.MainActivity
import me.king.jake.fis.camera.WorkflowModel
import java.util.*

class InventoryFragment : Fragment() {
    private var workflowModel: WorkflowModel? = null
    private var currentWorkflowState: WorkflowModel.WorkflowState? = null
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

        val bottomSheet = view.findViewById<LinearLayout>(R.id.inventory_wrapper)
        bsBehavior = BottomSheetBehavior.from(bottomSheet)

        setupWorkflowModel()
    }

    private fun setupWorkflowModel() {
        workflowModel = (activity as MainActivity).workflowModel

        workflowModel!!.workflowState.observe(this, Observer { workflowState ->
            if (workflowState == null || Objects.equals(currentWorkflowState, workflowState)) {
                return@Observer
            }

            currentWorkflowState = workflowState

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


}