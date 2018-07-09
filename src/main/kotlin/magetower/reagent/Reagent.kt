package se.magetower.reagent

import java.util.*

class Reagent(var name : String,
              var id : String,
              var priceFloor : Int,
              var priceCeiling : Int,
              var potencyFloor : Int,
              var potencyCeiling : Int) {
    var finalPrice = 0
    var finalPotency = 0

    fun build(finalPrice : Int) : Reagent {
        var reagent = Reagent(this.name,this.id,this.priceFloor,this.priceCeiling,this.potencyFloor,this.potencyCeiling)
        reagent.finalPrice = finalPrice
        reagent.finalPotency = Random().nextInt(this.potencyCeiling - this.potencyFloor) + this.potencyFloor
        return reagent
    }

    override fun toString(): String {
        return name
    }
}