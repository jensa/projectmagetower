package magetower

import magetower.action.*
import magetower.spell.MagicBranch
import magetower.spell.SpellStone
import magetower.town.MagentificCommunity
import magetower.town.Time
import magetower.contract.Contract
import magetower.reagent.Reagent
import magetower.spell.Spell
import magetower.staff.Staff
import se.magetower.town.ReagentShop
import kotlin.collections.ArrayList

class TowerState {



    private var researchedSpells : ArrayList<Spell> = ArrayList()
    private var spellStones : ArrayList<SpellStone> = ArrayList()
    private var reagents = ArrayList<ArrayList<Reagent>>()
    private var staff = ArrayList<Staff>()
    var changeTower = ChangeTower()
    var viewTower = TowerView()
    var magentificCommunity = MagentificCommunity()
    var contracts : ArrayList<Contract> = ArrayList()
    var reagentShop = ReagentShop()
    var possibleActions = arrayListOf(
            BuyReagent(viewTower),
            TakeContract(viewTower),
            ResearchSpell(viewTower),
            InspectTower(viewTower))

    private var gold = 100
    private var time = Time()

    inner class TowerView {

        fun getSpellStones() : List<SpellStone> {
            return spellStones
        }

        fun getStaff() : List<Staff> {
            return staff
        }

        fun canNegotiateContracts () : Boolean {
            return false
        }

        fun getAvaliableContracts() : List<Contract> {
            return contracts
        }

        fun getDay() : Int {
            return time.getCurrentDay().toInt()
        }

        fun listReagents() : String{
            return reagents.filter { it.size > 0 }.map { "${it[0].name} (${it.size})" }.joinToString(",")
        }

        fun getReagents() : List<List<Reagent>> {
            return reagents.filter { it.size > 0 }
        }

        fun getResearchedSpells() : List<Spell> {
            return researchedSpells.toList()
        }

        fun getAvaliableShopReagents() : List<Pair<Reagent,Int>> {
            return reagentShop.avaliableReagents
        }

        fun getDiscoveredMagicBranches () : List<MagicBranch> {
            return magentificCommunity.discoveredBranches
        }

        fun g() : Int {
            return gold
        }

        fun hasG(amount : Int) : Boolean {
            return gold >= amount
        }


        override fun toString(): String {
            val strings = arrayListOf("Gold: $gold g")
            if (getResearchedSpells().isNotEmpty()) strings.add("Spells: ${listString(getResearchedSpells())}")
            if (getReagents().isNotEmpty()) strings.add("Reagents: ${listReagents()}")
            if (contracts.isNotEmpty()) strings.add("Contracts: ${listString(contracts)}")
            if (spellStones.isNotEmpty()) strings.add("Spellstones: ${listString(spellStones)}")
            return strings.joinToString("\n")
        }
    }

    inner class ChangeTower {

        fun time() : Time {
            return time
        }

        fun addSpellStone(spellstone : SpellStone) {
            spellStones.add(spellstone)

        }

        fun useReagent (reagent : Reagent) : Boolean {
            val reagentGroup = reagents.find { it.size > 0 && it[0].id == reagent.id } ?: return false
            return reagentGroup.remove(reagent)
        }

        fun addReagent(reagent : Reagent) {
            val reagentGroup = reagents.find { it.size > 0 && it[0].id ==reagent.id }
            if(reagentGroup == null) {
                reagents.add(arrayListOf(reagent))
                possibleActions.add(CreateReagent(viewTower))
            } else {
                reagentGroup.add(reagent)
            }
        }

        fun addSpell(spell : Spell) {
            if(researchedSpells.isEmpty()){
                possibleActions.add(ViewSpells(viewTower))
                possibleActions.add(CreateSpellStone(viewTower))
            }
            researchedSpells.add(spell)
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
    }

}