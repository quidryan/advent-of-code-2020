import java.io.InputStream
import kotlin.math.absoluteValue

fun main(args: Array<String>) {
//    day1_1()
//    day1_2()
//    day2_1()
//    day2_2()
//    day3_1()
//    day3_2()
//    day4_1()
//    day4_2()
//    day5_1()
//    day5_2()
//    day6_1()
//    day6_2()
//    day7_1()
//    day7_2()
//    day8_1()
//    day8_2()
//    day9_1()
//    day9_2()
//    day10_1()
//    day10_2()
//    day11_1()
//    day11_2()
    day12_1()
    day12_2()
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
    println("Answer for Day 1 part 2: " + match.t1 * match.t2 * match.t3)
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

    println("Found nonconforming password: $passwords")
}

fun day2_2() {
    val passwords = getResourceAsText("day2_input.txt").bufferedReader().readLines()
        .map { it.split(":").toTypedArray() }
        .map { parts -> Pair(createPolicy(parts[0]), parts[1].trim()) }
        .filter { it.first.compliesToboggan(it.second) }
        .count()

    println("Found nonconforming Toboggan password: $passwords")
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
    val trees = forest.sled(3, 1)
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

    println("Day 3_2: Number of trees: $trees")
}

data class Passport(
    val byr: String?, // (Birth Year)
    val iyr: String?, //  (Issue Year)
    val eyr: String?, //  (Expiration Year)
    val hgt: String?, //  (Height)
    val hcl: String?, //  (Hair Color)
    val ecl: String?, //  (Eye Color)
    val pid: String?, //  (Passport ID)
    val cid: String?, //  (Country ID))
) {

    private val requiredFields = listOf(byr, iyr, eyr, hgt, hcl, ecl, pid)

    fun isValid() : Boolean {
        return !requiredFields.any { it == null }
    }
}

class Progress(val passports: List<Passport> = listOf(), val buf: String = "")

fun createPassport(buf:String): Passport {
    val fieldMap = buf.trim().split(" ").associate {
        val (key, value) = it.split(":")
        key to value
    }
    return Passport(
        fieldMap["byr"],
        fieldMap["iyr"],
        fieldMap["eyr"],
        fieldMap["hgt"],
        fieldMap["hcl"],
        fieldMap["ecl"],
        fieldMap["pid"],
        fieldMap["cid"]
    )
}

fun createValidPassport(buf:String): Passport {
    val fieldMap = buf.trim().split(" ").associate {
        val (key, value) = it.split(":")
        key to value
    }
    return Passport(
        if (validYear(1920, 2002, fieldMap["byr"])) fieldMap["byr"] else null,
        if (validYear(2010, 2020, fieldMap["iyr"])) fieldMap["iyr"] else null,
        if (validYear(2020, 2030, fieldMap["eyr"])) fieldMap["eyr"] else null,
        if (validHeight(fieldMap["hgt"])) fieldMap["hgt"] else null,
        if (validHairColor(fieldMap["hcl"])) fieldMap["hcl"] else null,
        if (validEyeColor(fieldMap["ecl"])) fieldMap["ecl"] else null,
        if (validID(fieldMap["pid"])) fieldMap["pid"] else null,
        fieldMap["cid"]
    )
}

fun validYear(min: Int, max:Int, value: String?) : Boolean {
    if (value.isNullOrEmpty()) return false
    val year = value.toInt()
    return (year in min..max)
}

fun validHeight(value:String?) : Boolean {
    if (value == null) return false
    val unit = value.takeLast(2)
    val num = value.dropLast(2).toIntOrNull() ?: return false
    return when (unit) {
        "cm" -> {
            num in 150..193
        }
        "in" -> {
            num in 59..76
        }
        else -> {
            false
        }
    }
}

fun validHairColor(value: String?) : Boolean {
    if( value == null) return false
    return value.matches(Regex("\\#[0-9a-f]{6}"))
}

fun validEyeColor(value: String?) : Boolean {
    return "amb blu brn gry grn hzl oth".split(" ").contains(value)
}

fun validID(value: String?) : Boolean {
    if( value == null) return false
    return value.matches(Regex("[0-9]{9}"))
}

fun day4_1() {
    val initialProgress = Progress()
    val progress = getResourceAsText("day4_input.txt").bufferedReader().readLines()
        .fold(initialProgress, { acc, line ->
            if (line != "") {
                Progress(acc.passports, acc.buf + " " + line)
            } else {
                // We have a passport, so generate it and add it too the progress
                val passports = acc.passports + createPassport(acc.buf)
                Progress(passports, "")
            }
        })
    // The left over bits, make up the last passport
    val passports = progress.passports + createPassport(progress.buf)

    val valid = passports.filter { it.isValid() }

    println("Valid passports: ${valid.size}")
}

