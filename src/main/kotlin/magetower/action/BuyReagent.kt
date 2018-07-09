package se.magetower.action

import se.magetower.TowerState

class BuyReagent(var state: TowerState) : Action {

    override fun doAction(state: TowerState): Action {
        return BuyReagent(state)
    }

    override fun description(): String {
        return "Buy reagents"
    }

    override fun hasSteps(): Boolean {
        return false
    }

    override fun printChoices() {

    }

    override fun processInput(input: Int) {
    }
}