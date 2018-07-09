package se.magetower

import magetower.action.listString
import magetower.town.MagentificCommunity
import se.magetower.contract.Contract
import se.magetower.reagent.Reagent
import se.magetower.spell.Spell
import se.magetower.town.ReagentShop
import java.util.*
import kotlin.collections.ArrayList
import kotlin.collections.HashMap

class TowerState {

    var spells : ArrayList<Spell> = ArrayList()
    var reagents = HashMap<String,ArrayList<Reagent>>()
    var magentificCommunity = MagentificCommunity()
    var contracts : Queue<Contract> = LinkedList<Contract>()
    var reagentShop = ReagentShop()
    private var gold = 100

    override fun toString(): String {
        return "Spells: ${listString(spells)}\n" +
                "Reagents: ${listReagents()}\n" +
                "Contracts: ${listString(contracts)}\n" +
                "Gold: $gold g"
    }

    fun listReagents() : String{
        return reagents.filter { it.value.size > 0 }.map { "${it.value[0].name} (${it.value.size})" }.joinToString(",")
    }

    fun takeG(amount : Int) : Boolean {
        if(gold < amount){
            return false
        }
        gold -= amount
        return true
    }

    fun addG(amount : Int) {
        gold += amount
    }

    fun g() : Int {
        return gold
    }

}