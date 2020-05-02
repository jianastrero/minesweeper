package minesweeper.state

import minesweeper.unaryMinus
import minesweeper.unaryPlus

class ViewState(minesweeper: Minesweeper) : Minesweeper.State(minesweeper) {

    override fun run() {
        repeat(minesweeper.rows + 3) { row ->
            // Move through Y
            repeat(minesweeper.columns + 3) { col ->
                // Move through X
                if (col == 1 || col == minesweeper.columns + 2) {
                    -("|")
                } else if (row == 1 || row == minesweeper.rows + 2) {
                    -("-")
                } else if (row == 0) {
                    -(
                        if (col > 0)
                            col - 1
                        else
                            " "
                        )
                } else if (col == 0) {
                    -(
                        if (row > 0)
                            row - 1
                        else
                            " "
                        )
                } else {
                    val index = minesweeper.pointToIndex(col - 2, row - 2)
                    -(
                        if (minesweeper.marks.contains(index)) {
                            "*"
                        } else {
                            minesweeper.cells[index].representation
                        }
                        )
                }
            }
            +""
        }
    }
}