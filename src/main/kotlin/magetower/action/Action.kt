package se.magetower.action

import se.magetower.TowerState

interface Action {

    fun description() : String
    fun hasSteps(): Boolean
    fun printChoices()
    fun processInput(input: Int)
    fun doAction(state: TowerState) : Action

}