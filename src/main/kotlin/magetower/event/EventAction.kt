package magetower.event

import magetower.action.Action

interface EventAction : Action {

    var handleAfter : Int

}