package magetower.action

import se.magetower.TowerState
import se.magetower.action.Action

class ViewTower(var state: TowerState) : Action {

    var hasViewed = false

    override fun description(): String {
        return "View tower"
    }

    override fun hasSteps(): Boolean {
        return !hasViewed
    }

    override fun promptChoices() {
        informPlayer("Your tower has:\n$state")
        hasViewed = true
    }

    override fun processInput(inputList: List<String>) {
    }

    override fun doAction(state: TowerState): Action {
        return ViewTower(state)
    }
}