package com.altice.eteco.course.basic.simonSays

import android.os.Bundle
import android.view.View

import com.altice.eteco.course.basic.BaseFragment
import com.altice.eteco.course.basic.R

import com.jakewharton.rxbinding2.view.clicks

import com.pawegio.kandroid.hide
import com.pawegio.kandroid.longToast
import com.pawegio.kandroid.show
import com.pawegio.kandroid.toast

import com.trello.rxlifecycle2.android.FragmentEvent

import io.reactivex.Observable
import io.reactivex.Observable.merge
import io.reactivex.android.schedulers.AndroidSchedulers

import io.reactivex.rxkotlin.withLatestFrom

import io.reactivex.subjects.BehaviorSubject
import io.reactivex.subjects.PublishSubject
import io.reactivex.subjects.Subject

import kotlinx.android.synthetic.main.simon_says_fragment.*

import java.util.Queue
import java.util.LinkedList
import java.util.concurrent.TimeUnit

class SimonSaysFragment : BaseFragment() {

    override val layoutRes: Int = R.layout.simon_says_fragment
    override val titleRes : Int = R.string.simon_says_title

    val seq     : Subject<Queue<Item>> = BehaviorSubject.createDefault(LinkedList<Item>())
    val autoPlay: Subject<Boolean>     = PublishSubject.create<Boolean>()

    override fun onViewCreated(view: View?, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val game = Game(listOf(green, red, yellow, blue))
        val bump = PublishSubject.create<Boolean>()

        val counter = seq.map { it.size }.map { size ->
            { ->
                val c = if (size <= 0) 0 else size - 1
                score.text = "$c"
            }
        }

        // Wins!
        val winner = bump.delay(200, TimeUnit.MILLISECONDS)
            .withLatestFrom(seq) {
                b, s -> Pair(b, s)
            }
            .map { (bump, s) -> {

                if (bump) {
                    longToast(R.string.simon_says_excellent)
                    R.raw.applause.play(context) {
                        seq.onNext(game.increase(s))
                        autoPlay.onNext(true)
                    }
                } else {
                    seq.onNext(s)
                    autoPlay.onNext(true)
                }
            }
        }

        // Start action button.
        val start = go.clicks()
            .doOnNext {
                go.hide()
                score.show()
                scoreLbl.show()
            }
            .throttleFirst(300, TimeUnit.MILLISECONDS)
            .map { _ -> { ->
                    longToast(R.string.simon_says_focus)
                    seq.onNext(game.genSeq())
                    autoPlay.onNext(true)
                }
            }

        // On click on item/Button stream.
        val play = merge(
            green .clicks().map { game.items[0] },
            red   .clicks().map { game.items[1] },
            yellow.clicks().map { game.items[2] },
            blue  .clicks().map { game.items[3] })
        .withLatestFrom(autoPlay) {
            item, autoPlay -> Pair(item, autoPlay)
        }
        .filter { (_, autoPlay) ->
            !autoPlay
        }
        .map { it.first }
        .withLatestFrom(seq.map { it.copy() }) {
            item, seq -> Pair(item, seq)
        }
        .map {
            (item, seq) ->
                val poll  = seq.poll()
                val match = item.button.equals(poll?.button)
                Triple(match, seq, item)
        }
        .map {
            (match, seq, item) -> { ->

               if (match && seq.isEmpty()) {

                   item.sound.play(context) {
                       bump.onNext(true)
                   }

               } else if (match) {

                   item.sound.play(context)

               } else {

                   toast(R.string.simon_says_aww)
                   R.raw.aww.play(context) {
                       bump.onNext(false)
                   }
               }
            }
        }

        // Auto play stream.
        var auto = autoPlay.filter { it }
            .delay(300, TimeUnit.MILLISECONDS)
            .withLatestFrom(seq) { _ , seq -> seq }
            .switchMap { seq ->
                // Emit auto value/sound each 2 seconds.
                Observable
                    .fromIterable(seq)
                    .map { Pair(it, it.index == seq.size - 1) }
                    .concatMap {
                        Observable.just(it).delay(3, TimeUnit.SECONDS)
                    }
            }
            .map {
                (item, last) -> {
                    item.button.alpha = .2f
                    item.sound.play(context) {

                        item.button.alpha = 1f

                        if (last)
                            autoPlay.onNext(false)
                    }
            }
        }

        // Game flow.
        merge(start, winner, auto, play)
            .mergeWith(counter)
            .observeOn(AndroidSchedulers.mainThread())
            .compose(bindUntilEvent(FragmentEvent.DESTROY_VIEW))
            .subscribe(
                { it -> it.invoke() },
                { e  ->
                    print(e.message)
                },
                { println("completed") }
            )

    }
}