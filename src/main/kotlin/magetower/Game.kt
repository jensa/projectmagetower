package magetower

import magetower.action.Action
import magetower.action.Choice
import magetower.action.ChoiceInput
import magetower.action.informPlayer
import magetower.event.EventAction
import java.util.*

class Game(var state : TowerState) {
    var events: Queue<EventAction> = LinkedList<EventAction>()

    fun loop() {
        while (true) {
            if (getActionableEvents().isNotEmpty()) {
                processAction(events.poll().doAction(state.viewTower))
            } else {
                printChoices()
                val input = ChoiceInput(getInput())
                if (input.input.isNotBlank()) {
                    if(input.getNumber() < 0 || input.getNumber() >= state.possibleActions.size) {
                        informPlayer("invalid input")
                    } else {
                        processAction(state.getPossibleActions()[input.getNumber()].doAction(state.viewTower))
                    }
                }
            }
        }
    }

    private fun getActionableEvents() : List<EventAction> {
        return events.filter { it.handleAfter < state.viewTower.getDay() }
    }

    private fun printChoices() {
        /*
        Startup phase:
        Fresh out of college
        Start the tower in a rented basement

        School has copyright on all the spells you used in school :(
        so you have to research spells in one of the magic branches you learned in school
        take small contracts,
        buy reagents for spells
        complete the contracts
        try to make profit
        research new spells & alchemy to create more reagents

         moving to next phase means moving to a brand new office/tower
         */

        /*
        middle phase:
        recruit more people
        Mages have different needs/wants: some want to "experience a lot of things" (mostly junior)
        some want to only stay researching and not really talking
        some want more influence over prod dev. and UX
        some wants management
        finance functions:
            Set budget for depts
            take out loans from bank
            Gold alchemy (high risk/reward, desperate gambling)
        research functions:
            Develop new magic branches
            producing even 1 new is a major task
        Product development:
            Develop new spells
            goes faster, can iterate more often based on "feedback"
            scryers -> UX functions?

        Engineering/Alchemy?? same as product?
            New ways to create reagents
            Effectivize ways of gathering raw materials for reagents
            Improve "infrastructure": storage of reagents, "storage" of spells (better paper for spellbooks??)

        marketing/commercial
            Enchant "banners"/signposts
            Lobby politicians/warlords to get new classes of contracts
            Get approvals/certifications for new types of contracts
            create branding - specialize in certain branches of magic?
            Negotiate each contract payment

        Management/HR
            Negotiate salaries
            Build new departements/expand office/tower
            Set focus areas - specialize in certain branches of magic?
            Professional development of employees
            Workplace culture - improve conditions, buy perks - coffee machine etc. (with magic)
            handle conflicts between depts.

        operations - the actual people going out to do the contracts?
            At start these are the same employees who develop spells/research
            Later on can recruit/"upgrade/professional dev" employees who only do ops or only dev
            then this becomes their own dept.

         */
        informPlayer("Day : ${state.viewTower.getDay()}\n" + state.getPossibleActions().withIndex().map { (i, action) ->
            "$i. ${action.description()}"
        }.joinToString("\n"))
    }

    private fun processAction(action: Action) {
        state.changeTower.time().stopTime()
        var keepGoing = true
        while (action.hasSteps() && keepGoing) {
            val choice = action.promptChoices()
            informPlayer(choice.text)
            if(choice.inputType != Choice.InputType.NONE) {
                val input = getInput()
                if(input == "q"){
                    informPlayer("aborting!")
                    keepGoing = false
                }
                else if (!input.isEmpty()) {
                    val actionResult = action.processInput(ChoiceInput(input))
                    if(actionResult != null) {
                        if(actionResult.stateChangeCallback != null) {
                            actionResult.stateChangeCallback?.invoke(state.changeTower)
                        }
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
        state.changeTower.time().startTime()

    }

    private fun getInput(): String {
        return readLine()!!
    }
}