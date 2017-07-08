package com.altice.eteco.course.basic.exchange

import android.os.Build
import android.os.Bundle
import android.text.Html
import android.text.Html.fromHtml
import android.view.View

import com.altice.eteco.course.basic.BaseFragment
import com.altice.eteco.course.basic.R
import com.altice.eteco.course.basic.base.toDouble

import com.jakewharton.rxbinding2.widget.checkedChanges
import com.jakewharton.rxbinding2.widget.textChanges

import com.pawegio.kandroid.fromApi
import com.pawegio.kandroid.toApi

import com.trello.rxlifecycle2.android.FragmentEvent

import io.reactivex.Observable.merge
import io.reactivex.rxkotlin.Observables.combineLatest
import io.reactivex.rxkotlin.withLatestFrom

import kotlinx.android.synthetic.main.exchange_fragment.*

class ExchangeFragment : BaseFragment() {

    override val layoutRes: Int = R.layout.exchange_fragment
    override val titleRes : Int = R.string.exchange_title

    override fun onViewCreated(view: View?, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        combineLatest(

            // Currency
            merge(
                dop.checkedChanges().filter { it }.map { Currency.DOP },
                usd.checkedChanges().filter { it }.map { Currency.USD },
                eur.checkedChanges().filter { it }.map { Currency.EUR }
            ).startWith(Currency.USD),

            // Money entered
            money
                .textChanges()
                .map { it.toDouble() },

            // Transform
            { currency, money -> CurrencyExchange(currency, money) }

        ).doOnNext {
            progressBar.visibility = View.VISIBLE
        }
        .withLatestFrom(Rates.list()) { ce, rs ->
            Converter.toExchange(rs, ce)
        }
        .compose(bindUntilEvent(FragmentEvent.DESTROY_VIEW))
        .subscribe(this::onExchange)
    }

    @Suppress("DEPRECATION")
    fun onExchange(exchange: Pair<String, String>) {

        fromApi(Build.VERSION_CODES.N) {
            @SuppressWarnings("deprecation")
            conversion1.text = fromHtml(exchange.first, Html.FROM_HTML_MODE_LEGACY)

            @SuppressWarnings("deprecation")
            conversion2.text = fromHtml(exchange.second, Html.FROM_HTML_MODE_LEGACY)
        }

        toApi(Build.VERSION_CODES.N, false) {
            conversion1.text = fromHtml(exchange.first)
            conversion2.text = fromHtml(exchange.second)
        }

        progressBar.visibility = View.GONE
    }

}
