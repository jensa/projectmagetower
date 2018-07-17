package magetower.action

import magetower.TowerState
import magetower.event.yesNoChoice

class SaveGame() : Action(this::class.toString()) {

    override fun doAction(state: TowerState.TowerView): Action {
        return SaveGame()
    }

    private var choiceMade = false

    override fun description(): String {
        return "Save game"
    }

    override fun hasSteps(): Boolean {
        return !choiceMade
    }

    override fun promptChoices(): Choice {
        return yesNoChoice("Would you like to save?")
    }

    override fun processInput(input: ChoiceInput): ActionResult? {
        choiceMade = true
        return if(input.getYesNo()) {
            ActionResult("Saving!").addStateChangeCallback {
                it.saveState()
            }
        } else {
            ActionResult("Not saving")
        }
    }
}