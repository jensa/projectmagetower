package se.magetower.action

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

    override fun promptChoices() {
    }

    override fun processInput(inputList: List<String>) {
    }
}