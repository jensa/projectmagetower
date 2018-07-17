package magetower

import magetower.contract.Contract
import magetower.staff.Employee
import java.util.*
import kotlin.collections.ArrayList

/*
math questions:
what is the relationship between potency required for a contract and the payment recieved?
potency of spellstones vs time/money invested?
potency of employees vs their pay vs their training (maybe pay rises as training rises, at some interval)?
potency of spells vs time invested

 */

private val r = Random()

fun spellstonePotency(totalSpellPotency: Int, spellPropertyPotency: Int, reagentPotency: Int, employees: List<Employee>) : Int {
    return totalSpellPotency + spellPropertyPotency + reagentPotency
}

fun spellReagentCost(investment : Int) : Int {
    return investment * 5
}

fun totalSpellPotency(investment: Int): Int {
    return investment * (r.nextInt(50) + 50)
}

fun spellTimeInvestmentOutcome(existing: Int,
                               noFocusAreas: Boolean,
                               focusRatio : Int, time : Int): Int {
    var randomCeiling = 2 + if(noFocusAreas) 1 else 0
    randomCeiling += focusRatio
    return (existing + randomFromOne(randomCeiling)) * time
}

fun reagentPotency(potencyFloor: Int, potencyCeiling: Int): Int {
    return r.nextInt(potencyCeiling - potencyFloor) + potencyFloor
}

fun reagentPrice(priceFloor: Int, priceCeiling: Int) : Int {
    return r.nextInt(priceCeiling - priceFloor + 1) + priceFloor
}

fun contractCost(payment : Int) : Int {
    return payment / (r.nextInt(100) + 1)
}

fun contractPayment(day : Int) : Int {
    return (day/10) * randomFromOne(10)
}

fun contractComplexity(payment : Int, allProperties : Set<String>) : Map<String,Int> {
    val numProperties = randomFromOne(payment/1000)
    val totalComplexity = payment / (100- randomFromOne(30))
    var complexityLeft = totalComplexity
    return allProperties.shuffled().subList(0, numProperties).mapIndexed { i,it ->
        var complexity = complexityLeft - randomFromOne(complexityLeft)
        complexityLeft -= complexity
        if(i == numProperties - 1){
            complexity += complexityLeft
        }
        it to complexity
    }.toMap()
}

private fun randomFromOne(ceil : Int) : Int {
    return r.nextInt(if(ceil < 1) 1 else ceil) + 1
}

fun negotiateContract(state : TowerState.TowerView, askedAmount : Int) : Int? {
    return null
}

fun contractPayment(contract: Contract): Int {
    return contract.payment
}

fun fulfilledPotency(requirement: Int, totalSpellstone: Int,
                     employees: ArrayList<Employee>, totalTime: Int): Boolean {
    val avgCompetence = (employees.map { it.spellSkillMultiplier }.sum() / employees.size)
    return totalTime * totalSpellstone * avgCompetence > requirement
}