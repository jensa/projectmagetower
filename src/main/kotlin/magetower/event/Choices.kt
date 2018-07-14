package magetower.event

import magetower.action.Choice

fun yesNoChoice(text : String) : Choice {
    return Choice("$text\n" +
            listOf("Yes", "No").mapIndexed { i, s -> "$i. $s" }.joinToString("\n"), Choice.InputType.NUMBER)
}

fun listChoiceText(text : String, options :List<Any>) : Choice {
    return Choice("$text\n" + options.mapIndexed { i, option -> "$i. $option" }.joinToString("\n"),Choice.InputType.TEXT)
}

fun listChoice(text : String, options :List<Any>) : Choice {
    return Choice("$text\n" + options.mapIndexed { i, option -> "$i. $option" }.joinToString("\n"),Choice.InputType.NUMBER)
}

fun listChoicePlusContinue(text : String, options :List<Any>) : Choice {
    return Choice("$text\n" + options.plus("Continue").mapIndexed { i, option -> "$i. $option" }.joinToString("\n"),Choice.InputType.NUMBER)
}

fun listChoiceWithFinish(text : String, options :List<Any>) : Choice {
    return Choice("$text\n" + options.plus("Finish").mapIndexed { i, option -> "$i. $option" }.joinToString("\n"),Choice.InputType.NUMBER)
}