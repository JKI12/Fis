package me.king.jake.fis.adapters

import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentStatePagerAdapter
import me.king.jake.fis.Constants
import me.king.jake.fis.fragments.ItemInfoOverviewFragment
import me.king.jake.fis.fragments.PropertiesOverviewFragment
import me.king.jake.fis.models.InventoryDTO

class InventoryOverviewPagerAdapter(fragmentManager: FragmentManager, private val steps: ArrayList<Constants.OVERVIEW_STEPS>, private val inventoryItem: InventoryDTO)
    : FragmentStatePagerAdapter(fragmentManager, BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT) {
    override fun getItem(position: Int): Fragment {
        val fragment = when (steps[position]) {
            Constants.OVERVIEW_STEPS.ADD_ITEM_INFO -> ItemInfoOverviewFragment()
            Constants.OVERVIEW_STEPS.ADD_PROPERTIES_INFO -> PropertiesOverviewFragment()
        }

        fragment.apply {
            arguments = Bundle().apply {
                putParcelable(InventoryDTO.parcelableName, inventoryItem)
            }
        }

        return fragment
    }

    override fun getCount(): Int {
        return steps.size
    }
}