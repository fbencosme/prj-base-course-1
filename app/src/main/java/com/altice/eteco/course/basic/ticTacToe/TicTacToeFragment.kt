package com.altice.eteco.course.basic.ticTacToe

import android.graphics.Typeface
import android.os.Bundle
import android.support.v4.content.ContextCompat
import android.view.View
import android.widget.TextView

import com.altice.eteco.course.basic.BaseFragment
import com.altice.eteco.course.basic.R

import com.jakewharton.rxbinding2.view.clicks

import com.pawegio.kandroid.alert
import com.pawegio.kandroid.loadAnimation

import com.trello.rxlifecycle2.android.FragmentEvent

import io.reactivex.Observable.merge
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.rxkotlin.merge
import io.reactivex.subjects.PublishSubject
import io.reactivex.rxkotlin.withLatestFrom

import kotlinx.android.synthetic.main.tic_tac_toe_fragment.*

import java.util.concurrent.TimeUnit

class TicTacToeFragment : BaseFragment() {

    override val layoutRes: Int = R.layout.tic_tac_toe_fragment
    override val titleRes : Int = R.string.ticTacToe_title

    var gridBtns = listOf<Pair<Int, TextView>>()
    val moves    = PublishSubject.create<List<Move>>()
    val pc       = PublishSubject.create<Unit>()
    val counter  = PublishSubject.create<Symbol>()

    override fun onViewCreated(view: View?, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        gridBtns = (0 .. grid.childCount - 1).map {
            Pair(it, grid.getChildAt(it) as TextView)
        }

        // Configure buttons background.
        val bg   = windowBackgroundColor()
        gridBtns.forEach {
            (_, tv) -> tv.setBackgroundColor(bg)
        }

        // Reset Game.
        var restart = reset.clicks()
             .map {
                 _ -> { doReset()  }
             }

        // Game stream flow.
        var player = gridBtns.map {
            (position, tv) ->
                tv.clicks().map { Pair(position, tv) }
        }
        .merge()
        .throttleFirst(400, TimeUnit.MILLISECONDS)
        .map {
            (tv, p) -> Triple(Player.P1, tv, p)
        }

        // Marker.
        val pcPlayer =
            pc.withLatestFrom(moves) { _, ms -> ms}
              .filter { it.size < 8 }
              .map {
                  val p  = TicTacToe.nextRandomPCMove(it)
                  val (_, tv) = gridBtns[p]
                  Triple(Player.PC, p, tv)
              }
              .delay(250, TimeUnit.MILLISECONDS)

        val marker = merge(player, pcPlayer)
            .withLatestFrom(moves.startWith(emptyList<Move>())) {
                (player, position, tv), moves -> Gamble(player, position, tv, moves)
         }
        .filter {
            g -> !g.moves.any { it.position == g.position }
        }
        .map {
            Pair(it.player, TicTacToe.nextMove(it))
        }
        .doOnNext {
            (p, m) ->
                var ms = m.first + m.second
                moves.onNext(ms)
        }
        .doOnNext {
            if (it.first == Player.P1)
                pc.onNext(Unit.apply {  })
        }
        .map {
            (ms, m) -> { ->
                when (m.second.symbol) {
                    Symbol.Cross  -> m.third.text = "X"
                    Symbol.Nought -> m.third.text = "O"
                }
            }
        }

        // Check Game winner
        val winner = moves
            .filter { it.any { it.symbol != Symbol.None } }
            .map(TicTacToe::checkMove)
            .map {
                (s, winner, moves) -> { ->
                checkMove(s, winner, moves)
              }
            }

        val scoreboard = merge(
            counter
                .filter { it == Symbol.Cross }
                .scan (0) { x, _ -> x + 1 }
                .map {
                    n -> { ->
                        crosses.text =  "$n"
                    }
                },
            counter
                .filter { it == Symbol.Nought }
                .scan (0) { x, y -> x + 1 }
                .map {
                    n -> { ->
                        noughts.text =  "$n"
                    }
                })

         // Game flow.
         merge(restart, marker, winner, scoreboard)
             .observeOn(AndroidSchedulers.mainThread())
             .compose(bindUntilEvent(FragmentEvent.DESTROY_VIEW))
             .subscribe { it.invoke() }
    }

    fun checkMove(s: MoveState, winner: Array<Int>, moves: List<Move>) {
        when (s) {
            MoveState.Next   -> onNext()
            MoveState.Tie    -> onTie()
            MoveState.Winner -> onWin(winner, moves.last())
        }
    }

    fun onTie() =
        onAlert(getString(R.string.ticTacToe_tie))

    fun onAlert(msg: String) =
        context.alert {
            title(msg)
            onCancel {
                doReset()
            }
        }.show()

    fun onWin(poss: Array<Int>, move: Move) {

        // Fill moves
        moves.onNext((0..8).map { Move(Symbol.None, it) })

        val colorRes = if (move.symbol == Symbol.Nought) R.color.colorAccent else R.color.colorPrimary
        val color    = ContextCompat.getColor(context, colorRes)
        val wins     = gridBtns.filter  { (p, _ )  -> poss.contains(p) }

         wins.forEach { (_, tv) ->
            tv.setTextColor(color)
            tv.setTypeface(tv.typeface, Typeface.BOLD_ITALIC)
         }

        wins.firstOrNull { (_, tv) -> tv.text.isNotEmpty() }
           ?.let  {
               onAlert(getString(R.string.ticTacToe_win, it.second.text))
        }

        counter.onNext(move.symbol)
    }

    fun onNext() {}

    fun doReset() {
        val c = ContextCompat.getColor(context, android.R.color.black)

        moves.onNext(emptyList())

        gridBtns.forEach {
            (_, tv) ->
                tv.text = ""
                tv.setTypeface(null, Typeface.NORMAL)
                //tv.alpha = 1f
                tv.setTextColor(c)
        }

        with(context.loadAnimation(R.anim.abc_popup_enter)) {
            duration = 600
            grid.startAnimation(this)
        }
    }

}
