package minesweeper

import minesweeper.state.game_over.GameOverState
import minesweeper.state.Minesweeper
import java.util.Scanner

fun main() {
    val scanner = Scanner(System.`in`)

    -("How many mines do you want on the field? ")
    val mineCount = scanner.next()

    val minesweeper = Minesweeper(9, 9, mineCount.toInt())

    mainLoop@ while (true) {
        -("Set/unset mines marks or claim a cell as free: ")
        val x = scanner.next()
        val y = scanner.next()
        val command = scanner.next()

        minesweeper.move(x.toInt(), y.toInt(), command)
        minesweeper.view()
        minesweeper.check()

        if (minesweeper.state is GameOverState) {
            break@mainLoop
        }
    }
}

fun <T> MutableCollection<T>.addOrRemove(element: T) {
    if (contains(element)) {
        remove(element)
    } else {
        add(element)
    }
}

operator fun Any?.unaryPlus() = println(this)
operator fun Any?.unaryMinus() = print(this)