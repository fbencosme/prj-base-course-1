package com.altice.eteco.course.basic.base

import android.os.Bundle
import android.view.View

import com.altice.eteco.course.basic.BaseFragment
import com.altice.eteco.course.basic.R

import com.pawegio.kandroid.loadAnimation

import kotlinx.android.synthetic.main.welcome_fragment.*

class WelcomeFragment : BaseFragment() {

    override val layoutRes: Int = R.layout.welcome_fragment
    override val titleRes : Int = R.string.welcome_title

    override fun onViewCreated(view: View?, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        with(context.loadAnimation(R.anim.abc_slide_in_bottom)) {
            duration = 1000
            welcome.startAnimation(this)
        }

    }
}
