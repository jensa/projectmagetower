package magetower.contract

import magetower.contractPayment
import magetower.spell.SpellBuilder
import magetower.spell.SpellStone
import magetower.staff.Staff

class ContractProgress(val contract : Contract) {

    var timeInvestments : ArrayList<Int> = ArrayList()
    private var finalPayment = contract.payment
    var staff = ArrayList<Staff>()
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
        return true
    }

    fun getPayment() : Int{
        // payment variables: time required/time taken (what _actually_ causes delays? )
        // any potency overflowing contract fulfillment
        // payment multiple.. somehow?
        return contractPayment()
    }

}