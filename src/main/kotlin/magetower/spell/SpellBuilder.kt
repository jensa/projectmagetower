package magetower.spell

import kotlinx.serialization.Serializable
import magetower.reagent.ReagentRequirement
import magetower.spellReagentCost
import magetower.totalSpellPotency
import magetower.reagent.Reagent
import magetower.spellTimeInvestmentOutcome
import java.util.*
import kotlin.collections.ArrayList

@Serializable
class SpellBuilder(var branch : MagicBranch) {

    var name : String = ""
    var investments = ArrayList<Int>()
    var properties = branch.properties.toList()

    fun investTime(time : Int, focusAreas : Map<String,Int>) : SpellBuilder {
        properties = properties
                .map { pair ->
            return@map pair.first to spellTimeInvestmentOutcome(
                    pair.second,
                    focusAreas.containsKey(pair.first),
                    if(focusAreas.isEmpty()) 1 else properties.size/focusAreas.size,
                    time)
        }.toList()
        return this
    }

    fun build(avaliableReagents: List<Pair<Reagent, Int>>): Spell {
        /*
        spells have properties, but also potency, and cost
        cost is expressed in reagent cost
        potency is expressed as "how successful are you while doing a contract", which translates to cashh
        effective contract potency is a combination of 1)reagent potency and 2) researched spell base potency
        this is then multiplied by contract/magic branch match
         */

        val totalInvestment = investments.reduce { acc, i -> acc + i }
        //total reagent cost is total investments/5, reagentPriceCeiling is used for the calculation
        val totalReagentCost = spellReagentCost(totalInvestment)
        var reagentCost = 0
        var reagents = ArrayList<ReagentRequirement>()
        while(reagentCost < totalReagentCost) {
            var nextReagent = avaliableReagents.shuffled()[0].first
            reagents.add(ReagentRequirement(nextReagent.id, nextReagent.name))
            reagentCost += nextReagent.priceCeiling
        }
        val potency = totalSpellPotency(totalInvestment)
        return Spell(name,branch,investments, properties, potency, reagents)
    }

}