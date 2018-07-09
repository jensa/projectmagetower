package se.magetower.town

import com.beust.klaxon.Klaxon
import se.magetower.reagent.Reagent
import java.util.*

class ReagentShop {

    var avaliableReagents : List<Pair<Reagent,Int>> = ArrayList()

    init {
        val allReagents =
        Klaxon().parseArray<Reagent>(this.javaClass.classLoader.getResource("reagents.json").readText())
        avaliableReagents = allReagents!!
                .map { it to Random().nextInt(it.priceCeiling - it.priceFloor + 1) + it.priceFloor }
    }

}