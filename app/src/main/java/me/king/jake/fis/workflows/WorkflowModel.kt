package me.king.jake.fis.workflows

import android.app.Application
import androidx.annotation.MainThread
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import me.king.jake.fis.models.InventoryDTO

class WorkflowModel(application: Application) : AndroidViewModel(application) {
    val workflowState = MutableLiveData<WorkflowState>()
    val inventoryItem = MutableLiveData<InventoryDTO?>()

    var isCameraLive = false
        private set

    enum class WorkflowState {
        NOT_STARTED,
        DETECTING,
        DETECTED,
        CONFIRMING,
        SEARCHING,
        SEARCHED
    }

    @MainThread
    fun setWorkflowState(workflowState: WorkflowState) {
        this.workflowState.value = workflowState
    }

    fun markCameraLive() {
        isCameraLive = true
    }

    fun markCameraFrozen() {
        isCameraLive = false
    }
}