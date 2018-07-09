package magetower.town

import com.beust.klaxon.Klaxon
import magetower.spell.MagicBranch
import java.util.*

class MagentificCommunity {


    var allBranches : List<MagicBranch> = ArrayList()
    var discoveredBranches : List<MagicBranch> = ArrayList()

    init {
        val allBranches =
                Klaxon().parseArray<MagicBranch>(this.javaClass.classLoader.getResource("magicBranches.json").readText())
        discoveredBranches = allBranches!!.shuffled().subList(0,3)
    }
}