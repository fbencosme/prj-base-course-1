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
    val id  : Int,
    val view: View,
    val x   : Int,
    val y   : Int
)

data class Bucket(
    val x    : Int,
    val y    : Int,
    val turn : Turn
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
        val ys = board.filter { it.y == c.y  }
        val b  = ys.sortedBy { it.x }.lastOrNull()

        if (b == null)
            return Bucket(5, c.y, t)
        else
            return Bucket(5 - ys.size, c.y, t)

    }

    fun check(t: Turn, board: List<Bucket>) : Boolean {

        var win = checkVH(t, board, vertical  , { it.y }, { it.x }) ||
                  checkVH(t, board, horizontal, { it.x }, { it.y }) ||
                  checkDA(t, board) ||
                  checkDD(t, board)

        return win
    }

    fun checkVH(t: Turn, board: List<Bucket>, orientation: List<IntRange>, group: (Bucket) -> Int, map : (Bucket) -> Int) : Boolean =
        board
            .groupBy { group(it) }
            .filter { it.value.size >= 4 }
            .any {

            val xs = it.value.map { map(it) }.sorted()

            val match = orientation.any { w ->
                w.all { x -> xs.contains(x) }
               // xs.all { x -> w.contains(x) }
            }
            val match2 = it.value.count { it.turn == t } >= 4
            match && match2
        }

    fun checkDA(t: Turn, board: List<Bucket>) :  Boolean {
        var res = false

        for (x in 3..6) {
            for (y in 0..2) {

                val a = board.find { it.x == x   && it.y == y   }
                val b = board.find { it.x == x-1 && it.y == y+1 }
                val c = board.find { it.x == x-2 && it.y == y+2 }
                val d = board.find { it.x == x-3 && it.y == y+3 }

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

    fun checkDD(t: Turn, board: List<Bucket>) :  Boolean {
        var res = false

        for (x in 3..6) {
            for (y in 0..5) {

                var a = board.find { it.x == x   && it.y == y   }
                var b = board.find { it.x == x-1 && it.y == y-1 }
                var c = board.find { it.x == x-2 && it.y == y-2 }
                var d = board.find { it.x == x-3 && it.y == y-3 }

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

    val horizontal = listOf(0..3, 1..4, 2..5, 3..6)
    val vertical   = listOf(0..3, 1..4, 2..5)
}

fun TextView.bump(n: Int = 1) {
    this.text = ("${this.text}".toInt() + n).toString()
}