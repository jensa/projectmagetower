package magetower.event

import magetower.action.ActionResult
import magetower.action.Choice
import magetower.action.ChoiceInput
import magetower.event.SpellResearchStep.ChoiceState.*
import magetower.spell.SpellBuilder
import magetower.TowerState
import magetower.action.Action
import kotlin.collections.HashMap

class SpellResearchStep(var state : TowerState.TowerView,
                        override var handleAfter : Int,
                        var spellBuilder : SpellBuilder,
                        var lastStep : SpellResearchStep?) : EventAction {

    private enum class ChoiceState {
        FOCUS_AREAS, FOCUS_AREAS_SELECT, INVESTMENT, INVESTMENT_AMOUNT, COMPLETE
    }

    private var choiceState = INVESTMENT
    var focusAreas : Map<String, Int> = HashMap()
    var investmentMade = false

    override fun doAction(state: TowerState.TowerView): Action {
        val focus = if(lastStep == null) HashMap() else lastStep!!.focusAreas
        val researchStepResult = spellBuilder.investTime(spellBuilder.investments.last(), focus)
        return SpellResearchStep(state, handleAfter, researchStepResult, lastStep)
    }

    override fun description(): String {
        return "Spell research - ${spellBuilder.name} - step ${getNumberOfSteps()}"
    }

    override fun hasSteps(): Boolean {
        return choiceState != COMPLETE
    }

    override fun promptChoices(): Choice {
        when(choiceState) {
            INVESTMENT -> return yesNoChoice("Would you like to spend more researching ${spellBuilder.name}?")
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
            return ActionResult("You have completed research on ${spellBuilder.name}!")
                    .addStateChangeCallback {it.addSpell(spellBuilder.build(state.getAvaliableShopReagents()))}
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
        val length = spellBuilder.investments.last()
        return SpellResearchStep(state, state.getDay() + length, spellBuilder, this)
    }

}