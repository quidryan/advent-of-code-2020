fun main(args: Array<String>) {
    day16_1()
    day16_2()
}

fun day16_1() {
    val day16 = loadDay16("day16_input.txt")

    val total = day16.nearby.fold(0) { acc, ticket ->
        acc + ticket.filter { value ->
            // Find invalid
            !day16.isValid(value)
        }.sum()
    }

    println("Total of all errors is $total")
}
fun day16_2() {
    val day16 = loadDay16("day16_input.txt")

    val tickets = day16.nearby.filter { ticket ->
        ticket.all { value ->
            day16.isValid(value)
        }
    }

    println("Valid tickets: $tickets")

    val exclusions = mutableMapOf<String, MutableSet<Int>>()
    tickets.forEach { ticket ->
        print(" Ticket: ")
        var specialCase: String = ""
        ticket.forEachIndexed { idx, field ->
            val possibleRules = day16.possibleRules(field)
            val impossibleRules = day16.rules subtract possibleRules
            impossibleRules.forEach { rule ->
                exclusions.getOrPut(rule.name) { mutableSetOf() }.add(idx)
            }
            print(possibleRules.size.toString().padEnd(3))
            if (possibleRules.size != 20) {
                specialCase = impossibleRules.first().name
            }
        }
        println(" [$specialCase]")
    }

    val fieldsToSolve = (0..19).toMutableSet()
    val votes = exclusions.map { (key, value) ->
        key to (fieldsToSolve subtract value).toMutableSet()
    }.toMutableSet()

    val answers = mutableMapOf<String, Int>()
    while(votes.isNotEmpty()) {
        val (answerName, answerField) = votes.find { (key, possible) ->
            possible.size == 1
        }!!
        val answerNumber = answerField.first()
        answers[answerName] = answerNumber
        println ("Found that $answerName can only be $answerNumber")
        votes.removeIf { (key, possible) -> key == answerName }
        // Remove from all
        votes.forEach { (key, possible) ->
            possible.remove(answerNumber)
        }
    }

    val destinationFields = answers.filterKeys { it.startsWith("departure") }
    destinationFields.forEach { (key, field) ->
        println ("$key is field $field")
    }
    val multiplied = destinationFields.values.fold(1L) { acc, i ->
        acc * day16.myticket.get(i)
    }
    println("Found matches, when multiplied becomes $multiplied")
}

class Day16(val rules: Set<TicketRule>, val myticket: List<Int>, val nearby: List<List<Int>>) {
    fun isValid(value:Int) : Boolean {
        return rules.any { rule ->
            val matchingFields = rule.ranges.filter {
                it.contains(value)
            }
            matchingFields.isNotEmpty()
        }
    }
    fun possibleRules(value:Int) : Set<TicketRule> {
        return rules.filter { rule ->
            val matchingFields = rule.ranges.filter {
                it.contains(value)
            }
            matchingFields.isNotEmpty()
        }.toSet()
    }
}

data class TicketRule(val name:String, val ranges:List<IntRange>) {
}

fun loadDay16(filename: String) : Day16 {
    var readingRules = true
    var readingMyTicket = false
    val ruleRegex = Regex("([\\w\\s]+):\\s?(\\d+)-(\\d+) or (\\d+)-(\\d+)")
    val rules = mutableSetOf<TicketRule>()
    val fields = mutableListOf<List<Int>>()
    var myticket: List<Int> = emptyList()
    getResourceAsText(filename).bufferedReader().forEachLine { line ->
        if(readingRules) {
            if (line.isEmpty()) {
                readingRules = false
                readingMyTicket = true
            } else {
                val (name, rule1start, rule1end, rule2start, rule2end) = ruleRegex.matchEntire(line)!!.destructured
                val rule = TicketRule(name, listOf(
                    IntRange(rule1start.toInt(), rule1end.toInt()),
                    IntRange(rule2start.toInt(), rule2end.toInt())))

                rules.add(rule)
            }
        } else if(readingMyTicket) {
            if (line.isEmpty()) {
                readingRules = false
                readingMyTicket = false
            } else if(line == "your ticket:"){
                // ignore
            } else {
                myticket = line.split(',').map { it.toInt() }
            }
        } else {
            if(line == "nearby tickets:") {
                // starting
            } else {
                fields.add(line.split(',').map { it.toInt() })
            }
        }
    }

    return Day16(rules, myticket, fields)
}