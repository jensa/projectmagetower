package se.magetower.event

import se.magetower.action.Action

interface EventAction : Action {

    var handleAfter : Long

}