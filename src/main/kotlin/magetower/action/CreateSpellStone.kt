package magetower.action

import magetower.event.listChoice
import magetower.event.yesNoChoice
import magetower.spell.SpellStone
import magetower.TowerState
import magetower.action.CreateSpellStone.ChoiceState.*
import magetower.reagent.Reagent
import magetower.spell.Spell

class CreateSpellStone(var state: TowerState.TowerView) : Action {

    private enum class ChoiceState {
        CHOOSE_SPELL,CHOOSE_REAGENTS, CHOOSE_SPECIFIC_REAGENT, CONTINUE_SPECIFIC_REAGENT, INVEST, COMPLETE
    }

    private var choiceState = CHOOSE_SPELL
    private var fromSpell : Spell? = null
    private var currentReagentIdChoice : String? = null
    private var reagentsToUse : ArrayList<Reagent> = ArrayList()
    private var investment = 0


    override fun doAction(state: TowerState.TowerView): Action {
        return CreateSpellStone(state)
    }

    override fun description(): String {
        return "Create Spellstone"
    }

    override fun hasSteps(): Boolean {
        return choiceState != COMPLETE
    }

    override fun promptChoices(): Choice {
        return when(choiceState){

            CHOOSE_SPELL -> listChoice("From which spell?:", state.getResearchedSpells())
            CHOOSE_REAGENTS -> listChoice("Choose reagent type to use:",
                    getUnusedReagentList().map { "${it[0].name} (${it.size})" }.plus("Finish"))
            CHOOSE_SPECIFIC_REAGENT -> {
                    return listChoice("Choose reagent:", getUnusedReagentsWithId(currentReagentIdChoice!!))
            }
            CONTINUE_SPECIFIC_REAGENT -> yesNoChoice("Choose another one of these reagents?")
            INVEST -> Choice("Add gold", Choice.InputType.NUMBER)
            COMPLETE -> return Choice("", Choice.InputType.NONE)
        }
    }

    override fun processInput(input: ChoiceInput): ActionResult? {
        choiceState = when(choiceState){
            CHOOSE_SPELL -> {
                fromSpell = state.getResearchedSpells()[input.getNumber()]
                CHOOSE_REAGENTS
            }
            CHOOSE_REAGENTS ->
            {
                if(input.getNumber() >= getUnusedReagentList().size) {
                    INVEST
                } else {
                    currentReagentIdChoice = getUnusedReagentList()[input.getNumber()][0].id
                    CHOOSE_SPECIFIC_REAGENT
                }
            }
            CHOOSE_SPECIFIC_REAGENT -> {
                reagentsToUse.add(getUnusedReagentsWithId(currentReagentIdChoice!!)[input.getNumber()])
                if(getUnusedReagentsWithId(currentReagentIdChoice!!).isEmpty()) CHOOSE_REAGENTS else CONTINUE_SPECIFIC_REAGENT
            }
            CONTINUE_SPECIFIC_REAGENT -> if(input.getYesNo()) CHOOSE_SPECIFIC_REAGENT else CHOOSE_REAGENTS
            INVEST -> {
                investment = input.getNumber()
                COMPLETE
            }
            COMPLETE -> COMPLETE
        }
        if(choiceState == COMPLETE){
            return ActionResult("Created spellstone for spell " + fromSpell.toString())
                    .addStateChangeCallback { it.addSpellStone(SpellStone(fromSpell!!, reagentsToUse)) }
        }
        return null
    }

    private fun getUnusedReagentList() : List<List<Reagent>> {
        return state.getReagents().filter { getUnusedReagentsWithId(it[0].id).isNotEmpty() }
    }

    private fun getUnusedReagentsWithId (id : String) : List<Reagent> {
        return state.getReagents().find { it[0].id == id }!!.filter { !reagentsToUse.contains(it) }
    }
}