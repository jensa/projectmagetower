package magetower.contract

import com.beust.klaxon.Klaxon

class TownHall {


    var contracts : ArrayList<Contract> = ArrayList()
    init {
        val allContracts =
                Klaxon().parseArray<Contract>(this.javaClass.classLoader.getResource("contracts.json").readText())
        contracts = ArrayList(allContracts!!.shuffled().subList(0,3))
    }


}