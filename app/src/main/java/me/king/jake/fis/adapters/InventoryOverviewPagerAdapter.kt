package me.king.jake.fis.adapters

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentStatePagerAdapter
import me.king.jake.fis.fragments.ItemOverviewFragment
import me.king.jake.fis.fragments.PropertiesOverviewFragment

class InventoryOverviewPagerAdapter(fragmentManager: FragmentManager) : FragmentStatePagerAdapter(fragmentManager, BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT) {
    override fun getItem(position: Int): Fragment {
        return when (position) {
            0 -> ItemOverviewFragment()
            1 -> PropertiesOverviewFragment()
            else -> throw Error("Invalid number for position")
        }
    }

    override fun getCount(): Int {
        return 2
    }
}