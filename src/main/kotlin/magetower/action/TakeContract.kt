package magetower.action

import magetower.TowerState
import magetower.action.TakeContract.ChoiceState.*
import magetower.contract.ContractProgress
import magetower.contract.NullContract
import magetower.event.*
import magetower.negotiateContract
import magetower.spell.SpellStone
import magetower.staff.Employee

class TakeContract(var state: TowerState.TowerView) : Action {

    private enum class ChoiceState {
        CHOOSE, VIEW_DETAILS, NEGOTIATE, CHOOSE_PARTICIPANTS, CHOOSE_SPELLS, CONFIRM_TIME_INVESTMENT, COMPLETE
    }

    private var contractProgress = ContractProgress(NullContract())
    private var choiceState = CHOOSE

    override fun doAction(state: TowerState.TowerView): Action {
        return TakeContract(state)
    }

    override fun description(): String {
        return "Take Contract"
    }

    override fun hasSteps(): Boolean {
        return choiceState != COMPLETE
    }

    override fun promptChoices(): Choice {
        /*
        steps in taking a contract:
        list avaliable contracts
        see detail view of a contract
        contract parts:
            lower limit potency
            payment: base + multiplier(?)
            magic properties needed
            time required
            money investment needed (can be 0 but will usually be some % (1-10) of the base payment)
            avaliable for (no. of days)


        options when in detail view:
            accept contract
            negotiate payment (will always fail early, only after recruiting someone with negotiating skill or after specializing in some niche branch of magic will this be possible)
            don't accept - go back to contract list view

        after accepting:
            choose people to do the contract (only yourself avaliable at first)
            choose spells (must have been created w/ reagents before?) to bring

        steps:
            each step will report on the progress: potency for each property fulfilled so far
            if fulfilled, will report success + total payment and make the mage avaliable
            if any category not complete yet, will have the option to (for payment) send more spells/people
            should spells be possible to create fast, or be a process? process....
            if no spells to send or no money, fail the contract

         */
        if(getUnusedEmployees().isEmpty()){
            return Choice("Cannot take contracts: no avaliable employees",
                    Choice.InputType.NONE)
        }
        return when(choiceState) {
            CHOOSE -> listChoice("", state.getAvaliableContracts())
            VIEW_DETAILS -> {
                val options = arrayListOf("Accept", "Reject")
                if(state.canNegotiateContracts()){
                    options.add(1, "Negotiate")
                }
                listChoice(contractProgress.details(), options)
            }
            NEGOTIATE -> Choice("What do you want for this contract?", Choice.InputType.NUMBER)
            CHOOSE_PARTICIPANTS -> listChoiceWithFinish("Choose participants", getUnusedEmployees())
            CHOOSE_SPELLS -> listChoiceWithFinish("Choose spellstones to bring",  getUnusedSpellStones())
            CONFIRM_TIME_INVESTMENT ->
                yesNoChoice("This contract will need ${contractProgress.contract.effortRequired} effort " +
                        "and ${contractProgress.contract.cost} g to complete. Do you wish to embark?")
            COMPLETE -> Choice("", Choice.InputType.NONE)
        }
    }

    private fun getUnusedEmployees() : List<Employee> {
        return state.getAvaliableEmployees().filter { !contractProgress.employees.contains(it) }
    }

    private fun getUnusedSpellStones() : List<SpellStone> {
        return state.getSpellStones().filter { !contractProgress.spellStones.contains(it) }
    }

    override fun processInput(input: ChoiceInput): ActionResult? {
        choiceState = when(choiceState) {
            CHOOSE -> {
                contractProgress = ContractProgress(state.getAvaliableContracts()[input.getNumber()])
                VIEW_DETAILS
            }
            VIEW_DETAILS -> {
                when(input.getNumber()) {
                    0 -> CHOOSE_PARTICIPANTS
                    1 -> if(state.canNegotiateContracts()) NEGOTIATE else CHOOSE
                    else -> CHOOSE
                }
            }
            NEGOTIATE ->{
                val result = negotiateContract(state, input.getNumber())
                return if(result != null) {
                    contractProgress.setPayment(result)
                    CHOOSE_PARTICIPANTS
                    ActionResult("Negotiation succeeded!")
                } else {
                    VIEW_DETAILS
                    ActionResult("Negotiation failed")
                }
            }
            CHOOSE_PARTICIPANTS -> {
                if(input.getNumber() >= getUnusedEmployees().size) {
                    CHOOSE_SPELLS
                } else {
                    contractProgress.employees.add(getUnusedEmployees()[input.getNumber()])
                    CHOOSE_PARTICIPANTS
                }
            }
            CHOOSE_SPELLS -> {
                if(input.getNumber() >= getUnusedSpellStones().size) {
                    CONFIRM_TIME_INVESTMENT
                } else {
                    contractProgress.spellStones.add(getUnusedSpellStones()[input.getNumber()])
                    CHOOSE_SPELLS
                }
            }
            CONFIRM_TIME_INVESTMENT -> if(input.getYesNo()) {
                contractProgress.timeInvestments.add(contractProgress.contract.effortRequired)
                COMPLETE
            } else CHOOSE
            COMPLETE -> COMPLETE
        }
        return if(choiceState == COMPLETE) {
            ActionResult("Contract \"${contractProgress.contract.title}\" started")
                    .addStateChangeCallback {
                        it.takeContract(contractProgress.contract)
                        it.takeG(contractProgress.contract.cost)
                    }
        } else {
            null
        }
    }

    override fun hasSideEffect() : Boolean{
        return choiceState == COMPLETE
    }

    override fun getSideEffect() : EventAction? {
        val length = contractProgress.timeInvestments[0]
        return ContractStep(state, state.getDay() + length, contractProgress, null)
    }
}