fun day4_2() {
    val initialProgress = Progress()
    val progress = getResourceAsText("day4_input.txt").bufferedReader().readLines()
        .fold(initialProgress, { acc, line ->
            if (line != "") {
                Progress(acc.passports, acc.buf + " " + line)
            } else {
                // We have a passport, so generate it and add it too the progress
                val passports = acc.passports + createValidPassport(acc.buf)
                Progress(passports, "")
            }
        })
    // The left over bits, make up the last passport
    val passports = progress.passports + createValidPassport(progress.buf)

    val valid = passports.filter { it.isValid() }

    println("Valid passports part 2: ${valid.size}")
}

data class Seat(val row: Int, val col: Int) {
    fun id(): Int = row * 8 + col
}

fun toRow(rowStr: String) : Int {
    return rowStr.substring(0..6).fold(0, { acc, letter ->
        (acc shl 1) or if (letter=='F') 0 else 1
    })
}
fun toCol(colStr: String) : Int {
    return colStr.substring(7..9).fold(0, { acc, letter ->
        (acc shl 1) or if (letter=='L') 0 else 1
    })
}

fun day5_1() {
    val progress = getResourceAsText("day5_input.txt").bufferedReader().readLines()
        .map { Seat( toRow(it), toCol(it)) }

    val max = progress
        .maxByOrNull { it.id() }

    println ("Maximum seat id is $max with id of ${max?.id()}")
}

fun day5_2() {
    val progress = getResourceAsText("day5_input.txt").bufferedReader().readLines()
        .map { Seat( toRow(it), toCol(it)) }

    val neighbor = progress
        .sortedBy { it.id() }
        .zipWithNext()
        .find { it.first.id() + 2 == it.second.id() }!!

    val myseat = neighbor.first.id()+1
    println ("My seat is next to ${neighbor.first} and ${neighbor.second}, my seat id is $myseat")
}

fun day6_1() {
    val initial = Pair(0, setOf<Char>())
    val progress = getResourceAsText("day6_input.txt").bufferedReader().readLines()
        .fold(initial, { acc, line ->
            if (line != "") {
                Pair(acc.first, acc.second + line.toCharArray().asIterable())
            } else {
                // We have survey answers
                Pair(acc.first + acc.second.size, emptySet())
            }
        })
    val count = progress.first + progress.second.size
    println("The sum of the survey count is ${count}")

}

fun day6_2() {
    val initial = Pair<Int,Set<Char>?>(0, null)
    val progress = getResourceAsText("day6_input.txt").bufferedReader().readLines()
        .fold(initial, { acc, line ->
            if (line != "") {
                val newAnswers = line.toCharArray().toSet()
                val intersection = if(acc.second == null) newAnswers else { acc.second!! intersect newAnswers }
                Pair(acc.first, intersection)
            } else {
                // We have survey answers
                Pair(acc.first + if(acc.second==null) 0 else acc.second!!.size, null)
            }
        })
    val count = progress.first + if(progress.second==null) 0 else progress.second!!.size
    println("The sum of the survey count with unique answers is ${count}")

}

data class Rule(val quantity: Int, val color:String, val bags: List<Rule>)

fun day7_1() {
    val rules = loadRules("day7_input.txt")
    val map = reverseIndexRules(rules)

    // Search
    val matches = countMatches("shiny gold", map)
    println("The number of rules that could apply is ${matches.size}")
}

private fun countMatches(color: String, map: HashMap<String, MutableSet<Rule>>) : Set<Rule> {
    return map.getOrDefault(color, mutableSetOf() )
        .flatMap { rule -> setOf(rule) + countMatches(rule.color, map) }.toSet()
}

fun day7_2() {
    val rules = loadRules("day7_input.txt")
    val map = indexRules(rules)

    val bagCount = countBags("shiny gold", map)

    println("The number of rules that could apply is ${bagCount}")
}

private fun countBags(bagColor:String, map: Map<String, Rule>) : Int {
    val (_, _, bags) = map[bagColor]!!
    return bags
        .map { it.quantity + it.quantity * countBags(it.color, map) }
        .sum()
}

private fun reverseIndexRules(rules: List<Rule>): HashMap<String, MutableSet<Rule>> {
    // Reverse Index
    val map = HashMap<String, MutableSet<Rule>>()
    rules.forEach { rule ->
        rule.bags.forEach { bag ->
            map.getOrPut(bag.color) { mutableSetOf() }.add(rule)
        }
    }
    return map
}

