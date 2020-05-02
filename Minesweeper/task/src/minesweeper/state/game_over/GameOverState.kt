package minesweeper.state.game_over

import minesweeper.state.Minesweeper
import minesweeper.unaryPlus

open class GameOverState(minesweeper: Minesweeper, private val message: String) : Minesweeper.State(minesweeper) {

    override fun run() {
        +message
    }
}