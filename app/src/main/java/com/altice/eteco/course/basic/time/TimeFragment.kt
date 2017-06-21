package com.altice.eteco.course.basic.time

import android.app.DatePickerDialog
import android.os.Bundle
import android.view.View

import com.altice.eteco.course.basic.BaseFragment
import com.altice.eteco.course.basic.R
import com.altice.eteco.course.basic.base.medium
import com.altice.eteco.course.basic.base.split
import com.altice.eteco.course.basic.base.toDate
import com.jakewharton.rxbinding2.view.clicks

import com.trello.rxlifecycle2.android.FragmentEvent
import io.reactivex.Observable
import io.reactivex.Observable.merge

import io.reactivex.rxkotlin.Observables.combineLatest
import io.reactivex.rxkotlin.withLatestFrom
import io.reactivex.subjects.BehaviorSubject

import io.reactivex.subjects.PublishSubject
import io.reactivex.subjects.Subject

import kotlinx.android.synthetic.main.time_fragment.*

import java.util.*

class TimeFragment : BaseFragment() {

    override val layoutRes: Int = R.layout.time_fragment
    override val titleRes : Int = R.string.time_title

    var datePicker : DatePickerDialog?   = null
    val dateChosen : Subject<Date>       = BehaviorSubject.create<Date>()
    val dateSource : Subject<TimeSource> = PublishSubject.create<TimeSource>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val now       = Date()
        val (y, m, d) = now.split()

        datePicker  =  DatePickerDialog(context, {
            _, y, m, d ->
            dateChosen.onNext(Triple(y, m, d).toDate())
        }, y, m, d)

        datePicker?.let {
            it.datePicker.maxDate = now.time
        }

        dateChosen.onNext(Triple(y, m, d).toDate())
    }

    override fun onViewCreated(view: View?, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        fun dateSrc (src: TimeSource) =
            combineLatest(dateChosen, dateSource.startWith(src))
            { dc, ds -> Pair(dc, ds) }
            .filter { it.second == src }

        val f = dateSrc(TimeSource.From)
        val t = dateSrc(TimeSource.To)

        // Open date picker dialog stream.
        merge(
            from.clicks().datePick(TimeSource.From, f),
            to  .clicks().datePick(TimeSource.To  , t))
        .compose(bindUntilEvent(FragmentEvent.DESTROY_VIEW))
        .subscribe {

            val (y, m, d) = it.split()

            datePicker?.let {
                it.datePicker.updateDate(y, m, d)
                it.show()
            }
         }

        // Cal period of time stream.
        combineLatest(
            f.doOnNext {
                from.setText(it.first.medium())
            },
            t.doOnNext {
                to.setText(it.first.medium())
            })
        {
            (from, _), (to, _) -> Pair(from, to)
        }
        .map { it.period() }
        .skip(1)
        .compose(bindUntilEvent(FragmentEvent.DESTROY_VIEW))
        .subscribe {
            diff.text = it
        }
    }

    fun Observable<Unit>.datePick(src: TimeSource, date: Observable<Pair<Date, TimeSource>>) =
        map { src }
        .withLatestFrom(date.filter { it.second == src }) {
            _, (date, _) -> date
        }
}
