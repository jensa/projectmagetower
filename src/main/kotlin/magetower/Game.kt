package se.magetower

import magetower.action.ViewTower
import magetower.action.informPlayer
import se.magetower.action.*
import se.magetower.event.EventAction
import java.util.*
import kotlin.collections.ArrayList

class Game {

    var state = TowerState()
    var events: Queue<EventAction> = LinkedList<EventAction>()
    var possibleActions: ArrayList<Action> = ArrayList()
    var currentEvent: EventAction? = null

    fun loop() {
        while (true) {
            if (getActionableEvents().isNotEmpty()) {
                processAction(events.poll().doAction(state))
            } else {
                printChoices()
                val inputList = getInput()
                val input = inputList[0].toIntOrNull()
                if (input != null) {
                    if(input < 0 || input >= possibleActions.size) {
                        informPlayer("invalid input")
                        return
                    }
                    processAction(possibleActions[input].doAction(state))
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
        informPlayer(possibleActions.withIndex().map { (i, action) ->
            "$i. ${action.description()}"
        }.joinToString("\n"))
    }

    private fun processAction(action: Action) {
        informPlayer("Action: ${action.description()}")
        while (action.hasSteps()) {
            action.promptChoices()
            if(action.hasSteps()) {
                val input = getInput()
                if (!input.isEmpty()) {
                    action.processInput(input)
                } else {
                    informPlayer("invalid input")
                }
            }
        }
        if(action.hasSideEffect()) {
            events.offer(action.getSideEffect())
        }

    }

    private fun getInput(): List<String> {
        return readLine()!!.split(' ')
    }

    init {
        possibleActions = arrayListOf(
                BuyReagent(state),
                TakeContract(state),
                ResearchSpell(state),
                CreateReagent(state),
                ViewTower(state))
    }
}