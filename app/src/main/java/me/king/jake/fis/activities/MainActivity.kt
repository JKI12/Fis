package me.king.jake.fis.activities

import android.os.Bundle
import androidx.viewpager.widget.ViewPager
import me.king.jake.fis.R
import me.king.jake.fis.adapters.MainPagerAdapter

class MainActivity : BaseActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val pager : ViewPager = findViewById(R.id.wrapper)

        pager.apply {
            adapter = MainPagerAdapter(supportFragmentManager)
            currentItem = 1
        }
    }
}
