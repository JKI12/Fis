package me.king.jake.fis.fragments

import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import me.king.jake.fis.Api
import me.king.jake.fis.R
import me.king.jake.fis.activities.MainActivity
import me.king.jake.fis.adapters.InventoryListAdapter
import me.king.jake.fis.models.InventoryDTO
import me.king.jake.fis.workflows.WorkflowModel
import java.util.*
import kotlin.collections.ArrayList

class ViewInventoryFragment : Fragment() {
    private val TAG = this.javaClass.canonicalName

    private lateinit var inventoryListAdapter: InventoryListAdapter

    private var mainWorkflowModel: WorkflowModel? = null
    private var mainCurrentWorkflowState: WorkflowModel.WorkflowState? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        super.onCreateView(inflater, container, savedInstanceState)

        return inflater.inflate(R.layout.fragment_inventory, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupMainWorkflowModel()
        setupRecyclerView()
        fetchInventory()
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
                        showOverviewFragment(mainWorkflowModel!!.inventoryItem.value!!)
                    }, 250)
                }
                else -> return@Observer
            }
        })
    }

    private fun showOverviewFragment(inventoryItem: InventoryDTO) {
        val overviewFragment = InventoryOverviewFragment()
        overviewFragment.arguments = Bundle().apply {
            putParcelable(InventoryDTO.parcelableName, inventoryItem)
        }

        val transaction = fragmentManager?.beginTransaction()
        transaction?.setCustomAnimations(R.anim.slide_up, R.anim.slide_down, R.anim.slide_up, R.anim.slide_down)
        transaction?.addToBackStack(null)
        transaction?.add(R.id.main_wrapper, overviewFragment, "INVENTORY_OVERVIEW")?.commit()
    }

    private fun fetchInventory() {
        Api.getInventory { err, inventory -> run {
            if (err != null) {
                Log.e(TAG, err)
            } else {
                Log.i(TAG, "Fetched inventory, items count: ${inventory!!.size}")
                inventoryListAdapter.updateInventoryList(inventory)
            }
        }}
    }

    private fun setupRecyclerView() {
        val recyclerView : RecyclerView = view!!.findViewById(R.id.rv_inventory)
        inventoryListAdapter = InventoryListAdapter(ArrayList()) {
            showOverviewFragment(it)
        }

        recyclerView.apply {
            adapter = inventoryListAdapter
            setHasFixedSize(false)
            layoutManager = LinearLayoutManager(this@ViewInventoryFragment.context)
        }
    }
}