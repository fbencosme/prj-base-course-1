package com.altice.eteco.course.basic.base

import org.joda.time.DateTime
import java.text.DateFormat
import java.text.SimpleDateFormat
import java.util.*

fun Date.yearsFrom(now: Date = Date()) =
   Math.abs(year() - now.year())

fun Date.year(): Int {
    val cal   = Calendar.getInstance()
    cal.time  = this
    return cal.get(Calendar.YEAR)
}

fun Date.month(): Int {
    val cal   = Calendar.getInstance()
    cal.time  = this
    return cal.get(Calendar.MONTH)
}

fun Date.add(field: Int, n: Int): Date {
    val cal   = Calendar.getInstance()
    cal.time  = this
    cal.add(field, n)
    return  cal.time
}

fun Date.split(): Triple<Int, Int, Int> {

    val cal   = Calendar.getInstance()
    cal.time  = this
    val year  = cal.get(Calendar.YEAR)
    val month = cal.get(Calendar.MONTH)
    val day   = cal.get(Calendar.DAY_OF_MONTH)

    return Triple(year, month, day)
}

fun Date.formatMedium() : String =
    DateFormat.getDateInstance(SimpleDateFormat.MEDIUM).format(this)

fun Date.formatCustom(format: String = "EEE, d MMM yyyy hh:mm:ss aaa") : String =
    SimpleDateFormat(format).format(this)

fun Triple<Int, Int, Int>.toDate() : Date {
    val cal   = Calendar.getInstance()
    cal.set(first, second, third, 0, 0);
    return cal.time
}

fun Triple<Int, Int, Int>.toDateTime() : DateTime =
    DateTime(first, second, third, 0, 0, 0)

fun Pair<Int, Int>.toDateTime() : DateTime =
    DateTime.now().withHourOfDay(first).withMinuteOfHour(second)


fun CharSequence.toDouble() = "$this".toDoubleOrNull() ?: 0.toDouble()