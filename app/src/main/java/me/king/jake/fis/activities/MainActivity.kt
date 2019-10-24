package me.king.jake.fis.activities

import android.os.Bundle
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.viewpager.widget.ViewPager
import me.king.jake.fis.R
import me.king.jake.fis.adapters.MainPagerAdapter
import me.king.jake.fis.workflows.WorkflowModel
import java.util.*

class MainActivity : BaseActivity() {
    var workflowModel: WorkflowModel? = null
    private var currentWorkflowState: WorkflowModel.WorkflowState? = null

    private lateinit var pager : ViewPager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        pager = findViewById(R.id.content_wrapper)

        pager.apply {
            adapter = MainPagerAdapter(supportFragmentManager)
            currentItem = 1
        }

        setupWorkflowModel()
    }

    private fun setupWorkflowModel() {
        workflowModel = ViewModelProviders.of(this).get(WorkflowModel::class.java)

        workflowModel!!.workflowState.observe(this, Observer { workflowState ->
            if (workflowState == null || Objects.equals(currentWorkflowState, workflowState)) {
                return@Observer
            }

            currentWorkflowState = workflowState

            when (workflowState) {
                WorkflowModel.WorkflowState.SEARCHED -> {
                    changePage(1)
                }
                else -> return@Observer
            }
        })
    }

    private fun changePage(id: Int) {
        pager.setCurrentItem(id, true)
    }

    override fun onBackPressed() {
        when {
            supportFragmentManager.backStackEntryCount > 0 -> supportFragmentManager.popBackStack()
            pager.currentItem != 1 -> changePage(1)
            else -> super.onBackPressed()
        }
    }
}
