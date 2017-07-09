package com.altice.eteco.course.basic.connect4

import android.content.Context
import android.graphics.drawable.Drawable
import android.support.v4.content.ContextCompat
import android.widget.TextView

import com.altice.eteco.course.basic.R

import com.pawegio.kandroid.loadAnimation


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
