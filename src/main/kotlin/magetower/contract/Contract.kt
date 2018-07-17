package magetower.contract

import kotlinx.serialization.Serializable
import magetower.TowerState
import magetower.contractComplexity
import magetower.contractCost

@Serializable
open class Contract(var title : String, var description : String, var payment : Int, var expires : Int) {

    var cost : Int = contractCost(payment)

    var potencyRequired : Map<String,Int>? = null

    fun calculateRequirements(state : TowerState.TowerView) : Contract{
        val allProperties = state.getDiscoveredMagicBranches()
                .map { it.properties.keys }
                .reduce { acc, set -> acc.plus(set) }
        potencyRequired = contractComplexity(payment, allProperties)
        return this
    }

    fun totalPotencyRequired() : Int {
        return potencyRequired!!.values.sum()
    }

    override fun toString(): String {
        return title
    }
}