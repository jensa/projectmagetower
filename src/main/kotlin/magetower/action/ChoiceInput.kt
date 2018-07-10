package magetower.action

class ChoiceInput(var input : String) {

    fun getNumber() : Int {
        return input.trim().toIntOrNull() ?: -1
    }

    fun getText() : String {
        return input
    }

    fun getYesNo() : Boolean {
        return getNumber() == 0
    }

    fun getNumberList() : List<Int> {
        return getText().split(",").map { it.toIntOrNull() }.filter { it != null }.map { it!! }
    }
}