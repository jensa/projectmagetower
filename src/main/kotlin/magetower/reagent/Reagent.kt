package magetower.reagent

import kotlinx.serialization.Serializable
import magetower.reagentPotency

@Serializable
class Reagent(var name : String,
              var id : String,
              var priceFloor : Int,
              var priceCeiling : Int,
              var potencyFloor : Int,
              var potencyCeiling : Int) {
    var finalPrice = 0
    var finalPotency = 0

    fun build(finalPrice : Int) : Reagent {
        val reagent = Reagent(this.name,this.id,this.priceFloor,this.priceCeiling,this.potencyFloor,this.potencyCeiling)
        reagent.finalPrice = finalPrice
        reagent.finalPotency = reagentPotency(potencyFloor, potencyCeiling)
        return reagent
    }

    override fun toString(): String {
        return "$name ($finalPotency)"
    }
}