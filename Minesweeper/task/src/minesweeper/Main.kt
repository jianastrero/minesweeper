package minesweeper

import java.util.Scanner
import kotlin.math.floor
import kotlin.random.Random

fun main() {
    val scanner = Scanner(System.`in`)

    print("How many mines do you want on the field? ")
    val mineCount = scanner.nextInt()

    val minesweeper = Minesweeper(9, 9, mineCount)

    mainLoop@ while (true) {
        while (minesweeper.state !is MarkState || !minesweeper.state.finish()) {
            print("Set/delete mines marks (x and y coordinates): ")
            val x = scanner.nextInt()
            val y = scanner.nextInt()

            minesweeper.mark(x, y)
        }
        minesweeper.view()
        minesweeper.check()

        if (minesweeper.state.finish()) {
            println("Congratulations! You found all mines!")
            break@mainLoop
        }
    }
}

class Minesweeper(val columns: Int, val rows: Int, mineCount: Int) {

    private val random = Random(System.currentTimeMillis())
    private lateinit var _state: State

    val minePositions = Array(mineCount) { it }.apply {
        val set = mutableSetOf<Int>()
        while (set.size < mineCount) {
            set.add(random.nextInt(columns * rows))
        }
        set.forEachIndexed { index, i ->
            this[index] = i
        }
    }
    val blocks = Array(columns * rows) {
        if (it in minePositions) {
            Block('X')
        } else {
            Block('.')
        }
    }.apply {
        minePositions.forEach {
            val around = getAround(it)
            around.forEach { aroundIndex ->
                this[aroundIndex].nearMines = this[aroundIndex].nearMines + 1
            }
        }
    }
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

    fun mark(x: Int, y: Int) {
        setState(MarkState(this, x, y))
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

        abstract fun finish(): Boolean
    }

    data class Block(val representation: Char) {
        var nearMines: Int = 0
    }
}

class ViewState(minesweeper: Minesweeper) : Minesweeper.State(minesweeper) {

    override fun run() {
        repeat(minesweeper.rows + 3) { row ->
            // Move through Y
            repeat(minesweeper.columns + 3) { col ->
                // Move through X
                if (col == 1 || col == minesweeper.columns + 2) {
                    print("|")
                } else if (row == 1 || row == minesweeper.rows + 2) {
                    print("-")
                } else if (row == 0) {
                    print(
                        if (col > 0)
                            col - 1
                        else
                            " "
                    )
                } else if (col == 0) {
                    print(
                        if (row > 0)
                            row - 1
                        else
                            " "
                    )
                } else {
                    val index = minesweeper.pointToIndex(col - 2, row - 2)
                    print(
                        if (minesweeper.marks.contains(index)) {
                            "*"
                        } else {
                            val block = minesweeper.blocks[index]
                            if (block.representation != 'X' && block.nearMines > 0)
                                block.nearMines
                            else
                                "."
                        }
                    )
                }
            }
            println()
        }
    }

    override fun finish() = true
}

class MarkState(minesweeper: Minesweeper, private val x: Int, private val y: Int) : Minesweeper.State(minesweeper) {

    private var finish = false

    override fun run() {
        val index = minesweeper.pointToIndex(x - 1, y - 1)
        val block = minesweeper.blocks[index]

        finish = if (block.representation != 'X' && block.nearMines > 0) {
            println("There is a number here!")
            false
        } else {
            minesweeper.marks.addOrRemove(index)
            true
        }
    }

    override fun finish() = finish
}

class CheckState(minesweeper: Minesweeper) : Minesweeper.State(minesweeper) {

    private var finish = false

    override fun run() {
        finish =
            minesweeper.marks.containsAll(minesweeper.minePositions.toList()) &&
                minesweeper.marks.size == minesweeper.minePositions.size
    }

    override fun finish() = finish
}

fun <T> MutableCollection<T>.addOrRemove(element: T) {
    if (contains(element)) {
        remove(element)
    } else {
        add(element)
    }
}