package me.king.jake.fis.fragments

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import androidx.fragment.app.Fragment
import androidx.viewpager.widget.ViewPager
import com.tbuonomo.viewpagerdotsindicator.WormDotsIndicator
import me.king.jake.fis.R
import me.king.jake.fis.adapters.InventoryOverviewPagerAdapter

class InventoryOverviewFragment: Fragment() {
    private lateinit var overviewStepsPager: ViewPager
    private lateinit var overviewStepsIndicator: WormDotsIndicator
    private lateinit var overviewStepsAdapter: InventoryOverviewPagerAdapter

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
        overviewStepsIndicator = view.findViewById(R.id.overview_steps_indicator)

        setupViewPager()
    }

    private fun setupViewPager() {
        overviewStepsAdapter = InventoryOverviewPagerAdapter(fragmentManager!!)

        overviewStepsPager.adapter = overviewStepsAdapter
        overviewStepsIndicator.setViewPager(overviewStepsPager)
    }

    private fun closeKeyboard() {
        val view = activity!!.currentFocus

        if (view != null) {
            val imm = activity!!.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager?
            imm!!.hideSoftInputFromWindow(view.windowToken, 0)
        }
    }
}