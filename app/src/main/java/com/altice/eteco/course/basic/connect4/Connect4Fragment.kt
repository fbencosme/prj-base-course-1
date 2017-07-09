package com.altice.eteco.course.basic.connect4

import android.os.Bundle
import android.view.View

import com.altice.eteco.course.basic.BaseFragment
import com.altice.eteco.course.basic.R
import com.altice.eteco.course.basic.simonSays.play

import com.jakewharton.rxbinding2.view.clicks

import com.pawegio.kandroid.views
import com.trello.rxlifecycle2.android.FragmentEvent
import io.reactivex.Observable

import io.reactivex.rxkotlin.merge
import io.reactivex.rxkotlin.withLatestFrom
import io.reactivex.subjects.PublishSubject
import io.reactivex.subjects.BehaviorSubject
import io.reactivex.Observable.merge
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.subjects.Subject

import kotlinx.android.synthetic.main.connect_four_fragment.*
import java.util.concurrent.TimeUnit

class Connect4Fragment : BaseFragment() {

    override val layoutRes: Int = R.layout.connect_four_fragment
    override val titleRes : Int = R.string.conn4_title

    val board : Subject<List<Bucket>> = PublishSubject.create<List<Bucket>>()
    val turn  : Subject<Turn>         = BehaviorSubject.createDefault(Turn.Red)
    val wins  : Subject<Turn>         = PublishSubject.create<Turn>()

    override fun onViewCreated(view: View?, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val views = grid.views
        
        // Collect chips.
        val chips = createChips(views)

        // Winner stream.
        val winner = winnerStream(views)

        // Restart stream.
        val restart = restartStream(views)

        // Player turn stream.
        val turnPlay = playTurnStream(chips)

        // Game flow.
        val game = merge(turnPlay, restart, winner)

        // Start game
        game.observeOn(AndroidSchedulers.mainThread())
            .compose(bindUntilEvent(FragmentEvent.DESTROY_VIEW))
            .subscribe {
                it?.invoke()
            }
    }

    fun createChips(views: List<View>): List<Chip> =
         grid.views.map {

            val tag = it.tag.toString()
            val row = "${tag.first()}".toInt()
            val column = "${tag.last()}".toInt()

            Chip(views.indexOf(it), it, column, row)
        }

    fun playTurnStream(chips: List<Chip>): Observable<() -> Unit>? =
        chips.map { chip ->
            chip.view.clicks().map { chip }
        }
        .merge()
        .throttleFirst(300, TimeUnit.MILLISECONDS)
        .withLatestFrom(board.startWith(emptyList<Bucket>())) {
            chip, board ->
            Pair(chip, board)
        }
        .withLatestFrom(
            turn.doOnNext {
                onTurn.background = it.drawable(context)
            }
        ) {
            (chip, board), t ->
            Triple(chip, board, t)
        }
        .map {
            (chip, board, turn) ->
            val bucket = Game.findBucket(turn, chip, board)
            val newChip = chips.find { it.row == bucket.row && it.column == bucket.column }
            Triple(Pair(newChip, bucket), board, turn)
        }
        .filter {
            (pair, board, _) ->
            val chip = pair.first
            board.isEmpty() || board.none { it.row == chip?.row && it.column == chip.column }
        }
        .doOnNext {
            (pair, bucket, _) ->
            val newBucket = pair.second
            board.onNext(bucket + newBucket)
        }
        .map { (pair, b, t) -> { ->
            pair.first?.let {
                it.view.background = t.drawable(context)
            }

            turn.onNext(t.flip())

            // Check for a winner.
            val tmp = b + pair.second

            if (Game.checkWins(t, tmp))
                wins.onNext(t)

            // Reset if there's no more room.
            else if (tmp.size == 42)
                wins.onNext(Turn.Black)
        }
    }

    fun restartStream(views: List<View>): Observable<() -> Unit>? =
         reset.clicks().map { _ -> {  doReset(views) } }

    fun winnerStream(views: List<View>): Observable<() -> Unit>? =
        wins.map { w -> { ->

            // Disabling all chips
            views.forEach {
                it.isEnabled = false
                it.alpha = .4f
            }

            // Increase Score counter
            when (w) {

                Turn.Red -> {
                    red.bump()
                    R.raw.applause.play(context) {
                        doReset(views, w)
                    }
                }

                Turn.Yellow -> {
                    yellow.bump()
                    R.raw.applause.play(context) {
                        doReset(views, w)
                    }
                }

                // Reset when is black,
                Turn.Black -> doReset(views)
            }
        }
    }

    fun doReset(views: List<View>, t: Turn = Turn.Black) {
        val bg = Turn.Black.drawable(context)

        views.forEach {
            it.background = bg
            it.isEnabled  = true
            it.alpha      = 1f
        }

        board.onNext(emptyList())
        turn.onNext(t)
    }

}