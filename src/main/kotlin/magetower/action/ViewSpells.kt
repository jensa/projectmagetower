package magetower.action

import kotlinx.serialization.Serializable
import magetower.TowerState

@Serializable
class ViewSpells(var state : TowerState.TowerView) : Action(this::class.toString()) {
    var hasViewed = false

    override fun description(): String {
        return "View spells"
    }

    override fun hasSteps(): Boolean {
        return !hasViewed
    }

    override fun promptChoices(): Choice {
        hasViewed = true
        return Choice("Spells:\n${state.getResearchedSpells().map {
            val title = "${it.name}:"
            val properties = it.properties.map { "${it.first}:${it.second}" }.joinToString(", ")
            val reagents = it.getRequirementsString()
            return@map "$title\n$properties\n$reagents"
        }.joinToString("\n")}", Choice.InputType.NONE)
    }

    override fun processInput(input: ChoiceInput): ActionResult? {
        return null
    }

    override fun doAction(state: TowerState.TowerView): Action {
        return ViewSpells(state)
    }
}