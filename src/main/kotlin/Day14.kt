import java.lang.IllegalStateException

fun main(args: Array<String>) {
    day14_1()
    day14_2()
}

interface Operation

data class Mask(val onesMask: Long, val zerosMask: Long, val floatMask: Long) : Operation

data class MemorySet(val location: Long, val value: Long) : Operation

class MemoryState(var mask: Mask? = null, val memory: MutableMap<Long, Long> = mutableMapOf())

fun day14_1() {
    val filename = "day14_input.txt"
    val operations = loadOperations(filename)
    val state = executeV1(operations)

    val total = state.memory.values.sum()

    println ("Total: $total")
}

fun day14_2() {

    val filename = "day14_input.txt"
    val operations = loadOperations(filename)
    val state = executeV2(operations)

    val total = state.memory.values.sum()

    println ("Total: $total")
}

private fun loadOperations(
    filename: String
): List<Operation> {
    val maskRegex = Regex("mask = ([X01]{36})")
    val memRegex = Regex("mem\\[([0-9]*)\\] = ([0-9]*)")

    val operations = getResourceAsText(filename).bufferedReader()
        .readLines()
        .map { line ->
            val matchedMask = maskRegex.matchEntire(line)
            val matchesMem = memRegex.matchEntire(line)
            when {
                matchedMask != null -> {
                    val (maskStr) = matchedMask.destructured
                    val charArray = maskStr.toCharArray()
                    var onesMask = 0L
                    var zerosMask = 0L
                    var floatMask = 0L
                    charArray.forEachIndexed { index, c ->
                        if (c == '1') {
                            onesMask = onesMask or 1
                        }
                        if (c == '0') {
                            zerosMask = zerosMask or 1
                        }
                        if( c == 'X') {
                            floatMask = floatMask or 1
                        }
                        if (index != charArray.lastIndex) {
                            onesMask = onesMask shl 1
                            zerosMask = zerosMask shl 1
                            floatMask = floatMask shl 1
                        }
                    }
                    Mask(onesMask, zerosMask, floatMask)
                }
                matchesMem != null -> {
                    val (memLocation, memValue) = matchesMem.destructured
                    MemorySet(memLocation.toLong(), memValue.toLong())
                }
                else -> {
                    throw IllegalStateException("Unrecognized instruction $line")
                }
            }
        }
    return operations
}

private fun executeV1(operations: List<Operation>): MemoryState {
    val state = operations.fold(MemoryState()) { state, operation ->
        when (operation) {
            is Mask -> {
                state.mask = operation
            }
            is MemorySet -> {
                // Apply mask, set value
                val mask = state.mask ?: throw IllegalStateException("Can't apply mask if it's not set yet")
                val memoryValue = operation.value or mask.onesMask and mask.zerosMask.inv()
                state.memory[operation.location] = memoryValue
            }
        }
        state
    }
    return state
}

private fun executeV2(operations: List<Operation>): MemoryState {
    return operations.fold(MemoryState()) { state, operation ->
        when (operation) {
            is Mask -> state.mask = operation
            is MemorySet -> {
                val masks = state.mask ?: throw IllegalStateException("Can't apply mask if it's not set yet")
                val base = operation.location or masks.onesMask

                // Find floating addresses
                (0 until 36)
                    .filter {  masks.floatMask shr it and 1L == 1L }
                    .fold(listOf(base)) { candidates, position ->
                        candidates.flatMap { candidate ->
                            // At this location, we need to vary in two ways, which will double the candidates accumulator
                            val field = 1L shl position
                            listOf(candidate or field, candidate and field.inv())
                        }
                    }
                    .forEach { state.memory[it] = operation.value }
            }
        }
        state
    }
}