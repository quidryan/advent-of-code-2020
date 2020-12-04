import java.io.InputStream

fun main(args: Array<String>) {
    day1_1()
    day1_2()
    day2_1()
    day2_2()
    day3_1()
    day3_2()
    day4_1()
    day4_2()
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