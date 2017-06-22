package com.altice.eteco.course.basic.ticTacToe

import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.support.v7.widget.AppCompatTextView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView

import com.altice.eteco.course.basic.BaseFragment
import com.altice.eteco.course.basic.R
import com.jakewharton.rxbinding2.view.clicks
import io.reactivex.Observable.merge
import io.reactivex.rxkotlin.merge

import kotlinx.android.synthetic.main.tic_tac_toe_fragment.*


class TicTacToeFragment : BaseFragment() {

    override val layoutRes: Int = R.layout.tic_tac_toe_fragment
    override val titleRes : Int = R.string.ticTacToe_title

    var gridBtns : Iterable<TextView> = listOf()

    override fun onViewCreated(view: View?, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        gridBtns = (0 .. grid.childCount - 1).map {  grid.getChildAt(it) as TextView }
        val bg   = windowBackgroundColor()
        gridBtns.forEach { it.setBackgroundColor(bg) }

        val clicks = gridBtns .map { tv -> tv.clicks().map { tv } }.merge()
         .subscribe {
            it.text = if (it.id % 2 == 0) "O" else "X"
        }

    }

}
