package se.magetower.town

import com.beust.klaxon.Klaxon
import magetower.reagent.Reagent
import magetower.reagentPrice
import java.util.*

class ReagentShop {

    var avaliableReagents : List<Pair<Reagent,Int>> = ArrayList()

    init {
        val allReagents =
        Klaxon().parseArray<Reagent>(this.javaClass.classLoader.getResource("reagents.json").readText())
        avaliableReagents = allReagents!!.map { it to reagentPrice(it.priceFloor, it.priceCeiling) }
    }

}