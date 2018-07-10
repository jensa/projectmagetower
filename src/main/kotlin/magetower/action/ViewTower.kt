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

    override fun promptChoices(): Choice {
        hasViewed = true
        return Choice("Your tower has:\n$state", Choice.InputType.NONE)
    }

    override fun processInput(input: ChoiceInput): ActionResult? {
        return null
    }

    override fun doAction(state: TowerState): Action {
        return ViewTower(state)
    }
}