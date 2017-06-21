package com.altice.eteco.course.basic.time

import java.util.*
enum class TimeSource {
    From, To
}

fun Pair<Date, Date>.period() : String {
    val (from, to) = this

    return "2 days"
}
