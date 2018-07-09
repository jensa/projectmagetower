package se.magetower.action

import se.magetower.TowerState
import se.magetower.event.EventAction

interface Action {

    fun description() : String
    fun hasSteps(): Boolean
    fun promptChoices()
    fun processInput(inputList: List<String>)
    fun doAction(state: TowerState) : Action

    fun hasSideEffect() : Boolean{
        return false
    }

    fun getSideEffect() : EventAction? {
        return null
    }

}