package me.king.jake.fis.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.viewpager.widget.ViewPager
import io.reactivex.disposables.Disposable
import me.king.jake.fis.Constants
import me.king.jake.fis.InventoryOverviewStore
import me.king.jake.fis.R
import me.king.jake.fis.adapters.InventoryOverviewPagerAdapter

class InventoryOverviewFragment: BaseOverviewFragment() {
    private lateinit var overviewStepsPager: ViewPager
    private lateinit var overviewStepsAdapter: InventoryOverviewPagerAdapter
    private lateinit var currentStateDisposable: Disposable

    private var itemIsComplete = false

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        super.onCreateView(inflater, container, savedInstanceState)

        return inflater.inflate(R.layout.fragment_inventory_overview, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        overviewStepsPager = view.findViewById(R.id.overview_steps)
        itemIsComplete = inventoryItem!!.item != null && inventoryItem!!.properties != null

        setupViewPager()
        setupTitle()
        listenToStoreEvents()
    }

    private fun setupViewPager() {
        val steps = ArrayList<Constants.OverviewSteps>()

        if (inventoryItem!!.item == null) {
            steps.add(Constants.OverviewSteps.ADD_ITEM_INFO)
        }

        if (inventoryItem!!.properties == null) {
            steps.add(Constants.OverviewSteps.ADD_PROPERTIES_INFO)
        }

        if (itemIsComplete) {
            steps.add(Constants.OverviewSteps.OVERVIEW)
        }

        overviewStepsAdapter = InventoryOverviewPagerAdapter(fragmentManager!!, steps, inventoryItem!!)

        overviewStepsPager.apply {
            adapter = overviewStepsAdapter
            beginFakeDrag()
        }
    }

    private fun setupTitle() {
        view!!.findViewById<TextView>(R.id.overview_title).text = when {
            itemIsComplete -> resources.getString(R.string.overview_overview)
            else -> resources.getString(R.string.overview_add_new)
        }
    }

    private fun nextPage() {
        val nextPage = overviewStepsPager.currentItem + 1

        if (nextPage > overviewStepsAdapter.count) {
            return
        }

        overviewStepsPager.setCurrentItem(nextPage, true)
        InventoryOverviewStore.setCurrentState(InventoryOverviewStore.States.IDLE)
    }

    private fun closeInventoryOverview() {
        fragmentManager!!.popBackStack()
        InventoryOverviewStore.setCurrentState(InventoryOverviewStore.States.IDLE)
    }

    private fun listenToStoreEvents() {
        currentStateDisposable = InventoryOverviewStore.currentState.observable.subscribe {
            state -> when(state) {
                InventoryOverviewStore.States.NEXT_PAGE -> nextPage()
                InventoryOverviewStore.States.FINISHED -> {
                    showSuccessSnackbar(stringResource = R.string.success_inventory)
                    closeInventoryOverview()
                }
                else -> return@subscribe
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        currentStateDisposable.dispose()
    }
}