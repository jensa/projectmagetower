package se.magetower.action

import magetower.action.ActionResult
import magetower.action.Choice
import magetower.action.ChoiceInput
import se.magetower.TowerState

class CreateReagent(var state: TowerState) : Action {

    override fun doAction(state: TowerState): Action {
        return CreateReagent(state)
    }

    override fun description(): String {
        return "Create reagent"
    }

    override fun hasSteps(): Boolean {
        return false
    }

    override fun promptChoices(): Choice {
        return Choice("template", Choice.InputType.NONE)
    }

    override fun processInput(input: ChoiceInput): ActionResult? {
        return null
    }
}