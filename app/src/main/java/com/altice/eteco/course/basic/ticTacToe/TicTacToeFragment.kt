package com.altice.eteco.course.basic.ticTacToe

import android.content.res.ColorStateList
import android.graphics.Color
import android.graphics.Typeface
import android.os.Bundle
import android.support.v4.content.ContextCompat
import android.view.View
import android.widget.TextView

import com.altice.eteco.course.basic.BaseFragment
import com.altice.eteco.course.basic.R
import com.jakewharton.rxbinding2.view.clicks
import com.pawegio.kandroid.alert
import com.pawegio.kandroid.longToast
import com.trello.rxlifecycle2.android.FragmentEvent
import io.reactivex.Observable
import io.reactivex.Observable.merge
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.rxkotlin.Observables
import io.reactivex.rxkotlin.merge
import io.reactivex.subjects.PublishSubject
import  io.reactivex.rxkotlin.withLatestFrom

import kotlinx.android.synthetic.main.tic_tac_toe_fragment.*
import java.util.concurrent.TimeUnit


class TicTacToeFragment : BaseFragment() {

    override val layoutRes: Int = R.layout.tic_tac_toe_fragment
    override val titleRes : Int = R.string.ticTacToe_title

    var gridBtns = listOf<Pair<Int, TextView>>()
    val moves    = PublishSubject.create<List<Move>>()

    override fun onViewCreated(view: View?, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        gridBtns = (0 .. grid.childCount - 1).map {
            Pair(it, grid.getChildAt(it) as TextView)
        }

        // Configure button background.
        val bg   = windowBackgroundColor()
        gridBtns.forEach {
            (_, tv) -> tv.setBackgroundColor(bg)
        }

        // Reset Game.
        reset
            .clicks()
            .compose(bindUntilEvent(FragmentEvent.DESTROY_VIEW))
            .subscribe { doReset() }

        // Game stream flow.
        gridBtns.map {
            (position, tv) ->
                tv.clicks().map { Pair(position, tv) }
        }
        .merge()
        .throttleFirst(600, TimeUnit.MILLISECONDS)
        .withLatestFrom(moves.startWith(emptyList<Move>())) {
             (position, tv), moves -> Triple(position, tv, moves)
         }
        .filter {
            (position, tv, moves) -> !moves.any { it.position == position }
        }
        .map(TicTacToe::nextMove)
        .doOnNext {
            (ms, m, _) ->
                moves.onNext(ms + m)
        }
        .observeOn(AndroidSchedulers.mainThread())
        .compose(bindUntilEvent(FragmentEvent.DESTROY_VIEW))
        .subscribe {
            (_, m, tv) ->

              when(m.symbol) {
                  Symbol.Cross  -> tv.text = "X"
                  Symbol.Nought -> tv.text = "O"
              }
        }

        // Check Game winner
        moves
            .filter { it.any() }
            .map(TicTacToe::checkMove)
            .compose(bindUntilEvent(FragmentEvent.DESTROY_VIEW))
            .subscribe {
                (s, poss, moves) ->
                when(s) {
                    MoveState.Next -> {}

                    MoveState.Tie  -> {
                        context.alert {
                            title("Tie")
                            onCancel { doReset()  }
                        }
                     //   doReset()
                    }

                    MoveState.Winner  -> {
                        context.alert("Win").show()
                        markWinner(poss, moves.last())
                    }
                }
            }
    }

    fun markWinner(poss: Array<Int>, move: Move) {
        val colorRes = if (move.symbol == Symbol.Nought) R.color.colorAccent else R.color.colorPrimary
        var color    = ContextCompat.getColor(context, colorRes)

        gridBtns
            .filter  { (p, _ )  -> poss.contains(p) }
            .forEach { (_, tv) ->
                tv.setTextColor(color)
                tv.setTypeface(tv.typeface, Typeface.BOLD_ITALIC)
                //tv.alpha = .5f
            }
    }

    fun doReset() {
        val c = ContextCompat.getColor(context, android.R.color.black)

        moves.onNext(emptyList())

        gridBtns.forEach {
            (_, tv) ->
                tv.text = ""
                tv.setTypeface(tv.typeface, Typeface.NORMAL)
                //tv.alpha = 1f
                tv.setTextColor(c)
        }
    }

}
