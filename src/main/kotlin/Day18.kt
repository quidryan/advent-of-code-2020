fun main(args: Array<String>) {
//    day18_1()
    day18_2()
}

fun day18_1() {
    assertEquals("1 + 2 * 3 + 4 * 5 + 6", 71)
    assertEquals("1 + (2 * 3) + (4 * (5 + 6))", 51)
    assertEquals("2 * 3 + (4 * 5)", 26)
    assertEquals("5 + (8 * 3 + 9 + 3 * 4 * 3)", 437)
    assertEquals("5 * 9 * (7 * 3 * 3 + 9 * 3 + (8 + 6 * 4))", 12240)
    assertEquals("((2 + 4 * 9) * (6 + 9 * 8 + 6) + 6) + 2 + 4 * 2", 13632)
    assertEquals("(2 * (9 + 3 + 8 * 8) + 6) * 3 * 9 * (7 + 7) * (2 * 2) * 5", 2464560)
    assertEquals("(5 + 3 + (4 + 8 * 6 + 6 + 6) + 4 * 6) * (3 + 6 * (9 + 3 + 6) + 7 + 4 * 3) * 3 * (2 + 5 * 2 + (6 + 5 + 8 * 9 * 8 * 3) + 2 * 6)", 22_169_687_040L) //  added up to 22_169_687_040
    assertEquals("(9 + 3 * 9) + (4 * 9 + 7 + 2) + 7 + (7 * (3 + 4 + 8 * 9) + 8) + 9", 1122)

    val added = getResourceAsText("day18_input.txt").bufferedReader()
        .readLines()
        .map { Day18.parseDay18(it).sum() }
        .sum()
    println("total: $added")
}

fun day18_2() {
    assertEquals2("1 + 2 * 3 + 4 * 5 + 6", 231)
    assertEquals2("1 + (2 * 3) + (4 * (5 + 6))", 51)
    assertEquals2("2 * 3 + (4 * 5)", 46)
    assertEquals2("5 + (8 * 3 + 9 + 3 * 4 * 3)", 1445)
    assertEquals2("5 * 9 * (7 * 3 * 3 + 9 * 3 + (8 + 6 * 4))", 669060)
    assertEquals2("((2 + 4 * 9) * (6 + 9 * 8 + 6) + 6) + 2 + 4 * 2", 23340)

    val added = getResourceAsText("day18_input.txt").bufferedReader()
        .readLines()
        .map { Day18.parseDay18(it).sum2() }
        .sum()
    println("total: $added")
}

fun assertEquals(line:String, expected: Long) {
    println("Trying $line")
    val sum = Day18.parseDay18(line).sum()
    if( sum != expected) {
        throw IllegalStateException("Did not get total of $expected from $line, but instead $sum")
    } else {
        println("Solved $line")
    }
}

fun assertEquals2(line:String, expected: Long) {
    println("Trying $line")
    val sum = Day18.parseDay18(line).sum2()
    if( sum != expected) {
        throw IllegalStateException("Did not get total of $expected from $line, but instead $sum")
    } else {
        println("Solved $line")
    }
}

interface Syntax
interface Arg: Syntax {
    fun toLong(): Long
    fun toLong2(): Long
}

class ValueArg(val i: Long) : Arg {
    override fun toLong(): Long {
        return i
    }
    override fun toLong2(): Long {
        return i
    }

    override fun toString(): String {
        return "$i"
    }
}

class Parens(private val stack: List<Syntax>) : Arg {
    override fun toLong(): Long {
        var prev: Arg? = null
        var prevOp: Op? = null
        for(syntax in stack) {
            if(syntax is Op) {
                prevOp = syntax
            } else if (syntax is Arg) {
                if(prev == null) {
                    prev = syntax
                } else {
                    if(prevOp == null) {
                        throw IllegalStateException("We should have an op when ever we have a secoond arg")
                    }
                    // We have previous arg, the op and now a second arg
                    val calculated = prevOp.doop(prev, syntax)
                    prev = ValueArg(calculated)
                }
            }
        }
        return prev!!.toLong()
    }

