package magetower.spell

import kotlinx.serialization.Serializable
import magetower.spellstonePotency
import magetower.reagent.Reagent
import magetower.staff.Employee

@Serializable
class SpellStone(var spell : Spell, var reagents : List<Reagent>, var employees : List<Employee>) {

    var potencies : List<Pair<String,Int>> = ArrayList()

    init {
        val reagentPotency = reagents.fold(0) {acc,r -> acc + r.finalPotency}
        potencies = spell.properties.map {
            /*
            Spell stone potency calculation
            spell potency + property potency * reagent potency + employee competency? maybe
             */
            it.first to spellstonePotency(spell.potency, it.second, reagentPotency, employees)
        }
    }

    override fun toString(): String {
        return "${spell.name} (avg: ${potencies.fold(0) { acc, pair -> acc + pair.second }/potencies.size})"
    }
}