package com.altice.eteco.course.basic.ticTacToe

import android.widget.TextView

data class Gamble(
    val player  : Player,
    val position: Int,
    var textView: TextView,
    val moves   : List<Move>
)
enum class Player {
    P1, P2, PC
}

enum class Symbol {
    Cross, Nought, None
}

data class Move(
    val symbol  : Symbol,
    val position: Int
)

enum class MoveState {
    Next, Winner, Tie
}

object TicTacToe {

    fun checkMove(moves: List<Move>) : Triple<MoveState, List<Int>, List<Move>> {

        val positions = moves.map { it.position }

        fun checkMatch (match: List<Int>) : Boolean {
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
            return Triple(MoveState.Tie, emptyList(), moves)

        return Triple(MoveState.Next, emptyList(), moves)
    }

    fun nextPCMove(moves: List<Move>) : Int {

        val crosses = moves.filter { it.symbol == Symbol.Cross  }.map { it.position }
        val noughts = moves.filter { it.symbol == Symbol.Nought }.map { it.position }

        val noughtCanWin = wins.find { w ->
            val found = noughts.count  { w.contains(it) }
            found > 1 && crosses.count { w.contains(it) } == 0
        }

        if (noughtCanWin != null) {
            val find = wins.filter { w ->
                val found = noughts.count { w.contains(it) }
                found > 1 && crosses.count { w.contains(it) } == 0
            }
            .map { w ->
                val none = w.filter { !noughts.contains(it) && !noughts.contains(it)}
                none.first()
            }

            if (find.any())
              return find.first()
        }

        val crossNearToWin = wins.find { w ->
            val found = crosses.count  { w.contains(it) }
            found > 1 && noughts.count { w.contains(it) } == 0
        }

        if (crossNearToWin != null) {
            val find = wins.filter { w ->
                val found = crosses.count  { w.contains(it)  }
                found > 1 && noughts.count { w.contains(it) } == 0
            }
            .map { w ->
                val none = w.filter { !crosses.contains(it) && !noughts.contains(it) }
                none.first()
            }

            if (find.any())
              return find.first()
        }

        if (moves.count { it.position == 4 } == 0)
          return 4

        return randomMove(moves)
    }

    fun randomMove(moves: List<Move>): Int {
        val ps  = 0..8
        val ms  = ps.subtract(moves.map { it.position }).toList()
        val idx = (Math.random() * ms.size).toInt()
        val mv  = ms[idx]
        return mv
    }

    fun find(moves: List<Move>, positions: Iterable<Int>, symbol: Symbol) : Int {

        val f  = moves.filter { positions.contains(it.position) }
        val fs = f.filter     { it.symbol == symbol }
        val c  = fs.count()

        if (c == 1 || c == 2) {
            val p = positions.find { p -> fs.none { it.position == p } }
            return p ?: -1
        }

        return -1
    }

    fun nextMove(g: Gamble) : Triple<List<Move>, Move, TextView> {

        val last = g.moves.lastOrNull()
        if (last == null)
            return Triple(g.moves, Move(Symbol.Cross, g.position), g.textView)
        else
            return Triple(g.moves, Move(last.symbol.opposite(), g.position), g.textView)
    }

    val wins = listOf(
        (0 .. 2),
        (3 .. 5),
        (6 .. 8),

        (0 .. 6 step 3),
        (1 .. 7 step 3),
        (2 .. 8 step 3),

        (0 .. 8 step 4),
        (2 .. 6 step 2)
    ).map {
        println(it)
        it.toList()

    }
}

fun Symbol.opposite() =
    when(this) {
        Symbol.Cross  -> Symbol.Nought
        Symbol.Nought -> Symbol.Cross
        Symbol.None   -> Symbol.None
    }