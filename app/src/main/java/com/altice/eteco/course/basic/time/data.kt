package com.altice.eteco.course.basic.time

import android.content.Context

import com.altice.eteco.course.basic.R

import org.joda.time.DateTime
import org.joda.time.Period
import org.joda.time.format.PeriodFormatterBuilder

import java.util.*

enum class TimeSource {
    From, To, Now
}

fun Pair<Date, Date>.period(ctx: Context) : String {
    val (from, to) = this

    fun res(r: Int) = ctx.getString(r)
    val separator   = " , "
    val formatter   = PeriodFormatterBuilder()
        .printZeroAlways()

        .appendYears()
        .appendSuffix(res(R.string.time_year), res(R.string.time_years))
        .appendSeparator(separator)

        .appendMonths()
        .appendSuffix(res(R.string.time_month), res(R.string.time_months))
        .appendSeparator(separator)
        .printZeroRarelyLast()

        .appendDays()
        .appendSuffix(res(R.string.time_day), res(R.string.time_days))
        .appendSeparator(separator)
        .printZeroRarelyLast()

        .appendHours()
        .appendSuffix(res(R.string.time_hour), res(R.string.time_hours))
        .appendSeparator(separator)
        .printZeroRarelyLast()

        .appendMinutes()
        .appendSuffix(res(R.string.time_minute), res(R.string.time_minutes))
        .appendSeparator(separator)
        .printZeroRarelyLast()

        .appendSeconds()
        .appendSuffix(res(R.string.time_second), res(R.string.time_seconds))
        .appendSeparator(separator)
        .printZeroRarelyLast()

        .toFormatter()

    return Period(DateTime(from), DateTime(to)).toString(formatter)
}
