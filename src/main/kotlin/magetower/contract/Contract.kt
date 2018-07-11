package magetower.contract

import magetower.contractCost

open class Contract(var title : String,
               var description : String,
               var potencyRequired : List<Pair<String,Int>>,
               var payment : Int,
               var paymentMultiple : Int,
               var effortRequired : Int,
               var expiresAt : Int) {

    var cost : Int = contractCost(payment)

    override fun toString(): String {
        return title
    }
}