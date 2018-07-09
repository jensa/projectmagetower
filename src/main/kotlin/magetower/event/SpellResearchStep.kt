package magetower.event

import magetower.action.informPlayer
import magetower.event.SpellResearchStep.ChoiceState.*
import magetower.spell.SpellBuilder
import se.magetower.TowerState
import se.magetower.action.Action
import se.magetower.event.EventAction

class SpellResearchStep(var state : TowerState,
                        override var handleAfter : Long,
                        var stepLength : Long,
                        var spellBuilder : SpellBuilder,
                        var lastStep : SpellResearchStep?) : EventAction {

    private enum class ChoiceState {
        START,MIDDLE,FINISH, INVESTMENT, INVESTMENT_AMOUNT, COMPLETE
        //NONE, USABILITY, POWER, AREA, VERSATILITY, CAST_TIME, COOL
    }

    private var choiceState = START

    override fun doAction(state: TowerState): Action {
        return SpellResearchStep(state, handleAfter, stepLength, spellBuilder, lastStep)
    }

    override fun description(): String {
        return "Spell research - ${spellBuilder.name} - step ${getNumberOfSteps()}"
    }

    override fun hasSteps(): Boolean {
        return choiceState != COMPLETE
    }

    override fun promptChoices() {
        var stepNumber = getNumberOfSteps()
        var choice = StringBuilder("How much? (0-100) ")
        when(choiceState) {
            START -> when(stepNumber) {
                1 -> choice.append("Usability")
                2-> choice.append("Power")
            }
            MIDDLE -> when(stepNumber) {
                1 -> choice.append("Area")
                2-> choice.append("Versatility")
            }
            FINISH -> when(stepNumber) {
                1 -> choice.append("Cast time")
                2-> choice.append("Cool")
            }
            INVESTMENT -> {
                choice = StringBuilder("Would you like to invest more g?\n" +
                        listOf("Yes", "No").mapIndexed { i, s -> "$i. $s" }.joinToString("\n"))
            }
            INVESTMENT_AMOUNT -> choice = StringBuilder("How much? (Avaliable: ${state.g()} g)")
            COMPLETE -> return
        }
        informPlayer(choice.toString())
    }

    override fun processInput(inputList: List<String>) {
        val input = inputList[0].toIntOrNull()
        if(input == null || input < 0) {
            informPlayer("Invalid input")
            return
        }
        val stepNumber = getNumberOfSteps()
        when(choiceState) {
            START -> {
                when(stepNumber) {
                    1 -> spellBuilder.usability = input
                    2-> spellBuilder.power = input
                }
                choiceState = MIDDLE
            }
            MIDDLE -> {
                when(stepNumber) {
                    1 -> spellBuilder.area = input
                    2-> spellBuilder.versatility = input
                }
                choiceState = FINISH
            }
            FINISH -> {
                when(stepNumber) {
                    1 -> spellBuilder.castTime = input
                    2-> spellBuilder.cool = input
                }
                choiceState = INVESTMENT
            }
            INVESTMENT -> {
                choiceState = if(input == 0) INVESTMENT_AMOUNT else COMPLETE
            }
            INVESTMENT_AMOUNT -> {
                if(state.takeG(input)) {
                    spellBuilder.investments.add(input)
                    choiceState = COMPLETE
                } else {
                    informPlayer("Not enough g")
                }
            }
            COMPLETE -> return
        }
        if(choiceState == COMPLETE && stepNumber == 2) {
            state.spells.add(spellBuilder.build())
        }
    }

    private fun getAllSteps() : List<SpellResearchStep> {
        var steps = arrayListOf(this)
        var step = lastStep
        while (step != null) {
            steps.add(step)
            step = step.lastStep
        }
        return steps.reversed()
    }

    private fun getNumberOfSteps() : Int {
        return getAllSteps().size
    }

    override fun hasSideEffect() : Boolean{
        return getNumberOfSteps() < 2
    }

    override fun getSideEffect() : EventAction? {
        return SpellResearchStep(state, System.currentTimeMillis() + stepLength, stepLength, spellBuilder, this)
    }

}