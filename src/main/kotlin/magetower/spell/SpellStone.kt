package magetower.spell

import magetower.spellstonePotency
import magetower.reagent.Reagent

class SpellStone(var spell : Spell, var reagents : List<Reagent>) {

    var potencies : List<Pair<String,Int>> = ArrayList()

    init {
        val reagentPotency = reagents.fold(0) {acc,r -> acc + r.finalPotency}
        potencies = spell.properties.map {
            /*
            Spell stone potency calculation
            spell potency + property potency * reagent potency
             */
            it.first to spellstonePotency(spell.potency, it.second, reagentPotency)
        }
    }

    override fun toString(): String {
        return "${spell.name} (avg: ${potencies.fold(0) { acc, pair -> acc + pair.second }/potencies.size})"
    }
}