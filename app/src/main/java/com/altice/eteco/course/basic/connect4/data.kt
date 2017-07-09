package com.altice.eteco.course.basic.connect4

import android.content.Context
import android.graphics.drawable.Drawable
import android.support.v4.content.ContextCompat
import android.view.View
import android.widget.TextView

import com.altice.eteco.course.basic.R

enum class Turn {
    Red, Yellow, Black
}

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

fun Turn.drawable(ctx: Context) : Drawable {

    val res = when (this) {
        Turn.Red    -> R.drawable.circle_red
        Turn.Yellow -> R.drawable.circle_yellow
        Turn.Black  -> R.drawable.circle_black
    }

    return ContextCompat.getDrawable(ctx, res)
}

object Game {

    fun findBucket(t: Turn, c: Chip, board: List<Bucket>) : Bucket {
        val rows   = board.filter  { it.column == c.column }
        val bucket = rows.sortedBy { it.row }.lastOrNull()

        return if (bucket == null)
            Bucket(
                row    = 5,
                column = c.column,
                turn   = t)
        else
            Bucket(
                row    = 5 - rows.size,
                column = c.column, turn = t)
    }

    fun check(t: Turn, board: List<Bucket>) : Boolean {

        var win = checkV(t, board)  ||
                  checkH(t, board)  ||
                  checkDA(t, board) ||
                  checkDD(t, board)

        return win
    }

    // Check Horizontal
    fun checkH(t: Turn, board: List<Bucket>) :  Boolean {
        var res = false

        for (row in 0..3) {
            for (column in 0..6) {

                val a = board.find { it.row == column && it.column == row   }
                val b = board.find { it.row == column && it.column == row+1 }
                val c = board.find { it.row == column && it.column == row+2 }
                val d = board.find { it.row == column && it.column == row+3 }

                if (a?.turn?.equals(t) ?: false &&
                    b?.turn?.equals(t) ?: false &&
                    c?.turn?.equals(t) ?: false &&
                    d?.turn?.equals(t) ?: false)
                {
                    res = true
                    return res
                }
            }
        }

        return res
    }

    // Check Vertical
    fun checkV(t: Turn, board: List<Bucket>) :  Boolean {
        var res = false

        for (column in 0..3) {
            for (row in 0..6) {

                val a = board.find { it.row == column   && it.column == row }
                val b = board.find { it.row == column+1 && it.column == row }
                val c = board.find { it.row == column+2 && it.column == row }
                val d = board.find { it.row == column+3 && it.column == row }

                if (a?.turn?.equals(t) ?: false &&
                        b?.turn?.equals(t) ?: false &&
                        c?.turn?.equals(t) ?: false &&
                        d?.turn?.equals(t) ?: false)
                {
                    res = true
                    return res
                }
            }
        }

        return res
    }

    // Check Diagonal up
    fun checkDA(t: Turn, board: List<Bucket>) :  Boolean {
        var res = false

        for (column in 3..6) {
            for (row in 0..2) {

                val a = board.find { it.row == column   && it.column == row   }
                val b = board.find { it.row == column-1 && it.column == row+1 }
                val c = board.find { it.row == column-2 && it.column == row+2 }
                val d = board.find { it.row == column-3 && it.column == row+3 }

                if (a?.turn?.equals(t) ?: false &&
                    b?.turn?.equals(t) ?: false &&
                    c?.turn?.equals(t) ?: false &&
                    d?.turn?.equals(t) ?: false)
                {
                    res = true
                    return res
                }
            }
        }

        return res
    }

    // Check Diagonal down
    fun checkDD(t: Turn, board: List<Bucket>) :  Boolean {
        var res = false

        for (column in 3..6) {
            for (row in 0..5) {

                var a = board.find { it.row == column   && it.column == row   }
                var b = board.find { it.row == column-1 && it.column == row-1 }
                var c = board.find { it.row == column-2 && it.column == row-2 }
                var d = board.find { it.row == column-3 && it.column == row-3 }

                if (a?.turn?.equals(t) ?: false &&
                    b?.turn?.equals(t) ?: false &&
                    c?.turn?.equals(t) ?: false &&
                    d?.turn?.equals(t) ?: false)
                {
                    res = true
                    return res
                }
            }
        }

        return res
    }

}

fun TextView.bump(n: Int = 1) {
    this.text = ("${this.text}".toInt() + n).toString()
}