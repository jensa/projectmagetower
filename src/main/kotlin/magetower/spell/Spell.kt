package magetower.spell

import kotlinx.serialization.Serializable
import magetower.reagent.ReagentRequirement

@Serializable
class Spell(var name: String,
            var branch: MagicBranch,
            var invest: List<Int>,
            var properties: List<Pair<String, Int>>,
            var potency: Int,
            private var requirements: List<ReagentRequirement>) {

    override fun toString(): String {
        return "$name ($branch)"
    }

    fun getRequirementsString() : String {
        return requirements.groupBy { it.reagentId }.map { "${it.value.size} ${it.value[0].reagentName}" }.joinToString(", ")
    }

    fun getRequirements() : List<ReagentRequirement> {
        return requirements
    }


}