package se.magetower

import se.magetower.contract.Contract
import se.magetower.event.Event
import se.magetower.reagent.Reagent
import se.magetower.spell.Spell
import java.util.*

class TowerState {

    var spells : Queue<Spell> = LinkedList<Spell>()
    var reagents : Queue<Reagent> = LinkedList<Reagent>()
    var contracts : Queue<Contract> = LinkedList<Contract>()
    var gold = 0

}