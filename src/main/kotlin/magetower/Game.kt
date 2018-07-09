package se.magetower

import com.beust.klaxon.Klaxon
import se.magetower.action.*
import se.magetower.event.Event
import java.util.*
import kotlin.collections.ArrayList

class Game {

    var state = TowerState()
    var events: Queue<Event> = LinkedList<Event>()
    var possibleActions: ArrayList<Action> = ArrayList()
    var currentEvent: Event? = null

    fun loop() {
        while (true) {
            printChoices()
            val input = getInput()
            if (input != null) {
                processAction(input)
            }
        }

        Klaxon()
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
        if (events.size > 0) {
            currentEvent = events.poll()
            println(currentEvent!!.text())
        } else {
            possibleActions.withIndex().forEach { (i, action) ->
                print("$i. ")
                print(action.description())
                println()
            }
        }
    }

    private fun processAction(actionIndex: Int) {
        var action = possibleActions[actionIndex].doAction(state)
        while (action.hasSteps()) {
            action.printChoices()
            val input = getInput()
            if (input != null) {
                action.processInput(input)
            } else {
                println("invalid input")
            }
        }

    }

    private fun getInput(): Int? {
        return readLine()!!.split(' ')[0].toIntOrNull()
    }

    init {
        possibleActions = arrayListOf(
                BuyReagent(state),
                TakeContract(state),
                ResearchSpell(state),
                CreateReagent(state))
    }
}