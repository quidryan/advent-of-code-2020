import java.lang.IllegalStateException
import kotlin.system.measureTimeMillis

fun main(args: Array<String>) {
    //day13_1()
    day13_2()
}

fun day13_1() {
    val reader = getResourceAsText("day13_input.txt").bufferedReader()
    val departure = reader.readLine().toInt()
    val buses = reader.readLine().split(",")
        .filter { it != "x" }
        .map { it.toInt() }
        .map { busId -> busId to ((departure / busId) + 1) * busId }

    val (bus, time) = buses.minByOrNull { it.second }!!

    println("We need to leave at $departure and the best bus is $bus at $time: " + bus*(time-departure))
}

fun day13_2() {

    assertBus("7,13,x,x,59,x,31,19", 1068781L)
    assertBus("17,x,13,19",3417L)
    assertBus("67,7,59,61",754018L)
    assertBus("67,x,7,59,61",779210L)
    assertBus("67,7,x,59,61",1261476L)
    assertBus("1789,37,47,1889",1202161486L)

    val reader = getResourceAsText("day13_input.txt").bufferedReader()
    val departure = reader.readLine().toInt() // ignore
    val buses = reader.readLine().split(',')
    val found = findSubsequent(buses)

    println (" Found that the first time is $found")
}

fun assertBus(busIds:String, expected:Long) {
    val timeInMillis = measureTimeMillis {
        val t = findSubsequent(busIds.split(','))
        if( t != expected) {
            println ("We expected $expected but got $t for input $busIds, we were off by ${t-expected}")
            //throw IllegalStateException("We expected $expected but got $t for input $busIds")
        }
    }
    println ("Searching $busIds took ${timeInMillis/1000} seconds" )
}
fun findSubsequent(busStrings: List<String>) : Long {
    val buses = busStrings
        .mapIndexed { idx, busId -> (if(busId=="x") 1 else busId.toInt()) to idx}

    var t = 0L;
    while(true) {
        t++
        val alignment = buses.all { (bus, idx) ->
            (t+idx) % bus == 0L
        }
        if(alignment) {
            return t
        }
    }
}