private fun indexRules(rules: List<Rule>): Map<String, Rule> {
    return rules.associateBy { it.color }
}

private fun loadRules(filename: String): List<Rule> {
    val ruleFormat = Regex("(\\w+ \\w+) bags contain ((\\d \\w+ \\w+ bags?(?:, )?)+|no other bags).")
    val destFormat = Regex("(\\d) (\\w+ \\w+) bags?(?:, )?")

    val rules = getResourceAsText(filename).bufferedReader().readLines()
        .map { line ->
            val result = ruleFormat.matchEntire(line)!!
            val groupValues = result.groupValues
            val src = groupValues[1]
            val dests = if (groupValues[2] == "no other bags") {
                emptyList()
            } else {
                groupValues[2].split(",")
                    .map { desc ->
                        val (quantity, color) = destFormat.find(desc)!!.destructured
                        Rule(quantity.toInt(), color, emptyList())
                    }
            }
            Rule(1, src, dests)
        }
    return rules
}

data class Instruction(val operation:String, val argument:Int)

fun day8_1() {
    val instructions = loadInstructions("day8_input.txt")

    var ip: Int = 0
    var acc: Int = 0
    val alreadyRun = mutableSetOf<Int>()
    while(!alreadyRun.contains(ip)) {
        val instruction = instructions[ip]
        alreadyRun.add(ip)
        when (instruction.operation) {
            "acc" -> {
                acc += instruction.argument
                ip += 1
            }
            "jmp" -> {
                ip += instruction.argument
            }
            "nop" -> {
                ip += 1
            }
        }
    }

    println (" The last value in the accumulator was $acc")
}

enum class MachineState {
    IllegalOperation, InfiniteLoop, Exited
}

fun day8_2() {
    val instructions = loadInstructions("day8_input.txt")

    val rowsToModify = instructions
        .mapIndexed { index, instruction -> Pair(index, instruction)}
        .filter { it.second.operation == "jmp" || it.second.operation == "nop" }
        .map { it.first }
        .toSet()

    val acc = findRow(rowsToModify, instructions)
    println ("After searching, the final acculator was $acc")
}

private fun findRow(
    rowsToModify: Set<Int>,
    instructions: List<Instruction>
) : Int {
    for (rowToChange in rowsToModify) {
        val originalRow = instructions[rowToChange]
        val newOperation = if (originalRow.operation == "jmp") "nop" else "jmp"
        val newRow = Instruction(newOperation, originalRow.argument)
        var (acc, state) = machine(instructions, rowToChange, newRow)
        if (state == MachineState.Exited) {
            // Successful swap
            println("Successful swap at $rowToChange from $originalRow to $newRow")
            return acc
        }
    }
    throw IllegalStateException("Unable to find a row that would work")
}

private fun machine(instructions: List<Instruction>, rowToChange: Int, newRow: Instruction): Pair<Int, MachineState> {
    val end = instructions.size
    var ip: Int = 0
    var acc: Int = 0
    val alreadyRun = mutableSetOf<Int>()
    while (!alreadyRun.contains(ip) && ip != end) {
        if(ip>end) {
            return Pair(acc, MachineState.IllegalOperation)
        }
        val instruction = if(ip == rowToChange) newRow else instructions[ip]
        //println("$instruction | $acc, $ip")
        alreadyRun.add(ip)
        when (instruction.operation) {
            "acc" -> {
                acc += instruction.argument
                ip += 1
            }
            "jmp" -> {
                ip += instruction.argument
            }
            "nop" -> {
                ip += 1
            }
        }
    }

    return Pair(acc, if(ip==end) MachineState.Exited else MachineState.InfiniteLoop)
}

private fun loadInstructions(filename: String): List<Instruction> {
    val instructions = getResourceAsText(filename).bufferedReader().readLines()
        .map { line ->
            val (operation, sign, quantity) = Regex("(nop|acc|jmp) (\\-|\\+)(\\d+)").find(line)!!.destructured
            Instruction(operation, argument = quantity.toInt() * if (sign == "-") -1 else 1)
        }.toList()
    return instructions
}

fun day9_1() {
    val codes = getResourceAsText("day9_input.txt").bufferedReader().readLines()
        .map { it.toLong() }

    val invalidCode = findInvalidNumber(codes, 25)
}

private fun findInvalidNumber(codes: List<Long>, preambleGap: Int) : Long {
    for (preambleEnd in preambleGap until codes.size) {
        val preambleStart = preambleEnd - preambleGap
        val preamble = codes.slice(preambleStart until preambleEnd)
        val code = codes[preambleEnd]
        //println("Need to test $code in $preamble")

        // Test
        val hasPair = combintorialPairs(preamble).map { it.first + it.second }.any { it == code }
        if (!hasPair) {
            //println("Unable to find a pair for $code in $preamble")
            return code
        }
    }
    throw IllegalStateException("Unable to find a broken code")
}

