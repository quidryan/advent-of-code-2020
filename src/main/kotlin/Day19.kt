import kotlin.math.max

fun main(args: Array<String>) {
    day19_1()
    day19_2()
}


fun day19_1() {
    assertEqualsDay19("day19_sample.txt", 2)
    assertEqualsDay19("day19_sample2.txt", 2)

    val validMessages = Day19.parse("day19_input.txt").validate()
    println("Found ${validMessages.size} valid messages")
}

fun day19_2() {

    assertEqualsDay19("day19_sample3.txt", 3)
    assertEqualsDay19("day19_sample4.txt", 12)

    val day19 = Day19.parse("day19_input2.txt")
    val validMessages = day19.validate()
    println("Found ${validMessages.size} valid messages")
}

fun assertEqualsDay19(filename: String, expected: Int) {
    println ("Looking at $filename")
    val validMessages = Day19.parse(filename).validate()
    if(validMessages.size != expected) {
        throw IllegalStateException("$filename did not have $expected, but had ${validMessages.size}")
    }
    println("Able to match $filename")
}

class Day19(val rules: Map<Int, Rule>, val messages: List<CharArray>) {
    interface Rule
    data class ValueRule(val id:Int, val ch: Char) : Rule
    data class SimpleRule(val id:Int, val refs: List<Int>) : Rule
    data class OrRule(val id:Int, val rule1: SimpleRule, val rule2: SimpleRule) : Rule

    fun validate() : List<CharArray> {
        return messages.filter { message ->
            val line = message.contentToString()
            val matched = walk(message.toList(), listOf(rules[0]!!))
            println("$line matched up to $matched")
            matched
        }
    }

    /**
     * @return Max Match
     */
    fun walk(message:List<Char>, rulesToMatch: List<Rule>) : Boolean {
        if(message.isEmpty()) {
            // Both line and rules are empty, to produce a true
            return rulesToMatch.isEmpty()
        } else if(rulesToMatch.isEmpty()){
            // No rules but we have more line to match
            return false
        }
        // Process first rule.
        // Routes to match, any will work
        val remainingRules = rulesToMatch.drop(1)
        when(val rule = rulesToMatch[0]) {
            is ValueRule -> {
                return if (rule.ch == message[0]) {
                    walk(message.drop(1), remainingRules)
                } else {
                    false
                }
            }
            is SimpleRule -> {
                val innerRules = rule.refs.map { refId -> rules[refId]!! }
                return walk(message, innerRules + remainingRules)
            }
            is OrRule -> {
                val left = walk(message, rule.rule1.refs.map { rules[it]!! } + remainingRules)
                val right = walk(message, rule.rule2.refs.map { rules[it]!! } + remainingRules)
                return left || right
            }
            else -> throw IllegalStateException("Should have matched and returned value")
        }
    }

    companion object {
        fun parse(filename: String) : Day19 {
            val valueRule = Regex("(\\d+): \"(\\w)\"")
            val singleRule = Regex("(\\d+): (\\d+)")
            val simpleRule = Regex("(\\d+): (\\d+) (\\d+)")
            val orRule = Regex("(\\d+):(.*)\\|(.*)")

            var readingRules = true
            val rules = mutableMapOf<Int, Rule>()
            val messages = mutableListOf<CharArray>()

            getResourceAsText(filename).bufferedReader().lines().forEach { line ->
                operator fun Regex.contains(text: CharSequence): Boolean = this.matches(text)

                if(line.isEmpty()) {
                    readingRules = false
                } else if(readingRules) {
                    when (line) {
                        in valueRule -> {
                            val (ruleId, ch) = valueRule.matchEntire(line)!!.destructured
                            val id = ruleId.toInt()
                            rules[id] = ValueRule(id, ch.first())
                        }
                        in singleRule -> {
                            val (ruleId, arg1) = singleRule.matchEntire(line)!!.destructured
                            val id = ruleId.toInt()
                            rules[id] = SimpleRule(id, listOf(arg1.toInt()))
                        }
                        in simpleRule -> {
                            val (ruleId, arg1, arg2) = simpleRule.matchEntire(line)!!.destructured
                            val id = ruleId.toInt()
                            rules[id] = SimpleRule(id, listOf(arg1.toInt(), arg2.toInt()))
                        }
                        in orRule -> {
                            val (ruleId, left, right) = orRule.matchEntire(line)!!.destructured
                            val id = ruleId.toInt()
                            rules[id] = OrRule(id,
                                SimpleRule(id, left.trim().split(' ').map { arg -> arg.trim().toInt() }),
                                SimpleRule(id, right.trim().split(' ').map { arg -> arg.trim().toInt() })
                            )
                        }
                        else -> {
                            throw IllegalStateException("Line \"$line\" did not match any rules")
                        }
                    }
                } else {
                    messages.add(line.toCharArray())
                }
            }
            return Day19(rules, messages)
        }
    }
}