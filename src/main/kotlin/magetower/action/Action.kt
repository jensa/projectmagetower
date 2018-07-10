package se.magetower.action

import magetower.action.Choice
import magetower.action.ChoiceInput
import magetower.action.ActionResult
import se.magetower.TowerState
import se.magetower.event.EventAction

interface Action {

    fun description() : String
    fun hasSteps(): Boolean
    fun promptChoices() : Choice
    fun processInput(input: ChoiceInput) : ActionResult?
    fun doAction(state: TowerState) : Action

    fun hasSideEffect() : Boolean{
        return false
    }

    fun getSideEffect() : EventAction? {
        return null
    }

}