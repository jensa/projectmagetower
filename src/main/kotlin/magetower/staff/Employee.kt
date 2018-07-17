package magetower.staff

import kotlinx.serialization.Serializable

@Serializable
class Employee(var name : String,
               var avaliableForNewJob : Boolean,
               var spellSkillMultiplier : Int) {

    override fun toString(): String {
        return name
    }

}