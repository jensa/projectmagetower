package se.magetower

import magetower.action.Choice
import magetower.action.ChoiceInput
import magetower.action.ViewTower
import magetower.action.informPlayer
import se.magetower.action.*
import se.magetower.event.EventAction
import java.util.*
import kotlin.collections.ArrayList

class Game {

    var state = TowerState()
    var events: Queue<EventAction> = LinkedList<EventAction>()

    fun loop() {
        while (true) {
            if (getActionableEvents().isNotEmpty()) {
                processAction(events.poll().doAction(state))
            } else {
                printChoices()
                val input = ChoiceInput(getInput())
                if (input.input.isNotBlank()) {
                    if(input.getNumber() < 0 || input.getNumber() >= state.possibleActions.size) {
                        informPlayer("invalid input")
                        return
                    }
                    processAction(state.possibleActions[input.getNumber()].doAction(state))
                }
            }
        }
    }

    private fun getActionableEvents() : List<EventAction> {
        return events.filter { it.handleAfter < System.currentTimeMillis() }
    }

    private fun printChoices() {
        /*
        Startup phase:
        Fresh out of college
        Start the tower in a rented basement
        Have like, 2-3 spells? require reagents
        take small contracts,
        buy reagents for spells
        complete the contracts
        try to make profit
        research new spells & alchemy to create more reagents
         */
        informPlayer(state.possibleActions.withIndex().map { (i, action) ->
            "$i. ${action.description()}"
        }.joinToString("\n"))
    }

    private fun processAction(action: Action) {
        informPlayer("Action: ${action.description()}")
        while (action.hasSteps()) {
            val choice = action.promptChoices()
            informPlayer(choice.text)
            if(choice.inputType != Choice.InputType.NONE) {
                val input = getInput()
                if (!input.isEmpty()) {
                    val actionResult = action.processInput(ChoiceInput(input))
                    if(actionResult != null) {
                        informPlayer(actionResult.text)
                    }
                } else {
                    informPlayer("invalid input")
                }
            }
        }
        if(action.hasSideEffect()) {
            events.offer(action.getSideEffect())
        }

    }

    private fun getInput(): String {
        return readLine()!!
    }
}