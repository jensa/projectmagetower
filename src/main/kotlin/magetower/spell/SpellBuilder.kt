package magetower.spell

import se.magetower.spell.Spell

class SpellBuilder {

    var name : String = ""
    var branch : MagicBranch? = null
    var investments = ArrayList<Int>()
    var usability = 0
    var power = 0
    var area = 0
    var versatility = 0
    var castTime = 0
    var cool = 0



    fun build(): Spell {
        return Spell(name,branch!!,investments,usability,power,area,versatility,castTime,cool)
    }
}