    override fun toLong2(): Long {
        val ops = stack.toMutableList()
        // Scan for Adds first, then evaluate
        var found = ops.indexOfFirst { it is AddOp }
        while(found != -1) {
            val arg1 = ops.removeAt(found - 1) as Arg
            val op = ops.removeAt(found - 1) as AddOp
            val arg2 = ops.removeAt(found - 1) as Arg
            val replacement = ValueArg(op.doop2(arg1, arg2))
            ops.add(found - 1, replacement)
            found = ops.indexOfFirst { it is AddOp }
        }

        var prev: Arg? = null
        var prevOp: Op? = null
        for(syntax in ops) {
            if(syntax is Op) {
                prevOp = syntax
            } else if (syntax is Arg) {
                if(prev == null) {
                    prev = syntax
                } else {
                    if(prevOp == null) {
                        throw IllegalStateException("We should have an op when ever we have a secoond arg")
                    }
                    // We have previous arg, the op and now a second arg
                    val calculated = prevOp.doop2(prev, syntax)
                    prev = ValueArg(calculated)
                }
            }
        }
        return prev!!.toLong()
    }

    override fun toString(): String {
        return stack.fold("(") { acc, syntax ->
            acc + syntax.toString()
        } + ")"
    }
}

interface Op: Syntax {
    fun doop(arg1: Arg, arg2: Arg): Long
    fun doop2(arg1: Arg, arg2: Arg): Long
}

class AddOp : Op {
    override fun doop(arg1: Arg, arg2: Arg): Long {
        return arg1.toLong() + arg2.toLong()
    }

    override fun doop2(arg1: Arg, arg2: Arg): Long {
        return arg1.toLong2() + arg2.toLong2()
    }

    override fun toString(): String {
        return " + "
    }
}

class MultiplyOp : Op {
    override fun doop(arg1: Arg, arg2: Arg): Long {
        return arg1.toLong() * arg2.toLong()
    }
    override fun doop2(arg1: Arg, arg2: Arg): Long {
        return arg1.toLong2() * arg2.toLong2()
    }
    override fun toString(): String {
        return " * "
    }

}

class Day18(val originalLine:String, val stack: List<Syntax>) {

    companion object {
        fun parseDay18(line: String): Day18 {
            return Day18(line, parse(line).first)
        }

        fun parse(line: String, start: Int = 0): Pair<List<Syntax>,Int> {
            val chars = line.toCharArray()
            val ourStack = mutableListOf<Syntax>()
            var chIdx: Int = start
            while(chIdx < chars.size) {
                val ch = chars[chIdx]
                when (ch) {
                    '(' -> {
                        // it will run until the end parens
                        val (acc, ended) = parse(line, chIdx + 1)
                        ourStack.add(Parens(acc))
                        chIdx = ended
                    }
                    ')' -> {
                        return Pair(ourStack, chIdx)
                    }
                    ' ' -> {
                    }
                    '+' -> {
                        ourStack.add(AddOp())
                    }
                    '*' -> {
                        ourStack.add(MultiplyOp())
                    }
                    else -> {
                        ourStack.add(ValueArg(Character.getNumericValue(ch).toLong()))
                    }
                }
                chIdx++
            }
            return Pair(ourStack, chIdx)
        }
    }

    fun sum() : Long {
        val init = Parens(stack)
        val added = init.toLong()
        val toString = init.toString()
        val cleaned = toString.slice(1..toString.length-2)
        if(cleaned != originalLine) {
            throw IllegalStateException("\n$cleaned vs \n$originalLine}")
        }
        println("$init = $added")
        return added
    }

    fun sum2() : Long {
        val init = Parens(stack)
        return init.toLong2()
    }

}