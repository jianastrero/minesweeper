package minesweeper.state

import minesweeper.state.game_over.LoseState
import minesweeper.state.game_over.WinState

class CheckState(minesweeper: Minesweeper) : Minesweeper.State(minesweeper) {

    override fun run() {
        if (minesweeper.cells.any { it.representation == 'X' }) {
            minesweeper.setState(LoseState(minesweeper))
        } else if (
            minesweeper.marks.containsAll(minesweeper.minePositions.toList()) &&
            minesweeper.marks.size == minesweeper.minePositions.size
        ) {
            minesweeper.setState(WinState(minesweeper))
        }
    }
}