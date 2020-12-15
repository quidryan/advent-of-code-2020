
fun main(args: Array<String>) {
    //day15_1()
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
    playGameAndAssert("0,3,6", "175594", 30_000_000)
    playGameAndAssert("1,3,2", "2578", 30_000_000)
    playGameAndAssert("2,1,3", "3544142", 30_000_000)
    playGameAndAssert("1,2,3", "261214", 30_000_000)
    playGameAndAssert("2,3,1", "6895259", 30_000_000)
    playGameAndAssert("3,2,1", "18", 30_000_000)
    playGameAndAssert("3,1,2", "362", 30_000_000)

    val input = "12,1,16,3,11,0"
    val answer = playGame(input, 30_000_000)
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

    val lastSeen = mutableMapOf<Int, Pair<Int, Int>>()
    initial.forEachIndexed { idx, value ->
        lastSeen[value] = Pair(idx, -1)
        //println("Turn ${idx+1}: $value")
    }

    // turn is the game version of turn, not the index in list
    var lastValue = initial.last()
    for (turn in initial.size until whenToEnd) {
        // Look up the last two times it's been seen
        val previouslySpoken = lastSeen.getOrDefault(lastValue, Pair(-1, -1))

        // The first reflects the what we just set.
        // The second reflects the previous time we set it
        lastValue = if (previouslySpoken.second == -1) { // Only spoken once
            0
        } else {
            (turn - 1) - previouslySpoken.second
        }

        val updatePreviousVisits = lastSeen.getOrDefault(lastValue, Pair(-1, -1))
        lastSeen[lastValue] = Pair(turn, updatePreviousVisits.first) // Move indexes over
        //println("Turn ${turn+1}: $lastValue")
    }

    println("Last element is $lastValue")
    return lastValue
}

