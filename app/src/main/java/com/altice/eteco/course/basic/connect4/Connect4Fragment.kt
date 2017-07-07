package com.altice.eteco.course.basic.connect4

import android.os.Bundle
import android.view.View
import android.widget.Button

import com.altice.eteco.course.basic.BaseFragment
import com.altice.eteco.course.basic.R

import com.jakewharton.rxbinding2.view.clicks

import com.pawegio.kandroid.views

import io.reactivex.rxkotlin.merge
import io.reactivex.rxkotlin.withLatestFrom
import io.reactivex.subjects.PublishSubject

import kotlinx.android.synthetic.main.connect_four_fragment.*

class Connect4Fragment : BaseFragment() {

    override val layoutRes: Int = R.layout.connect_four_fragment
    override val titleRes : Int = R.string.conn4_title

    val board = PublishSubject.create<List<Bucket>>()
    val turn  = PublishSubject.create<Turn>()

    override fun onViewCreated(view: View?, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val view = grid.views

        val btns = grid.views.map {
            val tag = it.tag.toString()

            Chip(view.indexOf(it), it, tag.first().toInt(), tag.last().toInt())
        }

        btns.map { c ->
            c.view.clicks().map { c }
        }
        .merge()
        .withLatestFrom(board.startWith(emptyList<Bucket>())) {
            c, board -> Pair(c, board)
        }
        .filter {
            (c, board) -> board.none { it.x == c.x && it.y == c.y}
        }
        .withLatestFrom(turn.startWith(Turn.Red).doOnNext {
            onTurn.background = it.drawable(context)
        }) {
            (c, b), t -> Triple(c, t, b)
        }
        .doOnNext  {
            (c, t, b) ->
              board.onNext(b + Bucket(c.x, c.y))
        }
        .subscribe { (c, t, b) ->

            c.view.background = t.drawable(context)
            turn.onNext(t.flip())
        }

    }
}
