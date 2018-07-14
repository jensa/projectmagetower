package magetower.event

import magetower.action.ActionResult
import magetower.action.Choice
import magetower.action.ChoiceInput
import magetower.TowerState
import magetower.action.Action
import magetower.contract.ContractProgress
import magetower.event.ContractStep.ChoiceState.*
import magetower.spell.SpellStone
import magetower.staff.Employee

class ContractStep(var state : TowerState.TowerView,
                   override var handleAfter : Int,
                   var contractProgress : ContractProgress,
                   var lastStep : ContractStep?) : EventAction {

    private enum class ChoiceState {
        BRIEFING, REINFORCE, CHOOSE_STAFF,CHOOSE_SPELLSTONE, COMPLETE
    }

    private var choiceState = BRIEFING
    var reinforcementMade = false


    override fun doAction(state: TowerState.TowerView): Action {
        val contractStepResult = contractProgress.investTime(contractProgress.timeInvestments.last(), null)
        return ContractStep(state, handleAfter, contractStepResult, lastStep)
    }

    override fun description(): String {
        return "Contract progress - ${contractProgress.contract.title}}"
    }

    override fun hasSteps(): Boolean {
        return choiceState != COMPLETE
    }

    /*
        each step will report on the progress: potency for each property fulfilled so far
        fulfilled potency is days progressed * random +  mages properties
        if fulfilled, will report success + total payment and make the mage avaliable
        if any category not complete yet, will have the option to (for payment) send more spells/people
        should spells be possible to create fast, or be a process? process....
        if no spells to send or no money, fail the contract
     */

    override fun promptChoices(): Choice {
        return when(choiceState) {
            BRIEFING -> Choice(getBriefingText(), Choice.InputType.NONE)
            REINFORCE -> yesNoChoice("Would you like to reinforce this contract?")
            CHOOSE_STAFF -> listChoicePlusContinue("Which employee?", getUnusedStaff())
            CHOOSE_SPELLSTONE -> listChoicePlusContinue("Which spells?", getUnusedSpellStones())
            COMPLETE -> Choice("", Choice.InputType.NONE)
        }
    }

    private fun getBriefingText() : String {
        return if(contractProgress.isFulfilled()) {
            "Contract ${contractProgress.contract.title} is complete. A payment of ${contractProgress.getPayment()} will be made"
        } else {
            "Contract ${contractProgress.contract.title} is not done yet!"
        }
    }

    override fun processInput(input: ChoiceInput): ActionResult? {
        choiceState = when(choiceState) {
            BRIEFING -> if(contractProgress.isFulfilled()) COMPLETE else REINFORCE
            REINFORCE ->  if(input.getYesNo()) COMPLETE else CHOOSE_STAFF
            CHOOSE_STAFF -> if(input.getNumber() >= getUnusedStaff().size) {
                CHOOSE_SPELLSTONE
            } else {
                contractProgress.employees.add(getUnusedStaff()[input.getNumber()])
                CHOOSE_STAFF
            }
            CHOOSE_SPELLSTONE -> if(input.getNumber() >= getUnusedSpellStones().size) {
                reinforcementMade = true
                COMPLETE
            } else {
                contractProgress.spellStones.add(getUnusedSpellStones()[input.getNumber()])
                CHOOSE_SPELLSTONE
            }
            COMPLETE -> COMPLETE
        }
        if(choiceState == COMPLETE && !reinforcementMade) {
            return if(contractProgress.isFulfilled()){
                ActionResult("You have completed contract on ${contractProgress.contract.title}!")
                        .addStateChangeCallback {
                            contractProgress.employees.forEach { employee ->
                                it.makeEmployeeAvaliable(employee)
                                it.addG(contractProgress.getPayment())
                            }
                        }
            } else {
                ActionResult("You've failed the contract!!")
            }

        } else if(reinforcementMade) {
            return ActionResult("Reinforcement made")
                    .addStateChangeCallback {
                        contractProgress.employees.forEach { employee ->
                            it.assignEmployeeToJob(employee)
                        }
                        contractProgress.spellStones.forEach { spellstone ->
                            it.useSpellstone(spellstone)
                        }
                    }
        }
        return null
    }

    private fun getUnusedStaff() : List<Employee> {
        return state.getAvaliableEmployees().filter { !contractProgress.employees.contains(it) }
    }

    private fun getUnusedSpellStones() : List<SpellStone> {
        return state.getSpellStones().filter { !contractProgress.spellStones.contains(it) }
    }


    override fun hasSideEffect() : Boolean{
        return reinforcementMade
    }

    override fun getSideEffect() : EventAction? {
        val length = contractProgress.timeInvestments.last()
        return ContractStep(state, state.getDay() + length, contractProgress, this)
    }



}