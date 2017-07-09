package com.altice.eteco.course.basic.connect4

import android.view.View

object Game {

    fun findBucket(turn: Turn, chip: Chip, board: List<Bucket>) : Bucket {
        val rows   = board.filter  { it.column == chip.column }
        val bucket = rows.sortedBy { it.row }.lastOrNull()

        return if (bucket == null)
            Bucket(
                row    = 5,
                column = chip.column,
                turn   = turn)
        else
            Bucket(
                row    = 5 - rows.size,
                column = chip.column, turn = turn)
    }

    fun checkWins(turn: Turn, board: List<Bucket>) : Boolean =
        turn checkVertically     board ||
        turn checkHorizontally   board ||
        turn checkDiagonallyUp   board ||
        turn checkDiagonallyDown board
}
