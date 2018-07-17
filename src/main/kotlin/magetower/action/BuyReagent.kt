package magetower.action

import kotlinx.serialization.Serializable
import magetower.event.listChoice
import magetower.TowerState

@Serializable
class BuyReagent : Action(this::class.toString()) {

    var purchaseComplete = false
    var state : TowerState.TowerView? = null

    fun setState(state: TowerState.TowerView) : Action {
        this.state = state
        return this
    }

    override fun doAction(state: TowerState.TowerView): Action {
        return BuyReagent().setState(state)
    }

    override fun description(): String {
        return "Buy reagents"
    }

    override fun hasSteps(): Boolean {
        return !purchaseComplete
    }

    override fun promptChoices(): Choice {
        return listChoice("Avaliable reagents:",
                state!!.getAvaliableShopReagents().map { "${it.first.name} (${it.second} g)" })
    }

    override fun processInput(input: ChoiceInput): ActionResult? {
        val choice = input.getNumber()
        return if(choice >= state!!.getAvaliableShopReagents().size){
            ActionResult("invalid reagent number")
        } else {
            purchaseComplete = true
            val chosenReagent = state!!.getAvaliableShopReagents()[choice]
            val price = chosenReagent.second
            val reagent = chosenReagent.first
            ActionResult(if(state!!.hasG(chosenReagent.second)) "Bought ${reagent.name} for $price g" else "Not enough gold")
                    .addStateChangeCallback {
                        it.takeG(price)
                        it.addReagent(reagent.build(price))
                    }
        }
    }
}