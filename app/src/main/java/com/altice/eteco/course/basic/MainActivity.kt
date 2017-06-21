package com.altice.eteco.course.basic

import java.util.concurrent.TimeUnit

import android.os.Bundle
import android.support.v4.view.GravityCompat
import android.support.v7.app.ActionBarDrawerToggle
import android.view.Gravity

import com.altice.eteco.course.basic.base.FragmentFactory

import com.jakewharton.rxbinding2.support.design.widget.itemSelections

import com.trello.rxlifecycle2.android.ActivityEvent
import com.trello.rxlifecycle2.components.support.RxAppCompatActivity

import io.reactivex.android.schedulers.AndroidSchedulers

import kotlinx.android.synthetic.main.main_app_bar.*
import kotlinx.android.synthetic.main.main_activity.*

class MainActivity : RxAppCompatActivity() {

    var actionBarDrawer: ActionBarDrawerToggle? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.main_activity)
        setSupportActionBar(toolbar)

        actionBarDrawer = ActionBarDrawerToggle(this, drawerLayout, toolbar, R.string.nav_drawer_open, R.string.nav_drawer_close)

        actionBarDrawer?.let {
            drawerLayout.addDrawerListener(it)
            it.syncState()
            drawerLayout.openDrawer(Gravity.START)
        }

        navView
            .itemSelections()
            .map { it.itemId }
            .startWith(-1)
            .doOnNext {
                drawerLayout.closeDrawer(GravityCompat.START)
            }
            .throttleFirst (1, TimeUnit.SECONDS)
            .map (FragmentFactory::create)
            .delay(600, TimeUnit.MICROSECONDS)
            .observeOn(AndroidSchedulers.mainThread())
            .compose(bindUntilEvent(ActivityEvent.DESTROY))
            .subscribe(this::onSelected)
    }

    override fun onDestroy() {
        super.onDestroy()
        actionBarDrawer?.let { drawerLayout.removeDrawerListener(it) }
    }

    override fun onBackPressed() {
        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START)
        } else {
            super.onBackPressed()
        }
    }

    fun onSelected(f: BaseFragment) {
        toolbar.title = getString(f.titleRes)

        supportFragmentManager
            .beginTransaction()
            .replace(R.id.content_frame, f)
            .commitAllowingStateLoss()

    }
}
