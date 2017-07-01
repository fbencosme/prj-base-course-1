package com.altice.eteco.course.basic.ticTacToe

import android.widget.TextView

enum class Symbol {
    Cross, Nought
}

data class Move(
    val symbol  : Symbol,
    val position: Int
)

enum class MoveState {
    Next, Winner, Tie
}

object TicTacToe {

    fun checkMove(moves: List<Move>) : Triple<MoveState, Array<Int>, List<Move>> {

        val positions = moves.map { it.position }

        fun checkMatch (match: Array<Int>) : Boolean {
            val ms = moves.filter { m -> match.contains(m.position) }
            return ms.all { it.symbol == Symbol.Nought } ||
                   ms.all { it.symbol == Symbol.Cross  }
        }

        val winner = wins.find {
            it.all { positions.contains(it) } && checkMatch(it)
        }

        if (winner != null)
            return Triple(MoveState.Winner, winner, moves)

        if (moves.size == 9)
            return Triple(MoveState.Tie, emptyArray(), moves)

        return Triple(MoveState.Next, emptyArray(), moves)
    }

    fun nextMove(t: Triple<Int, TextView, List<Move>>) : Triple<List<Move>, Move, TextView> {
        val (position, tv, moves) = t
        val last = moves.lastOrNull()
        if (last == null)
            return Triple(moves, Move(Symbol.Cross, position), tv)
        else
            return Triple(moves, Move(last.symbol.opposite(), position), tv)
    }

    val wins = arrayOf(
        arrayOf(0, 1, 2),
        arrayOf(3, 4, 5),
        arrayOf(6, 7, 8),

        arrayOf(0, 3, 6),
        arrayOf(1, 4, 7),
        arrayOf(2, 5, 8),

        arrayOf(0, 4, 8),
        arrayOf(2, 4, 6))
}

fun Symbol.opposite() =
    when(this) {
        Symbol.Cross  -> Symbol.Nought
        Symbol.Nought -> Symbol.Cross
    }