package minesweeper.state.game_over

import minesweeper.state.Minesweeper

class WinState(minesweeper: Minesweeper) : GameOverState(minesweeper, "Congratulations! You found all mines!")