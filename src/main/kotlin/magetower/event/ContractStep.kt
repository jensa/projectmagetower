package magetower.event

import magetower.action.ActionResult
import magetower.action.Choice
import magetower.action.ChoiceInput
import magetower.contract.ContractProgress
import se.magetower.TowerState
import se.magetower.action.Action
import se.magetower.event.EventAction

class ContractStep(var state : TowerState,
                   override var handleAfter : Long,
                   var stepLength : Long,
                   var contractProgress : ContractProgress,
                   var lastStep : ContractStep?) : EventAction {


    override fun doAction(state: TowerState): Action {
        return ContractStep(state, handleAfter, stepLength, contractProgress, lastStep)
    }

    override fun description(): String {
        return "Contract progress - ${contractProgress.name}}"
    }

    override fun hasSteps(): Boolean {
        return false
    }

    override fun promptChoices(): Choice {
        return Choice("git money", Choice.InputType.NONE)
    }

    override fun processInput(input: ChoiceInput): ActionResult? {
        return null
    }


}