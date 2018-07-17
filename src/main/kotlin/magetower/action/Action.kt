package magetower.action

import kotlinx.serialization.Serializable
import magetower.TowerState
import magetower.event.EventAction

@Serializable
abstract class Action(var id : String) {

    abstract fun description() : String
    abstract fun hasSteps(): Boolean
    abstract fun promptChoices() : Choice
    abstract fun processInput(input: ChoiceInput) : ActionResult?
    abstract fun doAction(state: TowerState.TowerView) : Action

    open fun hasSideEffect() : Boolean{
        return false
    }

    open fun getSideEffect() : EventAction? {
        return null
    }

}