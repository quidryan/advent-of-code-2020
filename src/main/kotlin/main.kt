import java.io.InputStream

fun main(args: Array<String>) {
    day1_1()
    day1_2()
}

fun getResourceAsText(path: String): InputStream {
    return object {}.javaClass.getResourceAsStream(path)
}

fun day1_1() {
    val expenses = getResourceAsText("day1_input.txt").bufferedReader().readLines().map { it.toInt() }
    val match = expenses
        .flatMap { left -> expenses.map { right -> Triple(left, right, left + right) } }
        .first { it.third == 2020 }
    println("Answer for Day 1:" + (match.first * match.second))
}

data class Addition(val t1: Int, val t2: Int, val t3: Int, val sum: Int)

fun day1_2() {
    val expenses = getResourceAsText("day1_input.txt").bufferedReader().readLines().map { it.toInt() }
    val match = expenses
        .flatMap { left -> expenses.map { right -> Pair(left, right) } }
        .flatMap { left -> expenses.map { right -> Addition(left.first, left.second, right, left.first + left.second + right) } }
        .first { it.sum == 2020 }
    println("Answer for Day 1 part 2:" + (match))
    println(match.t1 * match.t2 * match.t3)
}