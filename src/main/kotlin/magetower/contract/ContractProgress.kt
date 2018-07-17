package magetower.contract

import kotlinx.serialization.Serializable
import magetower.contractPayment
import magetower.fulfilledPotency
import magetower.spell.SpellStone
import magetower.staff.Employee

@Serializable
class ContractProgress(val contract : Contract) {

    var timeInvestments : ArrayList<Int> = ArrayList()
    private var finalPayment = contract.payment
    var employees = ArrayList<Employee>()
    var spellStones = ArrayList<SpellStone>()

    fun contractDetails() : String {
        return "Pays ${contract.payment}. Costs ${contract.cost}. Total effort required: ${contract.totalPotencyRequired()}." +
                "\nSpecific efforts:${contract.potencyRequired!!.map { "${it.key}:${it.value}" }.joinToString(",")}"
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
        return contract.potencyRequired!!.map { required ->
            fulfilledPotency(required.value, spellStonePotencies[required.key]!!, employees, totalTime)
        }.none { !it }
    }

    fun getSpellstonePotenciesPerRequiredProperty() : Map<String,Int>{
        return contract.potencyRequired!!.map { required ->
            val potency = spellStones.map { spellStone ->
                val matchingProperty = spellStone.potencies.find { potency -> potency.first == required.key}
                matchingProperty?.second ?: 0
            }.sum()
            Pair(required.key, potency)
        }.toMap()
    }

    fun getPayment() : Int{
        // payment variables: time required/time taken (what _actually_ causes delays? )
        // any potency overflowing contract fulfillment
        // payment multiple.. somehow?
        return contractPayment(contract)
    }

    override fun toString(): String {
        return contract.title
    }

}