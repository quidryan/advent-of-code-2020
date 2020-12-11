
interface Cell {
    val isSeat: Boolean
    var isOccupied : Boolean
    fun visual() : Char
    fun evolve() : Cell
    fun evolveVisible(seats: List<Cell>) : Cell
}

class Floor() : Cell {
    override val isSeat = false
    override var isOccupied = false
    override fun visual(): Char { return '.' }
    override fun evolve(): Cell {
        return this
    }
    override fun evolveVisible(seats: List<Cell>): Cell {
        return this
    }
}

class Chair(override var isOccupied: Boolean) : Cell {
    override val isSeat = true
    override fun visual(): Char { return if(isOccupied) '#' else 'L' }
    var neighbors: List<Cell> = listOf()
    var neighborsVisible: List<Cell> = listOf()

    override fun evolve() : Cell {
        val occupiedNeighbors = neighbors.map { it.isOccupied }.count { it }
        return if (!isOccupied && occupiedNeighbors == 0) {
            Chair(true)
        } else if(isOccupied && occupiedNeighbors >= 4) {
            Chair(false)
        } else {
            Chair(isOccupied)
        }
    }

    override fun evolveVisible(seats: List<Cell>) : Cell {
        val occupiedNeighbors = neighborsVisible.map { it.isOccupied }.count { it }
        return if (!isOccupied && occupiedNeighbors == 0) {
            Chair(true)
        } else if(isOccupied && occupiedNeighbors >= 5) {
            Chair(false)
        } else {
            Chair(isOccupied)
        }
    }
}

class Conway(val width: Int, val height: Int, var seats: List<Cell>) {

    init {
        updateNeighbors()
    }

    companion object {
        fun loadConway(filename: String) : Conway {
            var seats = getResourceAsText(filename).bufferedReader().readLines()
                .map { line -> line.toCharArray() }
                .map { row ->
                    row.map {
                        if(it=='L') Chair(false) else Floor()
                    }
                }

            val width = seats[0].size
            val height = seats.size

            return Conway(width, height, seats.flatten())
        }
    }
    fun evolve() : Boolean{
        val evolved = seats.map { it.evolve() }
        val change = !seatsEquals(evolved)
        seats = evolved
        updateNeighbors()

        return change
    }

    fun evolveVisible() : Boolean{
        val evolved = seats.map { it.evolveVisible(seats) }
        val change = !seatsEquals(evolved)
        seats = evolved
        updateNeighbors()

        return change
    }

    fun printSeats() {
        seats.windowed(width, step=width).forEach { row ->
            row.forEach { print(it.visual()) }
            println()
        }
        System.out.flush()
    }

    fun occupiedNext() : Int {
        return seats.count { it is Chair && it.isOccupied }
    }

    private fun seatsEquals(seats2: List<Cell>) : Boolean {
        return seats.zip(seats2).all { (left,right) ->
            left is Floor || left.isOccupied == right.isOccupied
        }
    }

    private fun updateNeighbors() {
        // Update neighbors
        seats.forEachIndexed { idx, seat ->
            if (seat is Chair) {
                val (x, y) = coordinate(idx)
                val directions = setOf(
                    Pair(-1, -1), Pair(0, -1), Pair(1, -1),
                    Pair(-1, 0), Pair(1, 0),
                    Pair(-1, 1), Pair(0, 1), Pair(1, 1),
                )
                val neighbors = directions
                    .map { (dx,dy) -> Pair(x+dx, y+dy)}
                    .filter { inRange(it) }
                    .map { look(it)}
                seat.neighbors = neighbors

                val visibleNeighbors = mutableListOf<Cell>()
                directions.forEach { (dx,dy) ->
                    for(scale in 1..width+height) { // crazy bigger than we need
                        val pointer = Pair(dx * scale + x, dy * scale + y)
                        if (!inRange(pointer)) {
                            break
                        }
                        val looking = look(pointer)
                        if(looking is Chair) {
                            visibleNeighbors.add(looking)
                            break
                        }
                    }
                }
                seat.neighborsVisible = visibleNeighbors
            }
        }
    }

    private fun look( coord: Pair<Int, Int>) : Cell {
        val (x,y) = coord
        return seats[y * width + x]
    }

    private fun inRange( coord: Pair<Int, Int>) : Boolean {
        val widthRange = 0 until width
        val heightRange = 0 until height
        val (x,y) = coord
        return widthRange.contains(x) && heightRange.contains(y)
    }

    private fun coordinate(idx: Int): Pair<Int, Int> {
        val x = idx % width
        val y = idx / width
        return Pair(x, y)
    }
}