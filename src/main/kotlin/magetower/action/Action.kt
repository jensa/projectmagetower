package magetower.action

import magetower.TowerState
import magetower.event.EventAction

interface Action {

    fun description() : String
    fun hasSteps(): Boolean
    fun promptChoices() : Choice
    fun processInput(input: ChoiceInput) : ActionResult?
    fun doAction(state: TowerState.TowerView) : Action

    fun hasSideEffect() : Boolean{
        return false
    }

    fun getSideEffect() : EventAction? {
        return null
    }

}