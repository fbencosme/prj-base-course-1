package com.altice.eteco.course.basic.connect4

import android.content.Context
import android.graphics.drawable.Drawable
import android.support.v4.content.ContextCompat
import android.view.View

import com.altice.eteco.course.basic.R

enum class Turn {
    Red, Yellow
}

data class Chip(
    val id  : Int,
    val view: View,
    val x   : Int,
    val y   : Int
)

data class Bucket(
    val x: Int,
    val y: Int
)

fun Turn.flip() : Turn = when(this) {
    Turn.Yellow -> Turn.Red
    Turn.Red    -> Turn.Yellow
}

fun Turn.drawable(ctx: Context) : Drawable {

    val res = when (this) {
        Turn.Red    -> R.drawable.circle_red
        Turn.Yellow -> R.drawable.circle_yellow
    }

    return ContextCompat.getDrawable(ctx, res)
}

object Game {

    fun findBucket(c: Chip, board: List<Bucket>) : Bucket {
        val b = board.firstOrNull { it.y == c.y }
        if (b == null)
            return Bucket(c.x, 0)
        else
            return Bucket(c.x, b.y + 1)

    }
}