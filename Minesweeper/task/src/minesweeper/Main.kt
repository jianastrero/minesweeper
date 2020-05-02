package minesweeper

import kotlin.math.floor

fun main() {
    val minesweeper = Minesweeper(9, 9)
}

class Minesweeper(val columns: Int, val rows: Int) {

    private lateinit var _state: State
    val blocks = Array(columns * rows) {
        val (_, row) = indexToPoint(it)
        if (row % 2 == 0)
            Block.SAFE
        else
            Block.MINE
    }

    val state: State
        get() = _state

    init {
        setState(ViewState(this))
    }

    fun setState(state: State) {
        _state = state
        _state.run()
    }

    /**
     *
     * if index = 11, columns = 5, row = 5
     * then column = 1, row = 3
     *
     * if index = 0, columns = 2, row = 2
     * then column = 0, row = 0
     *
     * if index = 1, columns = 2, row = 2
     * then column = 1, row = 0
     *
     * column = (index % columns)
     * row = floor(index / columns)
     *
     * @param index The index of a point
     *
     * @return (column, row)
     */
    fun indexToPoint(index: Int): Pair<Int, Int> =
        Pair(index % columns, floor(index.toFloat() / columns).toInt())

    /**
     *
     * row * columns + column
     *
     * @param column The column of the point
     * @param row The row of the point
     *
     * @return Index of the given point
     */
    fun pointToIndex(column: Int, row: Int): Int =
        row * columns + column

    abstract class State(protected val minesweeper: Minesweeper) {

        abstract fun run()
    }

    enum class Block(val representation: Char) {
        SAFE('.'),
        MINE('X')
    }
}

class ViewState(minesweeper: Minesweeper) : Minesweeper.State(minesweeper) {

    override fun run() {
        repeat(minesweeper.rows) { row ->
            // Move through Y
            repeat(minesweeper.columns) { column ->
                // Move through X
                print(minesweeper.blocks[minesweeper.pointToIndex(column, row)].representation)
            }
            println()
        }
    }
}