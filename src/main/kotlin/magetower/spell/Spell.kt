package se.magetower.spell

import magetower.spell.MagicBranch

class Spell(var name : String,
            var branch : MagicBranch,
            var invest : List<Int>,
            var usability : Int,
            var power : Int,
            var area : Int,
            var versatility : Int,
            var castTime : Int,
            var cool : Int) {

    override fun toString(): String {
        return "$name ($branch)"
    }
}