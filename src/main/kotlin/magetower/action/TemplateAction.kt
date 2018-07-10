package se.magetower.action

import magetower.action.ActionResult
import magetower.action.Choice
import magetower.action.Choice.InputType
import magetower.action.ChoiceInput
import se.magetower.TowerState

class TemplateAction(var state: TowerState) : Action {

    override fun doAction(state: TowerState): Action {
        return TemplateAction(state)
    }

    override fun description(): String {
        return "changeme"
    }

    override fun hasSteps(): Boolean {
        return false
    }

    override fun promptChoices(): Choice {
        return Choice("template", InputType.NONE)
    }

    override fun processInput(input: ChoiceInput): ActionResult? {
        return null
    }
}