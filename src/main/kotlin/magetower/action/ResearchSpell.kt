package se.magetower.action

import se.magetower.TowerState

class ResearchSpell(var state: TowerState) : Action {

    override fun doAction(state: TowerState): Action {
        return ResearchSpell(state)
    }

    override fun description(): String {
        return "Research spell"
    }

    override fun hasSteps(): Boolean {
        return false
    }

    override fun printChoices() {
    }

    override fun processInput(input: Int) {
    }
}