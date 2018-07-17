package magetower.action

fun informPlayer(text : String) {
    println(text)
}

fun listString(list : Collection<Any>) : String{
    return list.map { it.toString() }.joinToString(", ")
}

fun debug(s : String) {
    System.err.println(s)
}