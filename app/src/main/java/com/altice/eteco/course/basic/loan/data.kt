package com.altice.eteco.course.basic.loan

import com.altice.eteco.course.basic.base.month
import java.util.*

data class Loan(
    val amount: Double,
    val rate  : Double,
    val date  : Date
)

data class LoanDetail(
    val interest : Double,
    val amount   : Double,
    val months   : Int,
    val total    : Double
)

object LoanCalculator {

    fun simpleCalc(loan: Loan) : LoanDetail {
        val now      = Date()
        val months   = loan.date.month() - now.month()
        val interest = loan.amount - (loan.amount - (loan.amount * (loan.rate / 100)))
        val total    = interest + loan.amount
        val monthty  = total / months
        return LoanDetail(interest, monthty, months, total)
    }
}

