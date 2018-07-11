package magetower.action

import magetower.action.Choice.InputType
import magetower.event.SpellResearchStep
import magetower.event.listChoice
import magetower.spell.NullMagic
import magetower.spell.SpellBuilder
import magetower.TowerState
import magetower.action.ResearchSpell.ChoiceState.*
import magetower.event.EventAction

class ResearchSpell(var state: TowerState.TowerView) : Action {

    private enum class ChoiceState {
        NAME, BRANCH, INITIAL_INVESTMENT, COMPLETE
    }
    private var choiceState = BRANCH
    var spellBuilder = SpellBuilder(NullMagic())

    override fun doAction(state: TowerState.TowerView): Action {
        return ResearchSpell(state)
    }

    override fun description(): String {
        return "Research spell"
    }

    override fun hasSteps(): Boolean {
        return choiceState != COMPLETE
    }

    override fun promptChoices() : Choice {
        return when(choiceState) {
            BRANCH -> listChoice("Choose branch of magic:", state.getDiscoveredMagicBranches().map { it.name })
            NAME -> Choice("Choose a name:", InputType.TEXT)
            INITIAL_INVESTMENT -> Choice("Choose time investment (days):", InputType.NUMBER)
            COMPLETE -> Choice("", InputType.NONE)
        }
    }

    override fun processInput(input: ChoiceInput) : ActionResult? {
        when(choiceState) {
            BRANCH -> {
                spellBuilder = SpellBuilder(state.getDiscoveredMagicBranches()[input.getNumber()])
                choiceState = NAME
            }
            NAME -> {
                val name = input.getText()
                if(state.getResearchedSpells().find { it.name.equals(name)} != null){
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
        }
        if(choiceState == COMPLETE) {
            return ActionResult("Research started on ${spellBuilder.name}. It will be finished in ${spellBuilder.investments.last()} days")
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
        val length = spellBuilder.investments[0]
        return SpellResearchStep(state, state.getDay() + length, spellBuilder, null)
    }
}