package com.altice.eteco.course.basic.base

import android.content.Context
import android.support.v4.app.FragmentManager
import android.os.Bundle
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.widget.DatePicker

import com.altice.eteco.course.basic.R
import com.jakewharton.rxbinding2.view.clicks

import com.trello.rxlifecycle2.components.support.RxDialogFragment

import io.reactivex.Observable
import io.reactivex.subjects.PublishSubject
import io.reactivex.rxkotlin.Observables.combineLatest
import io.reactivex.rxkotlin.withLatestFrom
import io.reactivex.subjects.BehaviorSubject
import io.reactivex.subjects.Subject

import kotlinx.android.synthetic.main.date_time_dialog_fragment.*

import org.joda.time.DateTime

class CustomDatePicker(ctx: Context, attrs: AttributeSet) : DatePicker(ctx, attrs) {
    override fun onInterceptTouchEvent(event: MotionEvent?): Boolean {
        if (event?.actionMasked == MotionEvent.ACTION_DOWN)
            parent?.parent?.let { it.requestDisallowInterceptTouchEvent(true) }
        return false
    }
}

class DateTimeDialog : RxDialogFragment() {

    val input  : Subject<DateTime> = BehaviorSubject.create<DateTime>()
    val output : Subject<DateTime> = PublishSubject.create<DateTime>()

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?, savedInstanceState: Bundle?): View? =
        inflater?.inflate(R.layout.date_time_dialog_fragment, container, false)

    override fun onViewCreated(view: View?, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val date = PublishSubject.create<DateTime>()
        val time = PublishSubject.create<DateTime>()

        input.take(1)
             .subscribe {

            date.onNext(it)
            time.onNext(it)

            datePicker.init(it.year, it.monthOfYear, it.dayOfMonth, {
                _, y, m , d ->
                    date.onNext(Triple(y, m, d).toDateTime())
            })

            timePicker.setOnTimeChangedListener {
                _ , h, m ->
                    time.onNext(Pair(h, m).toDateTime())
            }
        }

        ok.clicks()
          .withLatestFrom(
            combineLatest(date, time) {
               date, time  ->
                    DateTime(date.year, date.monthOfYear, date.dayOfMonth, time.hourOfDay, time.minuteOfHour)
            })
          { _, datetime -> datetime }
          .take(1)
          .subscribe {
              output.onNext(it)
              dismissAllowingStateLoss()
          }
    }

    fun open(fm: FragmentManager, currDate: DateTime) {
        show(fm, "datetimesheet")
        input.onNext(currDate)
    }

    companion object {

        fun open(fm: FragmentManager, currDate: DateTime) : Observable<DateTime> {
            val f = DateTimeDialog()
            f.open(fm, currDate)
            return f.output
        }
    }
}