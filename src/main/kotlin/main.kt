import java.io.InputStream
import java.math.BigDecimal
import java.util.*

fun main(args: Array<String>) {
    day1_1()
    day1_2()
    day2_1()
    day2_2()
    day3_1()
    day3_2()
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

class Forest(val width: Int, val height: Int, val map: Array<CharArray>) {

    fun isTree(row: Int, col: Int): Boolean = map[row][col%width] == '#'

    fun sled(colStep: Int, rowStep: Int): Int {
        var row = 0
        var col = 0
        var trees = 0
        while ( (row+rowStep) < height ) {
            // right 3, down 1
            row += rowStep
            col += colStep
            if (isTree(row, col)) {
                trees++
            }
        }
        return trees
    }
}

fun createMap(rows: List<String>):Forest {
    val map = rows.map { it.toCharArray() }.toTypedArray()
    return Forest(map[0].size, map.size, map)
}

fun day3_1() {
    val forest = createMap(getResourceAsText("day3_input.txt").bufferedReader().readLines())
    var trees = forest.sled(3, 1)
    println("Day 3_1: Number of trees: $trees")

}

fun day3_2() {
    val forest = createMap(getResourceAsText("day3_input.txt").bufferedReader().readLines())
    val paths =
        listOf(forest.sled(1, 1),
                forest.sled(3, 1),
                forest.sled(5, 1),
                forest.sled(7, 1),
                forest.sled(1, 2))
            .map { it.toLong() }
    val trees = paths.reduce { acc, elem -> acc * elem }

    println("Day 3_2: Number of trees: " + trees)
}