fun combintorialPairs(arr: List<Long>) : List<Pair<Long, Long>> {
    val list= mutableListOf<Pair<Long, Long>>()

    arr.indices.forEach() {
            i -> arr.indices.minus(0..i).forEach() {
            j -> list.add(arr[i] to arr[j]) }
    }
    return list
}

fun day9_2() {
    val codes = getResourceAsText("day9_input.txt").bufferedReader().readLines()
        .map { it.toLong() }

    val invalidCode = findInvalidNumber(codes, 25)

    val segment = findMatchingSegment(codes, invalidCode)

    val min = segment.minOrNull()!!
    val max = segment.maxOrNull()!!
    val sum = min+max

    println("Combination of $min and $max is $sum")
}

private fun findMatchingSegment(codes: List<Long>, invalidCode: Long) : List<Long> {
    for (start in codes.indices) {
        // Try bigger and bigger ranges to find a total that is equal to invalidCode
        for (end in start + 1 until codes.size) {
            val segment = codes.slice(start..end)
            val segmentSum = segment.sum()
            if (segmentSum == invalidCode) {
                //println("Find a segment $segment for $invalidCode")
                return segment
            } else if (segmentSum > invalidCode) {
                // We'll never find a sum, so move on
                break
            }
        }
    }
    throw IllegalStateException("Unable to find segment for broken code")

}

fun day10_1() {
    var adaptorCodes = getResourceAsText("day10_input.txt").bufferedReader().readLines()
        .map { it.toInt() }

    val max = adaptorCodes.maxOrNull()!! + 3

    val codes = adaptorCodes + max

    var jolts = 0;
    val diffs = intArrayOf(0, 0, 0)
    while(jolts < max) {
        var found = false
        for (diff in 1..3) {
            val desired = jolts + diff
            //println (" Looking for $desired from $jolts")
            if(codes.contains(desired)) {
                //println (" Found $desired")
                diffs[diff-1] += 1
                jolts = desired
                found = true
                break
            }
        }
        if(!found) {
            throw IllegalStateException("Unable to find a compatible adaptor")
        }
    }

    println("Found ${diffs[0]} ${diffs[1]} ${diffs[2]}: " + diffs[0] * diffs[2])
}

private val cache: MutableMap<Int, Long> = mutableMapOf()

fun possiblePaths(jolts: Int, endGoal: Int, codes: List<Int>, ) : Long {
    if(jolts == endGoal) {
        return 1L
    }

    // memoize the path-count for the current adapter
    return cache.getOrPut(jolts) {
        listOf(1, 2, 3)
            .map { diff ->
                val desired = jolts + diff
                val indexOf = codes.indexOf(desired)
                if (indexOf != -1) {
                    possiblePaths(desired, endGoal, codes.slice(indexOf until codes.size))
                } else {
                    0
                }
            }.sum()
    }
}

fun day10_2() {
    var adaptorCodes = getResourceAsText("day10_input.txt").bufferedReader().readLines()
        .map { it.toInt() }
        .sorted()

    val max = adaptorCodes.maxOrNull()!! + 3

    val codes = adaptorCodes + max

    val paths = possiblePaths(0, max, codes)

    println("Found $paths possible")
}

fun day11_1() {
    val c = Conway.loadConway("day11_input.txt")

    var changed = true
    while(changed) {
        changed = c.evolve()
    }

    println ("Occupied seats: ${c.occupiedNext()}")
}

fun day11_2() {
    val c = Conway.loadConway("day11_input.txt")

    var changed = true
    while(changed) {
        changed = c.evolveVisible()
    }

    println ("Occupied seats: ${c.occupiedNext()}")

}

fun day12_1() {
    var directions = getResourceAsText("day12_input.txt").bufferedReader().readLines()
        .map { Pair(it[0], it.substring(1).toInt()) }

    val finalLocation = Ship().sail(Location( 0, 0, Direction.East), directions)

    println ("Final location is $finalLocation, manhattan distance is ${finalLocation.manhattan()}")
}

fun day12_2() {
    var directions = getResourceAsText("day12_input.txt").bufferedReader().readLines()
        .map { Pair(it[0], it.substring(1).toInt()) }

    val finalLocation = Ship().sailWithWaypoint( Location( 0, 0, Direction.East, Pair(10,1)), directions)

    println ("Final location is $finalLocation, manhattan distance is ${finalLocation.manhattan()}")
}