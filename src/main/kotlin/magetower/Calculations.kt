package magetower

import java.util.*

private val r = Random()

fun spellstonePotency(totalSpellPotency : Int, spellPropertyPotency : Int, reagentPotency : Int) : Int {
    return totalSpellPotency + spellPropertyPotency * reagentPotency
}

fun spellReagentCost(investment : Int) : Int {
    return investment * 10
}

fun totalSpellPotency(investment: Int): Int {
    return investment * (r.nextInt(50) + 50)
}

fun spellTimeInvestmentOutcome(existing: Int,
                               isFocusArea : Boolean,
                               noFocusAreas: Boolean,
                               focusRatio : Int, time : Int): Int {
    val focusMultiplier = if(noFocusAreas) 1 else focusRatio
    var randomCeiling = 2 + if(noFocusAreas) 1 else 0
    if(isFocusArea){
        randomCeiling += focusMultiplier
    }
    return (existing + (r.nextInt(randomCeiling) + 1)) * time
}

fun reagentPotency(potencyFloor: Int, potencyCeiling: Int): Int {
    return r.nextInt(potencyCeiling - potencyFloor) + potencyFloor
}

fun reagentPrice(priceFloor: Int, priceCeiling: Int) : Int {
    return r.nextInt(priceCeiling - priceFloor + 1) + priceFloor
}

fun contractCost(payment : Int) : Int {
    return payment / (r.nextInt(10) + 1)
}

fun negotiateContract(state : TowerState.TowerView, askedAmount : Int) : Int? {
    return null
}

fun contractPayment() : Int {
    return 0
}