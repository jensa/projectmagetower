package se.magetower.action

import magetower.action.informPlayer
import magetower.event.SpellResearchStep
import magetower.spell.SpellBuilder
import se.magetower.TowerState
import se.magetower.action.ResearchSpell.ChoiceState.*
import se.magetower.event.EventAction
import se.magetower.spell.Spell

class ResearchSpell(var state: TowerState) : Action {

    private enum class ChoiceState {
        NO_RESEARCH, NAME, BRANCH, INITIAL_INVESTMENT, COMPLETE, ABORT
    }
    private var choiceState = NO_RESEARCH
    var spellBuilder = SpellBuilder()

    override fun doAction(state: TowerState): Action {
        return ResearchSpell(state)
    }

    override fun description(): String {
        return "Research spell"
    }

    override fun hasSteps(): Boolean {
        return choiceState != ABORT && choiceState != COMPLETE
    }

    override fun promptChoices() {
        when(choiceState) {
            NO_RESEARCH -> informPlayer("Would you like to research a new spell?\n" +
                    listOf("Yes", "No").mapIndexed { i, s -> "$i. $s" }.joinToString("\n"))
            NAME -> informPlayer("Choose a name:\n")
            BRANCH -> informPlayer("Choose branch of magic:\n" +
                    state.magentificCommunity.discoveredBranches.mapIndexed { i, branch -> "$i. ${branch.name}" })
            INITIAL_INVESTMENT -> informPlayer("Choose initial investment amount:\n")
            COMPLETE -> informPlayer("Research started on ${spellBuilder.name}\n")
            ABORT -> return
        }
    }

    override fun processInput(inputList: List<String>) {
        when(choiceState) {
            NO_RESEARCH -> {
                val input = inputList[0].toIntOrNull()
                choiceState = if(input == 0) NAME else ABORT
            }
            NAME -> {
                val name = inputList.joinToString(" ")
                if(state.spells.find { it.name.equals(name)} != null){
                    informPlayer("Spell already exists")
                } else {
                    spellBuilder = SpellBuilder()
                    spellBuilder.name = inputList.joinToString(" ")
                    choiceState = BRANCH
                }
            }
            BRANCH -> {
                val input = inputList[0].toIntOrNull()
                if(input == null) {
                    choiceState = ABORT
                    return
                }
                spellBuilder.branch = state.magentificCommunity.discoveredBranches[input]
                choiceState = INITIAL_INVESTMENT
            }
            INITIAL_INVESTMENT -> {
                val input = inputList[0].toIntOrNull()
                if(input == null) {
                    choiceState = ABORT
                    return
                }
                if(state.takeG(input)){
                    spellBuilder.investments.add(input)
                    choiceState = COMPLETE
                } else {
                    informPlayer("Not enough g")
                }
            }
            COMPLETE -> return
            ABORT -> return
        }
        if(choiceState == COMPLETE) {
            informPlayer("Research started on ${spellBuilder.name}\n")
        }
    }

    override fun hasSideEffect() : Boolean{
        return choiceState == COMPLETE
    }

    override fun getSideEffect() : EventAction? {
        val length = 10L * 1000L
        return SpellResearchStep(state, System.currentTimeMillis() + length, length, spellBuilder, null)
    }
}