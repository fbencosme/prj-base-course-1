package com.altice.eteco.course.basic

import android.os.Bundle
import android.support.v4.view.GravityCompat
import android.support.v7.app.ActionBarDrawerToggle
import android.support.v7.app.AppCompatActivity
import com.altice.eteco.course.basic.base.FragmentFactory

import com.jakewharton.rxbinding2.support.design.widget.itemSelections
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.rxkotlin.addTo

import kotlinx.android.synthetic.main.main_app_bar.*
import kotlinx.android.synthetic.main.main_activity.*

import java.util.concurrent.TimeUnit

class MainActivity : AppCompatActivity() {

    val disposables = CompositeDisposable()

    var actionBarDrawer: ActionBarDrawerToggle? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.main_activity)
        setSupportActionBar(toolbar)

        actionBarDrawer = ActionBarDrawerToggle(this, drawerLayout, toolbar, R.string.nav_drawer_open, R.string.nav_drawer_close)

        actionBarDrawer?.let {
            drawerLayout.addDrawerListener(it)
            it.syncState()
        }

        navView
            .itemSelections()
            .debounce (1, TimeUnit.SECONDS)
            .map { FragmentFactory.create(it.itemId) }
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(this::onSelected)
            .addTo(disposables)

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
        drawerLayout.closeDrawer(GravityCompat.START)
    }
}
