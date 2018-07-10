package se.magetower.action

import magetower.action.ActionResult
import magetower.action.Choice
import magetower.action.ChoiceInput
import magetower.event.yesNoChoice
import se.magetower.TowerState

class TakeContract(var state: TowerState) : Action {

    private enum class ChoiceState {
        NO_CONTRACT, CHOOSE, VIEW_DETAILS, DETAIL, CHOOSE_SPELLS, ABORT
    }

    override fun doAction(state: TowerState): Action {
        return TakeContract(state)
    }

    override fun description(): String {
        return "Take Contract"
    }

    override fun hasSteps(): Boolean {
        return false
    }

    override fun promptChoices(): Choice {
        return yesNoChoice("Would you like to take a contract?")
    }

    override fun processInput(input: ChoiceInput): ActionResult? {
        return null
    }
}