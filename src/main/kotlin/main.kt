import java.io.InputStream

fun main(args: Array<String>) {
    day1_1()
    day1_2()
    day2_1()
    day2_2()
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

class Policy(val min: Int, val max: Int, val letter: Char) {
    fun complies(password: String): Boolean {
        val count = password.count { it == letter }
        return count in min..max
    }
    fun compliesToboggan(password: String): Boolean {
        val matchesFirst = password[min-1] == letter
        val matchesSecond = password[max-1] == letter
        return matchesFirst xor matchesSecond
    }
}

fun createPolicy(policyStr:String ):Policy {
    val match = Regex("(\\d+)-(\\d+) ([a-z])").find(policyStr)!!
    val (min, max, letter) = match.destructured
    return Policy(min.toInt(), max.toInt(), letter[0])
}

fun day2_1() {
    val passwords = getResourceAsText("day2_input.txt").bufferedReader().readLines()
        .map { it.split(":").toTypedArray() }
        .map { parts -> Pair(createPolicy(parts[0]), parts[1].trim()) }
        .filter { it.first.complies(it.second) }
        .count()

    println("Found unconforming password: " + passwords)
}

fun day2_2() {
    val passwords = getResourceAsText("day2_input.txt").bufferedReader().readLines()
        .map { it.split(":").toTypedArray() }
        .map { parts -> Pair(createPolicy(parts[0]), parts[1].trim()) }
        .filter { it.first.compliesToboggan(it.second) }
        .count()

    println("Found unconforming Toboggan password: " + passwords)
}