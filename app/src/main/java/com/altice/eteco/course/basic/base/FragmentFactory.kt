package com.altice.eteco.course.basic.base

import com.altice.eteco.course.basic.BaseFragment
import com.altice.eteco.course.basic.R
import com.altice.eteco.course.basic.age.AgeFragment
import com.altice.eteco.course.basic.connect4.Connect4Fragment
import com.altice.eteco.course.basic.exchange.ExchangeFragment
import com.altice.eteco.course.basic.loan.LoanFragment
import com.altice.eteco.course.basic.magic8Ball.Magic8BallFragment
import com.altice.eteco.course.basic.rnp.RPNFragment
import com.altice.eteco.course.basic.simonSays.SimonSaysFragment
import com.altice.eteco.course.basic.ticTacToe.TicTacToeFragment
import com.altice.eteco.course.basic.time.TimeFragment
import com.altice.eteco.course.basic.weight.WeightFragment

object FragmentFactory {

    fun create(id: Int): BaseFragment =
        when (id) {
            R.id.exchange   -> ExchangeFragment()
            R.id.loan       -> LoanFragment()
            R.id.age        -> AgeFragment()
            R.id.time       -> TimeFragment()
            R.id.ticTacToe  -> TicTacToeFragment()
            R.id.magic8ball -> Magic8BallFragment()
            R.id.simonSays  -> SimonSaysFragment()
            R.id.rpn        -> RPNFragment()
            R.id.weight     -> WeightFragment()
            R.id.connect4   -> Connect4Fragment()
            else            -> ExchangeFragment()
        }
}