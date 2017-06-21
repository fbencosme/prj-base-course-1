package com.altice.eteco.course.basic.time

import java.util.*

import android.app.DatePickerDialog
import android.os.Bundle
import android.view.View

import com.altice.eteco.course.basic.BaseFragment
import com.altice.eteco.course.basic.R
import com.altice.eteco.course.basic.base.formatMedium
import com.altice.eteco.course.basic.base.split
import com.altice.eteco.course.basic.base.toDate

import com.jakewharton.rxbinding2.view.clicks

import com.trello.rxlifecycle2.android.FragmentEvent

import io.reactivex.functions.Action
import io.reactivex.Observable.merge
import io.reactivex.subjects.PublishSubject
import io.reactivex.subjects.Subject

import io.reactivex.rxkotlin.withLatestFrom
import io.reactivex.rxkotlin.Observables.combineLatest
import io.reactivex.subjects.BehaviorSubject

import kotlinx.android.synthetic.main.time_fragment.*

class TimeFragment : BaseFragment() {

    override val layoutRes: Int = R.layout.time_fragment
    override val titleRes : Int = R.string.time_title

    var datePicker : DatePickerDialog?   = null
    val dateChosen : Subject<Date>       = PublishSubject .create<Date>()
    val toDate     : Subject<Date>       = BehaviorSubject.createDefault<Date>(Date())
    val fromDate   : Subject<Date>       = BehaviorSubject.createDefault<Date>(Date())
    val toSource   : Subject<TimeSource> = PublishSubject .create<TimeSource>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val now       = Date()
        val (y, m, d) = now.split()

        datePicker  =  DatePickerDialog(context, {
            _, y, m, d ->
            dateChosen.onNext(Triple(y, m, d).toDate())
        }, y, m, d)
    }

    override fun onViewCreated(view: View?, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Stream flow.
        merge(

            // Push stream from selected date to his owner.
            dateChosen.withLatestFrom(toSource) {
                date, src -> Pair(date, src)
            }
            .map {
                (date, src) ->
                Action {
                    when (src) {
                        TimeSource.From -> fromDate.onNext(date)
                        TimeSource.To   -> toDate  .onNext(date)
                        else            -> {}
                    }
                }
            },

            // Open date picker dialog stream.
            merge(

                // From click event.
                from.clicks()
                    .map { TimeSource.From }
                    .doOnNext { toSource.onNext(it) }
                    .withLatestFrom(fromDate) { _, d -> d },

                // To click event
                to  .clicks()
                    .map { TimeSource.To }
                    .doOnNext { toSource.onNext(it) }
                    .withLatestFrom(toDate  ) { _, d -> d })
            .map {
                Action {
                    val (y, m, d) = it.split()
                    datePicker?.let {
                        it.datePicker.updateDate(y, m, d)
                        it.show()
                    }
                }
             },

            // Calc period of time stream.
            combineLatest(
                fromDate.skip(1).doOnNext {
                    from.setText(it.formatMedium())
                },
                toDate.skip(1).doOnNext {
                    to.setText(it.formatMedium())
                },
                { from, to -> Pair(from, to) })
            .map { it.period(context) }
            .map {
                Action {
                    diff.text = it
                }
            }
        )
        .compose(bindUntilEvent(FragmentEvent.DESTROY_VIEW))
        .subscribe {
            it.run()
        }
    }
}