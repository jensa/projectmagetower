package se.magetower.action

import magetower.action.ActionResult
import magetower.action.Choice
import magetower.action.Choice.InputType
import magetower.action.ChoiceInput
import magetower.event.listChoice
import se.magetower.TowerState
import se.magetower.reagent.Reagent
import java.util.ArrayList

class BuyReagent(var state: TowerState) : Action {

    var purchaseComplete = false

    override fun doAction(state: TowerState): Action {
        return BuyReagent(state)
    }

    override fun description(): String {
        return "Buy reagents"
    }

    override fun hasSteps(): Boolean {
        return !purchaseComplete
    }

    override fun promptChoices(): Choice {
        return listChoice("Avaliable reagents:",
                state.reagentShop.avaliableReagents.map { "${it.first.name} (${it.second} g)" })
    }

    override fun processInput(input: ChoiceInput): ActionResult? {
        val choice = input.getNumber()
        return if(choice >= state.reagentShop.avaliableReagents.size){
            ActionResult("invalid reagent number")
        } else {
            val chosenReagent = state.reagentShop.avaliableReagents[choice]
            ActionResult(doPurchase(chosenReagent.first, chosenReagent.second))
        }
    }

    private fun doPurchase(reagent : Reagent, price : Int) : String {
        if(state.takeG(price)){
            if(state.reagents[reagent.id] == null) {
                state.reagents[reagent.id] = ArrayList()
            }
            state.reagents[reagent.id]!!.add(reagent.build(price))
        } else {
            return "Not enough gold"
        }
        purchaseComplete = true
        return "Purchase complete"
    }
}