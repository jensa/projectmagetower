package se.magetower.action

import magetower.action.informPlayer
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

    override fun promptChoices() {
        informPlayer(state.reagentShop.avaliableReagents
                .mapIndexed { i,it -> "${i}. ${it.first.name} (${it.second} g)" }
                .joinToString("\n"))
    }

    override fun processInput(inputList: List<String>) {
        val input = inputList[0].toIntOrNull()
        if(input == null || input >= state.reagentShop.avaliableReagents.size){
            informPlayer("invalid reagent number")
        } else {
            val chosenReagent = state.reagentShop.avaliableReagents[input]
            doPurchase(chosenReagent.first, chosenReagent.second)
        }
    }

    private fun doPurchase(reagent : Reagent, price : Int) {
        if(state.takeG(price)){
            if(state.reagents[reagent.id] == null) {
                state.reagents[reagent.id] = ArrayList()
            }
            state.reagents[reagent.id]!!.add(reagent.build(price))
        } else {
            informPlayer("Not enough gold")
        }
        purchaseComplete = true
    }
}