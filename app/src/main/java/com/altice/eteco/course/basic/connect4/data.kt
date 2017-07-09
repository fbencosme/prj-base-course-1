package com.altice.eteco.course.basic.connect4

import android.content.Context
import android.graphics.drawable.Drawable
import android.support.v4.content.ContextCompat
import android.view.View
import android.widget.TextView

import com.altice.eteco.course.basic.R

import com.pawegio.kandroid.loadAnimation

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

fun TextView.bump(n: Int = 1) =
    with(context.loadAnimation(R.anim.abc_fade_in)) {
        duration = 3000
        startAnimation(this)
        text = ("$text".toInt() + n).toString()
    }

fun Turn.drawable(ctx: Context) : Drawable {

    val res = when (this) {
        Turn.Red    -> R.drawable.circle_red
        Turn.Yellow -> R.drawable.circle_yellow
        Turn.Black  -> R.drawable.circle_black
    }

    return ContextCompat.getDrawable(ctx, res)
}

fun Turn.flip() : Turn = when(this) {
    Turn.Yellow -> Turn.Red
    Turn.Red    -> Turn.Yellow
    Turn.Black  -> Turn.Black
}