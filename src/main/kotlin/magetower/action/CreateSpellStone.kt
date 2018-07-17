package magetower.action

import magetower.event.listChoice
import magetower.event.yesNoChoice
import magetower.spell.SpellStone
import magetower.TowerState
import magetower.action.CreateSpellStone.ChoiceState.*
import magetower.event.listChoicePlusContinue
import magetower.event.listChoiceWithFinish
import magetower.reagent.Reagent
import magetower.spell.Spell
import magetower.staff.Employee

class CreateSpellStone(var state: TowerState.TowerView) : Action(this::class.toString()) {

    private enum class ChoiceState {
        CHOOSE_SPELL, CHOOSE_EMPLOYEES, CHOOSE_REAGENTS, CHOOSE_SPECIFIC_REAGENT,
        CONTINUE_SPECIFIC_REAGENT, INVEST, COMPLETE, UNFIT
    }

    private var choiceState = CHOOSE_SPELL
    private var fromSpell : Spell? = null
    private var currentReagentIdChoice : String? = null
    private var reagentsToUse : ArrayList<Reagent> = ArrayList()
    private var employees : ArrayList<Employee> = ArrayList()
    private var investment = 0


    override fun doAction(state: TowerState.TowerView): Action {
        return CreateSpellStone(state)
    }

    override fun description(): String {
        return "Create Spellstone"
    }

    override fun hasSteps(): Boolean {
        return choiceState != COMPLETE && choiceState != UNFIT
    }

    override fun promptChoices(): Choice {
        if(getUnusedEmployees().isEmpty() && employees.isEmpty()) {
            choiceState = UNFIT
        }
        if(getUnusedReagentList().isEmpty() && reagentsToUse.isEmpty()) {
            choiceState = UNFIT
        }
        return when(choiceState){
            CHOOSE_SPELL -> listChoice("From which spell?:", state.getResearchedSpells())
            CHOOSE_EMPLOYEES -> listChoicePlusContinue("Who should do it? :", getUnusedEmployees())
            CHOOSE_REAGENTS -> listChoiceWithFinish("Choose reagent type to use:",
                    getUnusedReagentList().map { "${it[0].name} (${it.size})" })
            CHOOSE_SPECIFIC_REAGENT -> {
                    return listChoice("Choose reagent:", getUnusedReagentsWithId(currentReagentIdChoice!!))
            }
            CONTINUE_SPECIFIC_REAGENT -> yesNoChoice("Choose another one of these reagents?")
            INVEST -> Choice("Add gold", Choice.InputType.NUMBER)
            COMPLETE -> return Choice("", Choice.InputType.NONE)
            UNFIT -> return Choice("Cannot create spellstones: no avaliable ${if(getUnusedEmployees().isEmpty()) "employees" else "reagents"}",
                    Choice.InputType.NONE)
        }
    }

    override fun processInput(input: ChoiceInput): ActionResult? {
        choiceState = when(choiceState){
            CHOOSE_SPELL -> {
                val spell = state.getResearchedSpells()[input.getNumber()]
                if(fulfillsRequirements(spell, state.getReagents())){
                    fromSpell = spell
                    CHOOSE_EMPLOYEES
                } else {
                    CHOOSE_SPELL
                    return ActionResult("You do not have enough reagents to create ${spell.name}")
                }
            }
            CHOOSE_EMPLOYEES ->
                if(input.getNumber() >= getUnusedEmployees().size) {
                    if(employees.isEmpty()){
                        CHOOSE_EMPLOYEES
                        return ActionResult("You must choose at least 1 employee to do it")
                    } else {
                        CHOOSE_REAGENTS
                    }
                } else {
                    employees.add(getUnusedEmployees()[input.getNumber()])
                    if(getUnusedEmployees().isEmpty()){
                        CHOOSE_REAGENTS
                    } else {
                        CHOOSE_EMPLOYEES
                    }
                }
            CHOOSE_REAGENTS -> {
                if(input.getNumber() >= getUnusedReagentList().size) {
                    if(fulfillsRequirements(fromSpell!!, reagentsToUse)){
                        INVEST
                    } else {
                        CHOOSE_REAGENTS
                        return ActionResult("Chosen reagents ${reagentsToUse.map { it.name }} are not enough")
                    }
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
                if(state.hasG(input.getNumber())){
                    investment = input.getNumber()
                    COMPLETE
                } else {
                    INVEST
                    return ActionResult("Not enough g. you have ${state.g()}")
                }
            }
            COMPLETE -> COMPLETE
            UNFIT -> UNFIT
        }
        if(choiceState == COMPLETE){
            return ActionResult("Created spellstone for spell " + fromSpell.toString())
                    .addStateChangeCallback {
                        reagentsToUse.forEach { reagent -> it.useReagent(reagent) }
                        it.takeG(investment)
                        it.addSpellStone(SpellStone(fromSpell!!, reagentsToUse, employees))
                    }
        }
        return null
    }

    private fun fulfillsRequirements(spell : Spell, reagents : List<Reagent>) : Boolean{
        return spell.getRequirements().none { req -> reagents.find { reagent -> req.reagentId == reagent.id } == null }
    }

    private fun getUnusedReagentList() : List<List<Reagent>> {
        return state.getReagentsByType().filter { getUnusedReagentsWithId(it[0].id).isNotEmpty() }
    }

    private fun getUnusedReagentsWithId (id : String) : List<Reagent> {
        return state.getReagentsByType().find { it[0].id == id }!!.filter { !reagentsToUse.contains(it) }
    }

    private fun getUnusedEmployees(): List<Employee> {
        return state.getAvaliableEmployees().filter { !employees.contains(it) }
    }
}