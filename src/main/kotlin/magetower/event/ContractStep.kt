package magetower.event

import magetower.action.ActionResult
import magetower.action.Choice
import magetower.action.ChoiceInput
import magetower.TowerState
import magetower.action.Action
import magetower.contract.ContractProgress
import magetower.event.ContractStep.ChoiceState.*

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
        if fulfilled, will report success + total payment and make the mage avaliable
        if any category not complete yet, will have the option to (for payment) send more spells/people
        should spells be possible to create fast, or be a process? process....
        if no spells to send or no money, fail the contract
     */

    override fun promptChoices(): Choice {
        return when(choiceState) {
            BRIEFING -> Choice(getBriefingText(), Choice.InputType.NONE)
            REINFORCE -> TODO()
            CHOOSE_STAFF -> TODO()
            CHOOSE_SPELLSTONE -> TODO()
            COMPLETE -> TODO()
        }
    }

    private fun getBriefingText() : String {
        return if(contractProgress.isFulfilled()) {
            "Contract ${contractProgress.contract.title} is complete. A payment of ${contractProgress.getPayment()} will be made"
        } else {
            ""
        }
    }

    override fun processInput(input: ChoiceInput): ActionResult? {
        choiceState = when(choiceState) {
            BRIEFING -> if(contractProgress.isFulfilled()) COMPLETE else REINFORCE
            REINFORCE -> COMPLETE
            CHOOSE_STAFF -> COMPLETE
            CHOOSE_SPELLSTONE -> COMPLETE
            COMPLETE -> COMPLETE
        }
        if(choiceState == COMPLETE && !reinforcementMade) {
            return ActionResult("You have completed contract on ${contractProgress.contract.title}!")
                    .addStateChangeCallback {}
        }
        return null
    }

    override fun hasSideEffect() : Boolean{
        return reinforcementMade
    }

    override fun getSideEffect() : EventAction? {
        val length = contractProgress.timeInvestments.last()
        return ContractStep(state, state.getDay() + length, contractProgress, this)
    }



}