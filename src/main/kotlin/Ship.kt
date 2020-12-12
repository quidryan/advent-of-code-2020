import kotlin.math.absoluteValue

data class Location(val x: Int, val y: Int, val dir: Direction, val waypoint: Pair<Int, Int> = Pair(0,0)) {
    fun manhattan() : Int = x.absoluteValue+y.absoluteValue
}

enum class Direction(val shorthand: Char, val clockhand: Int, val dx: Int, val dy: Int) {
    North( 'N',0,0,1 ),
    West( 'W', 270,-1,0 ),
    South( 'S', 180,0,-1 ),
    East( 'E', 90, 1, 0)
}

class Ship {

    val cardinal = mapOf('N' to Direction.North, 'S' to Direction.South, 'W' to Direction.West, 'E' to Direction.East)
    val clockhandMap = Direction.values().map { it.clockhand to it }.toMap()

    fun turn(location: Location, hand: Char, magnitude: Int ) : Location {
        val vector = magnitude * if (hand == 'L') -1 else 1
        val angle = (location.dir.clockhand + vector + 360) % 360
        val newDirection = clockhandMap[angle]!!
        return Location(location.x, location.y, newDirection)
    }

    fun move(location: Location, dirStr: Char, magnitude: Int) : Location {
        return move(location, cardinal[dirStr]!!, magnitude)
    }

    fun move(location: Location, direction: Direction, magnitude: Int) : Location {
        return Location( location.x + direction.dx*magnitude, location.y + direction.dy*magnitude, location.dir)
    }

    fun moveWaypoint(location: Location, dirStr: Char, magnitude: Int) : Location {
        return moveWaypoint(location, cardinal[dirStr]!!, magnitude)
    }
    fun moveWaypoint(location: Location, direction: Direction, magnitude: Int) : Location {
        return Location(
            location.x,
            location.y,
            location.dir,
            Pair(location.waypoint.first + direction.dx*magnitude, location.waypoint.second + direction.dy*magnitude))
    }

    private fun rotate(location: Location, hand:Char, magnitude: Int) : Location {
        var moved = location.waypoint
        val repeat = magnitude / 90
        val delta = if(hand=='R') Pair(1,-1) else Pair(-1, 1)
        for(i in 1..repeat) {
            moved = Pair(delta.first * moved.second, delta.second * moved.first)
        }

        return Location(location.x, location.y, location.dir, moved)
    }

    private fun forward(location: Location, magnitude: Int) : Location {
        return Location(
            location.x + location.waypoint.first*magnitude,
            location.y + + location.waypoint.second*magnitude,
            location.dir,
            location.waypoint)

    }
    fun sail(starting: Location, directions: List<Pair<Char, Int>>): Location {
        return directions.fold(  starting, { location, (op, magnitude) ->
            val newLocation = when (op) {
                'N', 'S', 'W', 'E' -> move(location, op, magnitude)
                'L', 'R' -> turn(location, op, magnitude)
                else -> move(location, location.dir, magnitude)
            }
            //println("$op$magnitude -> $newLocation")
            newLocation
        })
    }

    fun sailWithWaypoint(starting: Location, directions: List<Pair<Char, Int>>): Location {
        return directions.fold( starting, { location, (op, magnitude) ->
            val newLocation = when (op) {
                'N', 'S', 'W', 'E' -> moveWaypoint(location, op, magnitude)
                'L', 'R' -> rotate(location, op, magnitude)
                else -> forward(location, magnitude)
            }
            //println("$op$magnitude -> $newLocation")
            newLocation
        })
    }
}