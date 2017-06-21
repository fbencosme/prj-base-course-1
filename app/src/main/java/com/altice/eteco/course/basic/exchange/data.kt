package com.altice.eteco.course.basic.exchange

import io.reactivex.Observable

import java.text.DecimalFormat
import java.text.NumberFormat

enum class Currency {
    DOP, USD, EUR
}

data class CurrencyExchange
(
    val currency : Currency,
    val money    : Double
)

data class Rate
(
    val from  : Currency,
    val to    : Currency,
    val value : Double
)

object Rates  {

    fun list() : Observable<List<Rate>> = Observable.just(listOf(
        Rate(Currency.DOP, Currency.USD, 0.021062),
        Rate(Currency.DOP, Currency.EUR, 0.0188095557),
        Rate(Currency.USD, Currency.DOP, 47.4788719),
        Rate(Currency.USD, Currency.EUR, 0.893056486),
        Rate(Currency.EUR, Currency.DOP, 53.1644668),
        Rate(Currency.EUR, Currency.USD, 1.11975)
    ))
}

object Converter {

    val formatter by lazy<NumberFormat> {

        val df = DecimalFormat.getInstance()
        df.maximumFractionDigits = 2
        df
    }

    fun toExchange(rates: List<Rate>, ce: CurrencyExchange) : Pair<String, String> {

        if (ce.money <= 0)
            return  Pair("", "")

        val rates = rates.filter { it.from == ce.currency }
        var fst = rates[0]
        var snd = rates[1]

        fun format(c: CurrencyExchange, r: Rate) =
            "${formatter.format(c.money * r.value)} <b>${r.to}</b>"

        return Pair(
                format(ce, fst),
                format(ce, snd))
    }
}