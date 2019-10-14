package me.king.jake.fis.adapters

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentStatePagerAdapter
import me.king.jake.fis.fragments.BarcodeScannerFragment
import me.king.jake.fis.fragments.InventoryFragment

class MainPagerAdapter(private val fragmentManager: FragmentManager) : FragmentStatePagerAdapter(fragmentManager, BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT) {
    override fun getItem(position: Int): Fragment {
        return when (position) {
            0 -> BarcodeScannerFragment()
            1 -> InventoryFragment()
            2 -> InventoryFragment()
            else -> throw Error("Invalid number for position")
        }
    }

    override fun getCount(): Int {
        return 3
    }
}