package magetower.event

import magetower.action.Action

abstract class EventAction(id : String) : Action(id) {

    abstract var handleAfter : Int

}