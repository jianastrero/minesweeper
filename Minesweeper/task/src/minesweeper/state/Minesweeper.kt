package minesweeper.state

import kotlin.math.floor

class Minesweeper(val columns: Int, val rows: Int, mineCount: Int) {

    private lateinit var _state: State

    val minePositions = Array(mineCount) { -1 }
    val cells = Array<Cell>(columns * rows) { SafeCell() }
    val marks = mutableSetOf<Int>()

    val state: State
        get() = _state

    init {
        // Show initial state
        setState(ViewState(this))
    }

    fun setState(state: State) {
        _state = state
        _state.run()
    }

    fun view() {
        setState(ViewState(this))
    }

    fun move(x: Int, y: Int, command: String) {
        setState(MoveState(this, x, y, command))
    }

    fun check() {
        setState(CheckState(this))
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
    private fun indexToPoint(index: Int): Pair<Int, Int> =
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
        (row * columns + column).coerceIn(0 until rows * columns)

    /**
     *
     * @param index The index of a point
     *
     * @return Array of indexes around the point denoted by index
     */
    fun getAround(index: Int): IntArray {
        val list = mutableListOf<Int>()
        val (pointCol, pointRow) = indexToPoint(index)

        (-1..1).forEach { row ->
            (-1..1).forEach { col ->
                if (row != 0 || col != 0) {
                    val x = pointCol + col
                    val y = pointRow + row

                    if (x in 0 until columns && y in 0 until rows) {
                        list.add(pointToIndex(x, y))
                    }
                }
            }
        }

        return list.toIntArray()
    }

    abstract class State(protected val minesweeper: Minesweeper) {

        abstract fun run()
    }

    abstract class Cell {

        protected var _representation: Char = '.'

        val representation: Char
            get() = _representation

        abstract fun explore()

        abstract fun peek(): Char
    }

    class MineCell : Cell() {

        override fun explore() {
            _representation = 'X'
        }

        override fun peek() = 'X'
    }

    class SafeCell : Cell() {

        var nearMines: Int = 0

        override fun explore() {
            _representation =
                if (nearMines > 0)
                    "$nearMines".toCharArray()[0]
                else
                    '/'
        }

        override fun peek() =
            if (nearMines > 0)
                "$nearMines".toCharArray()[0]
            else
                '/'
    }
}