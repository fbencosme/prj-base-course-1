package com.altice.eteco.course.basic.age

import android.app.DatePickerDialog
import android.os.Bundle
import android.view.View

import com.altice.eteco.course.basic.BaseFragment
import com.altice.eteco.course.basic.R
import com.altice.eteco.course.basic.base.*

import com.jakewharton.rxbinding2.view.clicks

import com.pawegio.kandroid.loadAnimation
import com.pawegio.kandroid.show

import com.trello.rxlifecycle2.android.FragmentEvent

import io.reactivex.subjects.PublishSubject
import io.reactivex.subjects.Subject

import java.util.*

import kotlinx.android.synthetic.main.age_fragment.*
import io.reactivex.rxkotlin.withLatestFrom

class AgeFragment : BaseFragment() {

    override val layoutRes: Int = R.layout.age_fragment
    override val titleRes : Int = R.string.age_title

    var datePicker : DatePickerDialog? = null
    val currDOB    : Subject<Date> = PublishSubject.create<Date>()
    val lastDOB    : Subject<Date> = PublishSubject.create<Date>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val now       = Date()
        val (y, m, d) = now.split()

        datePicker  =  DatePickerDialog(context, {
            v, y, m, d ->
            currDOB.onNext(Triple(y, m, d).toDate())
        }, y, m, d)

        datePicker?.let {
            it.datePicker.maxDate = now.time
        }
    }

    override fun onViewCreated(view: View?, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Open date picker on click event.
        dob.clicks()
            .compose(bindUntilEvent(FragmentEvent.DESTROY_VIEW))
            .subscribe {
                datePicker?.let {
                    if (!it.isShowing) it.show()
                }
            }

        val now = Date()

        currDOB
            .doOnNext {
                dob.setText(it.medium())
            }
            .withLatestFrom(lastDOB.startWith(now)) {
                curr, last  -> Triple(curr, last, last == now)
            }
        .compose(bindUntilEvent(FragmentEvent.DESTROY_VIEW))
        .subscribe(this::onAge)
    }

    fun onAge(dobs: Triple<Date, Date, Boolean>) {

        val (curr, last, first) = dobs

        when(first) {

            true -> {
                val years = curr.yearsFrom()

                currentAge.text = resources.getQuantityString(R.plurals.age_current, years, years)
            }

            else -> {
                val currYears = curr.yearsFrom()
                val lastYears = last.yearsFrom()
                val diffYears = Math.abs(lastYears - currYears)
                currentAge.text = resources.getQuantityString(R.plurals.age_current, currYears, currYears)
                lastAge.text    = resources.getQuantityString(R.plurals.age_last   , lastYears, lastYears)
                diffAge.text    = resources.getQuantityString(R.plurals.age_diff   , diffYears, diffYears)
            }
        }

        lastDOB.onNext(curr)

        details.show()

        with(context.loadAnimation(R.anim.abc_popup_enter)) {
            duration = 600
            details.startAnimation(this)
        }
    }
}
