package magetower

import magetower.Game


class Main(val name : String) {
    fun greet() {
        println("Hello, ${name}");
    }
}

fun main(args : Array<String>) {
    Game().loop()
}