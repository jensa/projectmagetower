package magetower.action

import magetower.action.Choice.InputType
import magetower.event.SpellResearchStep
import magetower.event.listChoice
import magetower.spell.NullMagic
import magetower.spell.SpellBuilder
import magetower.TowerState
import magetower.event.EventAction
import kotlinx.serialization.Serializable

@Serializable
class ResearchSpell(var state: TowerState.TowerView) : Action(this::class.toString()) {

    private enum class ChoiceState {
        NAME, BRANCH, INITIAL_INVESTMENT, COMPLETE
    }

    private var choiceState = ChoiceState.BRANCH
    var spellBuilder = SpellBuilder(NullMagic())

    override fun doAction(state: TowerState.TowerView): Action {
        return ResearchSpell(state)
    }

    override fun description(): String {
        return "Research spell"
    }

    override fun hasSteps(): Boolean {
        return choiceState != ChoiceState.COMPLETE
    }

    override fun promptChoices() : Choice {
        return when(choiceState) {
            ChoiceState.BRANCH -> listChoice("Choose branch of magic:", state.getDiscoveredMagicBranches().map { it.name })
            ChoiceState.NAME -> Choice("Choose a name:", InputType.TEXT)
            ChoiceState.INITIAL_INVESTMENT -> Choice("Choose time investment (days):", InputType.NUMBER)
            ChoiceState.COMPLETE -> Choice("", InputType.NONE)
        }
    }

    override fun processInput(input: ChoiceInput) : ActionResult? {
        when(choiceState) {
            ChoiceState.BRANCH -> {
                spellBuilder = SpellBuilder(state.getDiscoveredMagicBranches()[input.getNumber()])
                choiceState = ChoiceState.NAME
            }
            ChoiceState.NAME -> {
                val name = input.getText()
                if(state.getResearchedSpells().find { it.name.equals(name)} != null){
                    return ActionResult("Spell already exists")
                } else {
                    spellBuilder.name = name
                    choiceState = ChoiceState.INITIAL_INVESTMENT
                }
            }
            ChoiceState.INITIAL_INVESTMENT -> {
                spellBuilder.investments.add(input.getNumber())
                choiceState = ChoiceState.COMPLETE
            }
            ChoiceState.COMPLETE -> return null
        }
        if(choiceState == ChoiceState.COMPLETE) {
            return ActionResult("Research started on ${spellBuilder.name}. It will be finished in ${spellBuilder.investments.last()} days")
        }
        return null
    }

    override fun hasSideEffect() : Boolean{
        return choiceState == ChoiceState.COMPLETE
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