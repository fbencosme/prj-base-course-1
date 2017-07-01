package com.altice.eteco.course.basic.base

import  android.support.v4.app.FragmentManager
import android.os.Bundle
import android.support.design.widget.BottomSheetDialogFragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.altice.eteco.course.basic.R
import io.reactivex.subjects.PublishSubject
import kotlinx.android.synthetic.main.date_time_bottom_sheet_dialog_fragment.*
import org.joda.time.DateTime

class DateTimeBottomSheetDialogFragment : BottomSheetDialogFragment() {

    val date = PublishSubject.create<DateTime>()

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?, savedInstanceState: Bundle?): View? =
        inflater?.inflate(R.layout.date_time_bottom_sheet_dialog_fragment, container, false)

    override fun onViewCreated(view: View?, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
    }

    fun open(fm: FragmentManager) {
        show(fm, "datetimesheet")
    }

}