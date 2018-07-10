package magetower.spell

open class MagicBranch(var name : String,
                       var id : String,
                       var properties: Map<String,Int>) {

    override fun toString(): String {
        return name
    }
}