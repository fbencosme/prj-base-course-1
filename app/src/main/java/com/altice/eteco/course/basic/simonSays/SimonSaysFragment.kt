package com.altice.eteco.course.basic.simonSays

import android.media.MediaPlayer
import android.os.Bundle
import android.view.View
import com.altice.eteco.course.basic.BaseFragment
import com.altice.eteco.course.basic.R

class SimonSaysFragment : BaseFragment() {

    override val layoutRes: Int = R.layout.simon_says_fragment
    override val titleRes : Int = R.string.simonSays_title


    override fun onViewCreated(view: View?, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        R.raw.sound1
    }

    fun Item.play() {
        MediaPlayer.create(context, sound).start()
    }
}
