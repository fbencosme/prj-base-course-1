package com.altice.eteco.course.basic

import android.arch.lifecycle.Lifecycle
import android.arch.lifecycle.LifecycleActivity
import android.arch.lifecycle.LifecycleFragment
import android.os.Bundle
import android.os.PersistableBundle
import com.trello.lifecycle2.android.lifecycle.AndroidLifecycle
import com.trello.rxlifecycle2.LifecycleProvider
import com.trello.rxlifecycle2.RxLifecycle

abstract class BaseFragment : LifecycleFragment() {

    protected val provider: LifecycleProvider<Lifecycle.Event> by lazy {
        AndroidLifecycle.createLifecycleProvider(this)
    }
}
