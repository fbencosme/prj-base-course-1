package com.altice.eteco.course.basic.simonSays

import android.content.Context
import android.graphics.Color
import android.media.MediaPlayer
import android.widget.Button

import com.altice.eteco.course.basic.R

import java.util.*

data class Item(
    val button: Button,
    val sound : Int,
    val index : Int = -1
)

class Game(options: List<Button>) {

    val items = listOf(
        Item(options[0], R.raw.sound1),
        Item(options[1], R.raw.sound2),
        Item(options[2], R.raw.sound3),
        Item(options[3], R.raw.sound4)
    )

    fun genSeq(limit: Int) : Queue<Item> {

        val q = LinkedList<Item>()

        (0..limit).forEach {
            val idx  = (Math.random() * items.size).toInt()
            q.add(items[idx].copy(index = it))
        }

        return q
    }

    fun increase(seq: Queue<Item>) : Queue<Item> {
        val idx  = (Math.random() * items.size).toInt()
        val item = items[idx].copy(index = seq.size - 1)
        seq.add(item)
        return seq
    }

}

fun Queue<Item>.copy() : Queue<Item> {
    val q = LinkedList<Item>()

    forEach {
        q.add(it)
    }
    return q
}

fun Int.play(ctx: Context, completed : (() -> Unit)? = null) {

    try {

        val mp = MediaPlayer.create(ctx, this)

        mp.setOnCompletionListener {
            mp.release()
            completed?.invoke()
            mp.setOnCompletionListener(null)
        }

        mp.start()

    } catch (e: Exception) {
        e.printStackTrace()
    }
}