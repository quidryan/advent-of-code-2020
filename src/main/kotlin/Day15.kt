
fun main(args: Array<String>) {
    day15_1()
    day15_2()
}

fun day15_1() {
    playGameAndAssert("0,3,6", "0", 10)
    playGameAndAssert("1,3,2", "1")
    playGameAndAssert("2,1,3", "10")
    playGameAndAssert("1,2,3", "27")
    playGameAndAssert("2,3,1", "78")
    playGameAndAssert("3,2,1", "438")
    playGameAndAssert("3,1,2", "1836")

    val input = "12,1,16,3,11,0"
    val answer = playGame(input, 2020)
    println("Answer at 2020 is $answer")
}

fun day15_2() {
    playGameAndAssert("0,3,6", "175594", 30000000)
    playGameAndAssert("1,3,2", "2578", 30000000)
    playGameAndAssert("2,1,3", "3544142", 30000000)
    playGameAndAssert("1,2,3", "261214", 30000000)
    playGameAndAssert("2,3,1", "6895259", 30000000)
    playGameAndAssert("3,2,1", "18", 30000000)
    playGameAndAssert("3,1,2", "362", 30000000)

    val input = "12,1,16,3,11,0"
    val answer = playGame(input, 30000000)
    println("Answer at 2020 is $answer")
}

fun playGameAndAssert(input: String, expectStr: String, whenToEnd: Int = 2020) {
    val last = playGame(input, whenToEnd)
    val expect = expectStr.toInt()
    if(last != expect) {
        throw IllegalStateException("Last $last was not expected $expect")
    }
}

private fun playGame(input: String, whenToEnd: Int): Int {
    val initial = input.split(',').map { it.toInt() }.toMutableList()

    initial.forEachIndexed { idx, value ->
        //println("Turn $idx: ${value}")
    }
    // turn is the game version of turn, not the index in list
    for (turn in initial.size+1..whenToEnd) {
        val previousTurn = turn - 1
        val recentIndex = previousTurn - 1
        val recent = initial[recentIndex]
        val hasBeenSpoken = initial.filter { it == recent }
        if (hasBeenSpoken.size == 1) { // Only spoken once
            initial.add(0)
        } else {
            val previouslySpoken = initial.slice(0 until recentIndex).lastIndexOf(recent) + 1 // convert to game turns.
            initial.add(previousTurn - previouslySpoken)
        }
        //println("Turn $turn: ${initial.last()}")
    }

    val last = initial.last()
    println("Last element is $last")
    return last
}

