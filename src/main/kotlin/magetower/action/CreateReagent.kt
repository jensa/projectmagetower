package magetower.action

import magetower.TowerState

class CreateReagent(var state: TowerState.TowerView) : Action {

    override fun doAction(state: TowerState.TowerView): Action {
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