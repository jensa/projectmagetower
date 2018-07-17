package magetower

import kotlinx.serialization.json.JSON
import java.io.File


class Main(val name : String) {
    fun greet() {
        println("Hello, ${name}");
    }
}

fun main(args : Array<String>) {
    val file = File("state.json")
    val stateString = if(file.exists()) file.readText() else ""
    val state = if(stateString.isBlank()) TowerState() else JSON.parse(stateString)
    Game(state).loop()
}