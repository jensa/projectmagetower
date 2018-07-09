package se.magetower.action

import se.magetower.TowerState

class TakeContract(var state: TowerState) : Action {

    override fun doAction(state: TowerState): Action {
        return TakeContract(state)
    }

    override fun description(): String {
        return "Take Contract"
    }

    override fun hasSteps(): Boolean {
        return false
    }

    override fun printChoices() {
    }

    override fun processInput(input: Int) {
    }
}