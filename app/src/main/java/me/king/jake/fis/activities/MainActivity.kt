package me.king.jake.fis.activities

import android.os.Bundle
import android.util.Log
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.viewpager.widget.ViewPager
import me.king.jake.fis.R
import me.king.jake.fis.adapters.MainPagerAdapter
import me.king.jake.fis.camera.WorkflowModel
import java.util.*

class MainActivity : BaseActivity() {
    private val TAG = this.javaClass.canonicalName

    var workflowModel: WorkflowModel? = null
    private var currentWorkflowState: WorkflowModel.WorkflowState? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val pager : ViewPager = findViewById(R.id.wrapper)

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
                    Log.i(TAG, "Barcode: ${workflowModel!!.detectedBarcode.value?.barcode}")
                }
                else -> return@Observer
            }
        })
    }
}
