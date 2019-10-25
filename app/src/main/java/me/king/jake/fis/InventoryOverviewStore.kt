package me.king.jake.fis

import androidx.annotation.MainThread

object InventoryOverviewStore {
    var currentState = Variable(States.IDLE)

    enum class States {
        IDLE,
        NEXT_PAGE,
        FINISHED,
    }

    @MainThread
    fun setCurrentState(newState: States) {
        this.currentState.value = newState
    }
}