package minesweeper.state

import minesweeper.addOrRemove
import kotlin.random.Random

class MoveState(
    minesweeper: Minesweeper,
    private val x: Int,
    private val y: Int,
    private val command: String
) : Minesweeper.State(minesweeper) {

    private val random = Random(System.currentTimeMillis())

    override fun run() {

        val index = minesweeper.pointToIndex(x - 1, y - 1)
        val cell = minesweeper.cells[index]

        if (command == "free" && minesweeper.minePositions.any { it == -1 }) {
            // Set mines and make sure the selected position is not a mine
            val set = mutableSetOf<Int>()
            while (set.size < minesweeper.minePositions.size) {
                val mineIndex = random.nextInt(minesweeper.columns * minesweeper.rows)
                if (index != mineIndex) {
                    set.add(mineIndex)
                }
            }
            set.forEachIndexed { _index, i ->
                minesweeper.minePositions[_index] = i
                minesweeper.cells[i] = Minesweeper.MineCell()
            }

            // update mine counts
            minesweeper.minePositions.forEach {
                val around = minesweeper.getAround(it)
                around.forEach { aroundIndex ->
                    val safeCell = minesweeper.cells[aroundIndex]
                    if (safeCell is Minesweeper.SafeCell) {
                        safeCell.nearMines = safeCell.nearMines + 1
                    }
                }
            }
        }

        if (command == "mine") {
            minesweeper.marks.addOrRemove(index)
        } else {
            cell.explore()
            minesweeper.marks.remove(index)
            if (cell.representation == 'X') {
                minesweeper.minePositions.forEach {
                    minesweeper.marks.remove(it)
                    minesweeper.cells[it].explore()
                }
            } else if (cell.representation == '/') {
                exploreAround(index)
            }
        }
    }

    private fun exploreAround(index: Int): Boolean {
        val around = minesweeper.getAround(index)

        var hasExplorable = false

        around.forEach forEach@{
            val cell = minesweeper.cells[it]
            if (cell.representation == '.') {
                if (cell.peek() == '/') {
                    cell.explore()
                    minesweeper.marks.remove(it)
                    hasExplorable = true

                    if (!exploreAround(it)) {
                        return@forEach
                    }
                } else if (cell.peek() in '0'..'9') {
                    minesweeper.marks.remove(it)
                    cell.explore()
                }
            }
        }

        return hasExplorable
    }
}