package magetower.contract

import magetower.contractPayment
import magetower.fulfilledPotency
import magetower.spell.SpellStone
import magetower.staff.Employee

class ContractProgress(val contract : Contract) {

    var timeInvestments : ArrayList<Int> = ArrayList()
    private var finalPayment = contract.payment
    var employees = ArrayList<Employee>()
    var spellStones = ArrayList<SpellStone>()

    fun details() : String {
        return toString()
    }

    fun setPayment(payment: Int) {
        this.finalPayment = payment
    }

    fun investTime(time: Int, spellStones : List<SpellStone>?): ContractProgress {
        timeInvestments.add(time)
        if(spellStones != null) {
            this.spellStones = ArrayList(this.spellStones.plus(spellStones))
        }
        return this
    }

    fun isFulfilled(): Boolean {
        val totalTime = timeInvestments.sum()
        val spellStonePotencies = getSpellstonePotenciesPerRequiredProperty()
        return contract.potencyRequired.map { required ->
            fulfilledPotency(required.second, spellStonePotencies[required.first]!!, employees, totalTime)
        }.none { !it }
    }

    fun getSpellstonePotenciesPerRequiredProperty() : Map<String,Int>{
        return contract.potencyRequired.map { required ->
            val potency = spellStones.map { spellStone ->
                val matchingProperty = spellStone.potencies.find { potency -> potency.first == required.first}
                matchingProperty?.second ?: 0
            }.sum()
            Pair(required.first, potency)
        }.toMap()
    }

    fun getPayment() : Int{
        // payment variables: time required/time taken (what _actually_ causes delays? )
        // any potency overflowing contract fulfillment
        // payment multiple.. somehow?
        return contractPayment(contract)
    }

}