package com.altice.eteco.course.basic.loan

import java.util.*

import android.app.DatePickerDialog
import android.os.Bundle
import android.view.View

import com.altice.eteco.course.basic.BaseFragment
import com.altice.eteco.course.basic.R
import com.altice.eteco.course.basic.base.*

import com.jakewharton.rxbinding2.view.clicks
import com.jakewharton.rxbinding2.widget.textChanges

import com.pawegio.kandroid.hide
import com.pawegio.kandroid.inputMethodManager
import com.pawegio.kandroid.loadAnimation
import com.pawegio.kandroid.show

import com.trello.rxlifecycle2.android.FragmentEvent

import io.reactivex.subjects.Subject
import io.reactivex.subjects.PublishSubject
import io.reactivex.rxkotlin.Observables.combineLatest
import io.reactivex.rxkotlin.withLatestFrom

import kotlinx.android.synthetic.main.loan_fragment.*
import kotlinx.android.synthetic.main.loan_details.*

class LoanFragment : BaseFragment() {

    override val layoutRes: Int = R.layout.loan_fragment
    override val titleRes : Int = R.string.loan_title

    var datePicker  : DatePickerDialog? = null
    val dateSelected: Subject<Date>     = PublishSubject.create<Date>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val (y, m, d) = Date().split()

        datePicker  =  DatePickerDialog(context, {
            _, y, m, d ->
                dateSelected.onNext(Triple(y, m, d).toDate())
        }, y, m, d)

        datePicker?.let {
            it.datePicker.minDate = Date().add(Calendar.MONTH, 1).time
        }
    }
    
    override fun onViewCreated(view: View?, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Open date picker on click event.
        date.clicks()
            .compose(bindUntilEvent(FragmentEvent.DESTROY_VIEW))
            .subscribe {
                datePicker?.let {
                    if (!it.isShowing) it.show()
                }
            }

        // Form loan.
        calc.clicks()
            // Hide keyboard before submit.
            .doOnNext {
                details.hide()
                context.inputMethodManager?.hideSoftInputFromWindow(calc.windowToken, 0)
                calc.isEnabled = false
                progressBar.show()
            }
            .withLatestFrom(
                // Collect form info.
                combineLatest(
                    amount.textChanges().map { it.toDouble() },
                    rate  .textChanges().map { it.toDouble() },
                    dateSelected
                        .doOnNext {
                            date.setText(it.formatMedium())
                        })
                // Transform form data.
                { amount, rate, date -> Loan(amount, rate, date) }
                .doOnNext {
                    calc.isEnabled = true
                }
        ) {
            // Calculate possible loan details.
            _, l -> LoanCalculator.simpleCalc(l)
        }
        .compose(bindUntilEvent(FragmentEvent.DESTROY_VIEW))
        .subscribe(this::onDetail)
    }

    fun onDetail(detail: LoanDetail)
    {
        title.text    = getString(R.string.loan_detail_title  , detail.amount)
        message.text  = getString(R.string.loan_detail_message, detail.amount, detail.months)
        monthly.text  = "$${detail.amount}"
        time.text     = detail.months.toString()
        totalLbl.text = getString(R.string.loan_detail_total, detail.months)
        total.text    = "$${detail.total}"
        interest.text = "$${detail.interest}"
        monthlyLbl.text = getString(R.string.loan_detail_monthly_value, detail.months)

        details.show()

        calc.isEnabled = true
        progressBar.hide(true)

        with(context.loadAnimation(R.anim.abc_popup_enter)) {
            duration = 600
            details.startAnimation(this)
        }
    }
}
