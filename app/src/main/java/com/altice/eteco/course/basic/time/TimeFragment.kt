package com.altice.eteco.course.basic.time

import java.util.*

import android.app.DatePickerDialog
import android.os.Bundle
import android.view.View

import com.altice.eteco.course.basic.BaseFragment
import com.altice.eteco.course.basic.R
import com.altice.eteco.course.basic.base.*

import com.jakewharton.rxbinding2.view.clicks

import com.trello.rxlifecycle2.android.FragmentEvent

import io.reactivex.subjects.Subject

import io.reactivex.rxkotlin.withLatestFrom
import io.reactivex.rxkotlin.Observables.combineLatest
import io.reactivex.subjects.BehaviorSubject

import kotlinx.android.synthetic.main.time_fragment.*

import org.joda.time.DateTime

class TimeFragment : BaseFragment() {

    override val layoutRes: Int = R.layout.time_fragment
    override val titleRes : Int = R.string.time_title

    val toDate     : Subject<DateTime>   = BehaviorSubject.createDefault<DateTime>(DateTime())
    val fromDate   : Subject<DateTime>   = BehaviorSubject.createDefault<DateTime>(DateTime())

    override fun onViewCreated(view: View?, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Stream flow.
        combineLatest(

            // From click event.
            from.clicks()
                .withLatestFrom(fromDate) { _, date -> date }
                .switchMap {
                    DateTimeDialog.open(childFragmentManager, it)
                }.doOnNext {
                  from.setText(it.toDate().formatCustom())
            },

            // To click event.
            to.clicks()
              .withLatestFrom(toDate  ) { _, d -> d }
              .switchMap {
                  DateTimeDialog.open(childFragmentManager, it)
              }.doOnNext {
                  to.setText(it.toDate().formatCustom())
            }
        )
        { from, to -> Pair(from, to) }
        .map { it.period(context) }
        .compose(bindUntilEvent(FragmentEvent.DESTROY_VIEW))
        .subscribe {
            diff.text = it
        }
    }
}