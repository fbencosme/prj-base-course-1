package com.altice.eteco.course.basic.connect4

import android.view.View

data class Chip(
    val id    : Int,
    val view  : View,
    val column: Int,
    val row   : Int
)

data class Bucket(
    val column: Int,
    val row   : Int,
    val turn  : Turn
)

fun Turn.flip() : Turn = when(this) {
    Turn.Yellow -> Turn.Red
    Turn.Red    -> Turn.Yellow
    Turn.Black  -> Turn.Black
}

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
