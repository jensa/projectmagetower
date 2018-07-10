package magetower.event

import magetower.action.ActionResult
import magetower.action.Choice
import magetower.action.ChoiceInput
import magetower.event.SpellResearchStep.ChoiceState.*
import magetower.spell.SpellBuilder
import se.magetower.TowerState
import se.magetower.action.Action
import se.magetower.event.EventAction
import kotlin.collections.HashMap

class SpellResearchStep(var state : TowerState,
                        override var handleAfter : Long,
                        var stepLength : Long,
                        var spellBuilder : SpellBuilder,
                        var lastStep : SpellResearchStep?) : EventAction {

    private enum class ChoiceState {
        FOCUS_AREAS, FOCUS_AREAS_SELECT, INVESTMENT, INVESTMENT_AMOUNT, COMPLETE
    }

    private var choiceState = INVESTMENT
    var focusAreas : Map<String, Int> = HashMap()
    var investmentMade = false

    override fun doAction(state: TowerState): Action {
        val focus = if(lastStep == null) HashMap() else lastStep!!.focusAreas
        val researchStepResult = spellBuilder.investTime(spellBuilder.investments.last(), focus)
        return SpellResearchStep(state, handleAfter, stepLength, researchStepResult, lastStep)
    }

    override fun description(): String {
        return "Spell research - ${spellBuilder.name} - step ${getNumberOfSteps()}"
    }

    override fun hasSteps(): Boolean {
        return choiceState != COMPLETE
    }

    override fun promptChoices(): Choice {
        when(choiceState) {
            INVESTMENT -> return yesNoChoice("Would you like to spend more time?")
            INVESTMENT_AMOUNT -> return Choice("How many more days?", Choice.InputType.NUMBER)
            FOCUS_AREAS -> return yesNoChoice("Any focus areas?")
            FOCUS_AREAS_SELECT -> return listChoiceText("Which focus areas?", spellBuilder.properties.map { it.first })
            COMPLETE -> return Choice("", Choice.InputType.NONE)
        }
    }

    override fun processInput(input: ChoiceInput): ActionResult? {
        when(choiceState) {
            INVESTMENT -> choiceState = if(input.getYesNo()) INVESTMENT_AMOUNT else COMPLETE
            INVESTMENT_AMOUNT -> {
                spellBuilder.investments.add(input.getNumber())
                investmentMade = true
                choiceState = FOCUS_AREAS
            }
            FOCUS_AREAS -> choiceState = if(input.getYesNo()) FOCUS_AREAS_SELECT else COMPLETE
            FOCUS_AREAS_SELECT -> {
                val indexList = input.getNumberList()
                focusAreas = spellBuilder.properties.filterIndexed { i, _ -> indexList.contains(i) }.toMap()
                choiceState = COMPLETE
            }
            COMPLETE -> return null
        }
        if(choiceState == COMPLETE && !investmentMade) {
            state.addSpell(spellBuilder.build(state.reagentShop.avaliableReagents))
            return ActionResult("You have completed research on ${spellBuilder.name}!")
        }
        return null
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
        return investmentMade
    }

    override fun getSideEffect() : EventAction? {
        var length = spellBuilder.investments.last() * 1000
        return SpellResearchStep(state, System.currentTimeMillis() + length, stepLength, spellBuilder, this)
    }

}