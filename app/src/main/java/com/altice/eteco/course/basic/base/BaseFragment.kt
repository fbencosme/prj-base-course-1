package com.altice.eteco.course.basic

import android.os.Bundle
import android.support.annotation.LayoutRes
import android.support.annotation.StringRes
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup

import com.trello.rxlifecycle2.components.support.RxFragment

abstract class BaseFragment : RxFragment() {

    @LayoutRes
    open val layoutRes: Int = -1

    @StringRes
    open val titleRes: Int = -1

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?, savedInstanceState: Bundle?): View? =
        inflater?.inflate(layoutRes, container, false)

    fun windowBackgroundColor() : Int {
        val a = TypedValue()
        activity.theme.resolveAttribute(android.R.attr.windowBackground, a, true)
        if (a.type >= TypedValue.TYPE_FIRST_COLOR_INT && a.type <= TypedValue.TYPE_LAST_COLOR_INT)
            return a.data
        else
            return R.color.white
    }

}
