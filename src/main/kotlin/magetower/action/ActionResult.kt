package magetower.action

import magetower.TowerState

class ActionResult(var text : String) {

    var stateChangeCallback : ((TowerState.ChangeTower) -> Unit)? = null

    fun addStateChangeCallback(stateChangeCallback : ((TowerState.ChangeTower) -> Unit)) : ActionResult {
        this.stateChangeCallback = stateChangeCallback
        return this
    }
}