package magetower.action

import magetower.action.Choice.InputType
import magetower.TowerState
import magetower.action.TemplateAction.ChoiceState.*

class TemplateAction(var state: TowerState.TowerView) : Action {

    private enum class ChoiceState {
        START,MIDDLE,END
    }

    private var choiceState = START

    override fun doAction(state: TowerState.TowerView): Action {
        return TemplateAction(state)
    }

    override fun description(): String {
        return "changeme"
    }

    override fun hasSteps(): Boolean {
        return false
    }

    override fun promptChoices(): Choice {
        return when(choiceState) {

            START -> Choice("template", InputType.NONE)
            MIDDLE -> Choice("template", InputType.NONE)
            END -> Choice("template", InputType.NONE)
        }
    }

    override fun processInput(input: ChoiceInput): ActionResult? {
        choiceState = when(choiceState) {
            START -> {
                MIDDLE
            }
            MIDDLE ->  {
                END
            }
            END -> return null
        }
        return if(choiceState == END) {
            ActionResult("done")
        } else {
            null
        }
    }
}