package minesweeper.state.game_over

import minesweeper.state.Minesweeper

class LoseState(minesweeper: Minesweeper) : GameOverState(minesweeper, "You stepped on a mine and failed!")