package se.magetower.action

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

    override fun printChoices() {
    }

    override fun processInput(input: Int) {
    }
}