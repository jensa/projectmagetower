package se.magetower.action

import magetower.action.Choice
import magetower.action.ChoiceInput
import magetower.action.Choice.InputType
import magetower.action.ActionResult
import magetower.event.SpellResearchStep
import magetower.event.listChoice
import magetower.event.yesNoChoice
import magetower.spell.NullMagic
import magetower.spell.SpellBuilder
import se.magetower.TowerState
import se.magetower.action.ResearchSpell.ChoiceState.*
import se.magetower.event.EventAction

class ResearchSpell(var state: TowerState) : Action {

    private enum class ChoiceState {
        NO_RESEARCH, NAME, BRANCH, INITIAL_INVESTMENT, COMPLETE, ABORT
    }
    private var choiceState = NO_RESEARCH
    var spellBuilder = SpellBuilder(NullMagic())

    override fun doAction(state: TowerState): Action {
        return ResearchSpell(state)
    }

    override fun description(): String {
        return "Research spell"
    }

    override fun hasSteps(): Boolean {
        return choiceState != ABORT && choiceState != COMPLETE
    }

    override fun promptChoices() : Choice {
        return when(choiceState) {
            NO_RESEARCH -> yesNoChoice("Would you like to research a new spell?")
            BRANCH -> listChoice("Choose branch of magic:", state.magentificCommunity.discoveredBranches.map { it.name })
            NAME -> Choice("Choose a name:", InputType.TEXT)
            INITIAL_INVESTMENT -> Choice("Choose time investment (days):", InputType.NUMBER)
            COMPLETE -> Choice("Research started on ${spellBuilder.name}", InputType.NONE)
            ABORT -> Choice("Research aborted", InputType.NONE)
        }
    }

    override fun processInput(input: ChoiceInput) : ActionResult? {
        when(choiceState) {
            NO_RESEARCH -> choiceState = if(input.getYesNo()) BRANCH else ABORT
            BRANCH -> {
                spellBuilder = SpellBuilder(state.magentificCommunity.discoveredBranches[input.getNumber()])
                choiceState = NAME
            }
            NAME -> {
                val name = input.getText()
                if(state.getSpells().find { it.name.equals(name)} != null){
                    return ActionResult("Spell already exists")
                } else {
                    spellBuilder.name = name
                    choiceState = INITIAL_INVESTMENT
                }
            }
            INITIAL_INVESTMENT -> {
                spellBuilder.investments.add(input.getNumber())
                choiceState = COMPLETE
            }
            COMPLETE -> return null
            ABORT -> return null
        }
        if(choiceState == COMPLETE) {
            return ActionResult("Research started on ${spellBuilder.name}")
        }
        return null
    }

    override fun hasSideEffect() : Boolean{
        return choiceState == COMPLETE
    }

    override fun getSideEffect() : EventAction? {
        /*
        research steps work like this:
        an initial time investment is made
        this corresponds to the time it takes until the next research step occurs.
        That step has a result, and an opportunity to further fine-tune the spell
        if no fine-tuning is done, the spell is completed
        if fine-tuning happens, it generates a new spellResearchStep with a
        deadline corresponding to the new time investment

         */
        val length = 1000L * spellBuilder.investments[0]
        return SpellResearchStep(state, System.currentTimeMillis() + length, length, spellBuilder, null)
    }
}