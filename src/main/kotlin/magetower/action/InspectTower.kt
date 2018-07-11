package magetower.action

import magetower.TowerState

class InspectTower(var state: TowerState.TowerView) : Action {

    var hasViewed = false

    override fun description(): String {
        return "Inspect tower"
    }

    override fun hasSteps(): Boolean {
        return !hasViewed
    }

    override fun promptChoices(): Choice {
        hasViewed = true
        return Choice("Day : ${state.getDay()}. Your tower has:\n$state", Choice.InputType.NONE)
    }

    override fun processInput(input: ChoiceInput): ActionResult? {
        return null
    }

    override fun doAction(state: TowerState.TowerView): Action {
        return InspectTower(state)
    }
}