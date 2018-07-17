package magetower

import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient
import kotlinx.serialization.json.JSON
import magetower.action.*
import magetower.spell.MagicBranch
import magetower.spell.SpellStone
import magetower.town.MagentificCommunity
import magetower.town.Time
import magetower.contract.Contract
import magetower.town.TownHall
import magetower.reagent.Reagent
import magetower.spell.Spell
import magetower.staff.Employee
import se.magetower.town.ReagentShop
import java.io.File
import kotlin.collections.ArrayList

@Serializable
class TowerState(
        private var researchedSpells : ArrayList<Spell> = ArrayList()
        ,private var spellStones : ArrayList<SpellStone> = ArrayList()
        ,private var reagents : ArrayList<ArrayList<Reagent>> = ArrayList()
        ,private var employees : ArrayList<Employee> = ArrayList()
        ,private var contractsInProgress : ArrayList<Contract> = ArrayList()
        ,private var magentificCommunity : MagentificCommunity = MagentificCommunity()
        ,private var townHall : TownHall = TownHall()
        ,private var reagentShop : ReagentShop = ReagentShop()
        , var possibleActions : ArrayList<String> = arrayListOf("Buy reagents","Research spell","Inspect tower", "Save game")
        ,private var gold : Int = 100
        ,private var time : Time = Time()) {


    @kotlinx.serialization.Transient
    val changeTower = ChangeTower()
    @kotlinx.serialization.Transient
    val viewTower = TowerView()
    /*
    init {
        researchedSpells = ArrayList()
        spellStones = ArrayList()
        reagents = ArrayList()
        employees = arrayListOf(Employee("You", true, 1))
        contractsInProgress = ArrayList()
        changeTower = ChangeTower()
        viewTower = TowerView()
        magentificCommunity = MagentificCommunity()
        townHall = TownHall(viewTower)
        reagentShop = ReagentShop()
        possibleActions = arrayListOf(
                BuyReagent(),
                ResearchSpell(viewTower),
                InspectTower(viewTower))

        gold = 100
        time = Time()
    } */

    fun getStateString() : String{
        time.saveDay()
        return JSON.stringify(this)
    }

    @Transient private val allActions = listOf(
            BuyReagent(),
            ResearchSpell(viewTower),
            InspectTower(viewTower),
            CreateSpellStone(viewTower),
            TakeContract(viewTower),
            ViewSpells(viewTower),
            CreateReagent(viewTower),
            SaveGame())
            .map { it.description() to it }
            .toMap()

    fun getPossibleActions() : List<Action> {
        return possibleActions.mapNotNull {
            allActions[it]
        }
    }

    inner class TowerView {

        fun getSpellStones() : List<SpellStone> {
            return spellStones
        }

        fun getAvaliableEmployees() : List<Employee> {
            return employees.filter { it.avaliableForNewJob }
        }

        fun canNegotiateContracts () : Boolean {
            return false
        }

        fun getAvaliableContracts() : List<Contract> {
            return townHall.getCurrentContracts(getDay(), this)
        }

        fun getDay() : Int {
            return time.getCurrentDay().toInt()
        }

        fun listReagents() : String{
            return reagents.filter { it.size > 0 }.map { "${it[0].name} (${it.size})" }.joinToString(",")
        }

        fun getReagentsByType() : List<List<Reagent>> {
            return reagents.filter { it.size > 0 }
        }

        fun getReagents() : List<Reagent> {
            return reagents.filter { it.size > 0 }.flatten()
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
            if (getReagentsByType().isNotEmpty()) strings.add("Reagents: ${listReagents()}")
            if (contractsInProgress.isNotEmpty()) strings.add("Contracts: ${listString(contractsInProgress)}")
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
            addActionIfNotPresent(TakeContract(viewTower))
        }

        fun useReagent (reagent : Reagent) : Boolean {
            val reagentGroup = reagents.find { it.size > 0 && it[0].id == reagent.id } ?: return false
            return reagentGroup.remove(reagent)
        }

        fun addReagent(reagent : Reagent) {
            val reagentGroup = reagents.find { it.size > 0 && it[0].id ==reagent.id }
            if(reagentGroup == null) {
                reagents.add(arrayListOf(reagent))
            } else {
                reagentGroup.add(reagent)
            }
            addActionIfNotPresent(CreateReagent(viewTower))
        }

        fun addSpell(spell : Spell) {
            researchedSpells.add(spell)
            addActionIfNotPresent(ViewSpells(viewTower))
            addActionIfNotPresent(CreateSpellStone(viewTower))
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

        fun makeEmployeeAvaliable(employee: Employee) {
            employee.avaliableForNewJob = true
        }

        fun assignEmployeeToJob(employee: Employee) {
            employee.avaliableForNewJob = false
        }

        fun useSpellstone(spellstone: SpellStone) {
            spellStones.remove(spellstone)
        }

        fun takeContract(contract : Contract) {
            townHall.takeContract(contract)
        }

        fun takeSpellstone(sp: SpellStone) {
            spellStones.remove(sp)
        }

        fun addActionIfNotPresent(action : Action) {
            if(possibleActions.find { it == action.description()} == null) {
                possibleActions.add(action.description())
            }
        }

        fun saveState() {
            val file = File("state.json")
            file.createNewFile()
            file.bufferedWriter().use { out ->
                out.write(getStateString())
                out.close()
            }
        }
    }

}